/* Copyright (C) 1999-2006 by Peter Eastman
   Modification copyright (C) 2016 by Petri Ihalainen
   Changes copyright (C) 2019 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package artofillusion.ui;

import buoy.event.*;
import buoy.widget.*;
import java.awt.*;
import artofillusion.*;
import java.util.ArrayList;
import java.util.List;

/** A ToolPalette is drawn as a grid of images, one for each EditingTool that is added to
    the palette.  It allows a single tool to be selected at any time. */

public class ToolPalette extends CustomWidget
{
  private int width, height, selected, lastSelected;
  
  private List<EditingTool> tools;
  
  private EditingTool defaultTool;
  private EditingTool selectedTool;
  
  private Dimension maxsize;
  private EditingWindow window;

  /** Create a new ToolPalette without the information of the parent window */

  public ToolPalette(int w, int h)
  {
	this(w, h, null);
  }
  /** Create a new ToolPalette.  w and h give the width and height of the tool palette,
      measured in icons. */

  public ToolPalette(int w, int h, EditingWindow win)
  {
    width = w;
    height = h;
    window = win;
    
    tools = new ArrayList<>(w * h);

    selected = 0;
    defaultTool = null;
    selectedTool = null;
    
    maxsize = new Dimension(0, 0);
    addEventLink(MousePressedEvent.class, this, "mousePressed");
    addEventLink(MouseClickedEvent.class, this, "mouseClicked");
    addEventLink(MouseEnteredEvent.class, this, "mouseEntered");
    addEventLink(MouseExitedEvent.class, this, "mouseExited");
    addEventLink(MouseMovedEvent.class, this, "mouseMoved");
    addEventLink(RepaintEvent.class, this, "paint");
    addEventLink(ToolTipEvent.class, this, "showToolTip");
    setBackground(ThemeManager.getPaletteBackgroundColor());
  }

  /** Add a new tool. */

  public void addTool(EditingTool tool)
  {
    addTool(tools.size(), tool);
  }

  /** Add a new tool. */

  public void addTool(int position, EditingTool t)
  {
    if (numTools == tool.length)
    {
      // We need to extend the palette.

      height++;
      EditingTool newTool[] = new EditingTool [width*height];
      System.arraycopy(tool, 0, newTool, 0, tool.length);
      tool = newTool;
      invalidateSize();
    }
    for (int i = numTools; i > position; i--)
      tool[i] = tool[i-1];
    tool[position] = t;
    numTools++;
    int buttonMargin = ThemeManager.getButtonMargin();
    int paletteMargin = ThemeManager.getPaletteMargin();
    int w = t.getButton().getWidth() + 2 * buttonMargin;
    int h = t.getButton().getHeight() + 2 * buttonMargin;
    if (w > maxsize.width)
      maxsize.width = w;
    if (h > maxsize.height)
      maxsize.height = h;
    for (int i = 0; i < numTools; i++)
      tool[i].getButton().setPosition((i % width) * maxsize.width + paletteMargin + buttonMargin, (i / width) * maxsize.height + paletteMargin + buttonMargin);
    if (numTools == 1)
      t.activate();
  }

  /** Get the number of tools in palette. */

  public int getNumTools()
  {
    return tools.size();
  }

  /** Get a tool by index. */

  public EditingTool getTool(int index)
  {
    return tools.get(index);
  }

  /** Get the default tool. */

  public EditingTool getDefaultTool()
  {
    return defaultTool;
  }

  /** Set the default tool. */

  public void setDefaultTool(EditingTool t)
  {
    defaultTool = t;
  }

  /** Return the number of the currently selected tool. */

  public int getSelection()
  {
    return selected;
  }

  /** Return the currently selected tool. */

  public EditingTool getSelectedTool()
  {
    return tools.get(selected);
  }

  private void paint(RepaintEvent ev)
  {
    Graphics2D g = ev.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int paletteMargin = ThemeManager.getPaletteMargin();
    g.setColor(ThemeManager.getPaletteBackgroundColor());
    g.fillRoundRect(0, 0, width * maxsize.width + 2 * paletteMargin, height * maxsize.height + 2 * paletteMargin, 8, 8);

    for(EditingTool tool: tools) tool.getButton().paint(g);

  }

  private void showToolTip(ToolTipEvent ev)
  {
    int i = findClickedTool(ev.getPoint());
    if (i > -1 && i < numTools)
    {
      String text = tool[i].getToolTipText();
      if (text == null)
        BToolTip.hide();
      else
        new BToolTip(text).processEvent(ev);
    }
  }

  @Override
  public Dimension getPreferredSize()
  {
    int paletteMargin = ThemeManager.getPaletteMargin() * 2;
    return new Dimension(width * maxsize.width + paletteMargin, height * maxsize.height + paletteMargin);
  }

  @Override
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  private void mousePressed(MousePressedEvent e)
  {
    int i = findClickedTool(e.getPoint());
    if (i > -1 && i < numTools && i != selected)
    {
      if (selected < tool.length)
        tool[selected].deactivate();
      selected = lastSelected = i;
      repaint();
      tool[i].activate();
    }
    
    // This is a bit ugly. The 'toolChanged' should be defined in EditingWindow but that 
    // causes error at compile with Score and UVMappingEditorWindow
    if(null == window) return;

    if (window instanceof LayoutWindow || window instanceof ObjectEditorWindow)
      ((LayoutWindow)window).toolChanged(tool[i]);


  }

  private void mouseClicked(MouseClickedEvent e)
  {
    int i = findClickedTool(e.getPoint());
    if (i > -1 && i < numTools && e.getClickCount() == 2)
      tool[i].iconDoubleClicked();
  }

  private void mouseEntered(MouseEnteredEvent ev)
  {
    int t = findClickedTool(ev.getPoint());
    for (int i = 0; i < numTools; i++)
      tool[i].getButton().setHighlighted(t == i);
    repaint();
  }

  private void mouseExited()
  {
    for(EditingTool tool: tools)
      tool.getButton().setHighlighted(false);
    repaint();
  }

  private void mouseMoved(MouseMovedEvent ev)
  {
    EditingTool tool = find
    int t = findClickedTool(ev.getPoint());
    for (int i = 0; i < numTools; i++)
      tool[i].getButton().setHighlighted(t == i);
    repaint();
  }

  private EditingTool findClickedToolByPoint(Point point) {
    Rectangle r = new Rectangle();
    for(EditingTool tool: tools) 
    {
      ToolButton tb = tool.getButton();      
      r.setBounds(tb.getPosition().x, tb.getPosition().y, tb.getWidth(), tb.getHeight());
      if(r.contains(point)) return tool;
    }

    return null;
  }
  
  private int findClickedTool(Point point)
  {
    Rectangle r = new Rectangle();
    for(EditingTool tool: tools) 
    {
      ToolButton tb = tool.getButton();      
      r.setBounds(tb.getPosition().x, tb.getPosition().y, tb.getWidth(), tb.getHeight());
      if(r.contains(point)) return tools.indexOf(tool);
    }
    return -1;
  }

  /** Change the currently selected tool. */

  public void selectTool(EditingTool which)
  {
    selectToolInternal(which);
    lastSelected = selected;
  }

  /** This is used internally to actually change the selected tool. */

  private void selectToolInternal(EditingTool which)
  {
    for (int i = 0; i < numTools; i++)
      if (tool[i] == which)
      {
        tool[selected].deactivate();
        selected = i;
        repaint();
        tool[i].activate();
      }
  }

  /** Allow the user to change tools with the keyboard. */

  public void keyPressed(KeyPressedEvent ev)
  {
    int code = ev.getKeyCode();
    int newtool;

    if (code == KeyPressedEvent.VK_LEFT)
      newtool = selected-1;
    else if (code == KeyPressedEvent.VK_RIGHT)
      newtool = selected+1;
    else if (code == KeyPressedEvent.VK_UP)
      newtool = selected-width;
    else if (code == KeyPressedEvent.VK_DOWN)
      newtool = selected+width;
    else
      return;
    if (newtool < 0)
      newtool += numTools;
    if (newtool >= numTools)
      newtool -= numTools;
    tool[selected].deactivate();
    selected = lastSelected = newtool;
    repaint();
    tool[selected].activate();
  }

  /** Calling this method will toggle between the default tool and the last tool which was
      explicitly selected. */

  public void toggleDefaultTool()
  {
    selectToolInternal(tool[selected == lastSelected ? defaultTool : lastSelected]);
  }
}