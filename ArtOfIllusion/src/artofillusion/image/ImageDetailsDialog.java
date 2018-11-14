/* Copyright (C) 2017 by Petri Ihalainen
   Some methods copyright (C) by Peter Eastman
   Changes copyright (C) 2018 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY 
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
   
package artofillusion.image;

import artofillusion.*;
import artofillusion.texture.Texture;
import artofillusion.ui.*;
import java.awt.*;
import java.awt.image.*;
import java.util.List;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import buoy.event.*;
import buoy.widget.*;
import java.util.stream.Collectors;


public class ImageDetailsDialog extends BDialog
{
    private static final Color hilightTextColor = new Color(0, 191, 191);
    private static final Color errorTextColor = new Color(143, 0, 0);
    private static final LayoutInfo layout = new LayoutInfo(LayoutInfo.WEST, LayoutInfo.NONE);
    
    private final BFrame parent;
    private final Scene scene;
    private ImageMap im;

    
    private final BLabel imageField; 
    
     
    private BufferedImage canvasImage;
    private final BButton refreshButton, reconnectButton, convertButton, exportButton;
    private BLabel[] title, data; 
    private final Color defaultTextColor;
    private Color currentTextColor;

    public ImageDetailsDialog(BFrame parent, Scene scene, ImageMap im)
    {
        super(parent, "Image data", true);
        this.parent = parent;
        this.scene = scene;
        this.im = im;
   
        List<String> usedTextureNames = scene.getTextures().
                filter((Texture t) -> t.usesImage(im)).
                map((Texture t) -> t.getName()).
                collect(Collectors.toList());
        
        ColumnContainer fields = new ColumnContainer();
        setContent(fields);
        
        fields.add(imageField  = new BLabel() {
            @Override
            protected JLabel createComponent(String text, Icon image) {
                return new ImageLabel();
            }
            
        });
        FormContainer infoTable; 
        fields.add(infoTable = new FormContainer(2, 7 + usedTextureNames.size()));
        RowContainer buttonField = new RowContainer();
        fields.add(buttonField);
        
        Font boldFont = new BLabel().getFont().deriveFont(Font.BOLD);
        
        title = new BLabel[7];
        data = new BLabel[7 + usedTextureNames.size()];

        infoTable.add(title[0] = Translate.label("imageName",    ":"), 0, 0, layout);
        infoTable.add(title[1] = Translate.label("imageType",    ":"), 0, 1, layout);
        infoTable.add(title[2] = Translate.label("imageSize",    ":"), 0, 2, layout);
        infoTable.add(title[3] = Translate.label("imageLink",    ":"), 0, 3, layout);
        infoTable.add(title[4] = Translate.label("imageCreated", ":"), 0, 4, layout);
        infoTable.add(title[5] = Translate.label("imageEdited",  ":"), 0, 5, layout);
        infoTable.add(title[6] = Translate.label("imageUsedIn",  ":"), 0, 6, layout);
        
        for (int j = 0; j < 7; j++)
        {
            infoTable.add(data[j]  = new BLabel(), 1, j, layout);
            title[j].setFont(boldFont);
        }
        
        for (int q = 0; q < usedTextureNames.size(); q++)
        {
            infoTable.add(data[q+7] = new BLabel(usedTextureNames.get(q)), 1, 6+q, layout);
        }

        paintImage();
        
        
        buttonField.add(refreshButton = Translate.button("refreshImage", this, "refreshImage")); 
        buttonField.add(reconnectButton = Translate.button("reconnectImage", "...", this, "reconnectImage")); 
        buttonField.add(convertButton = Translate.button("convertImage", this, "convertToLocal"));
        buttonField.add(exportButton = Translate.button("exportImage", "...", this, "exportImage"));
        buttonField.add(Translate.button("ok", this, "closeDetailsDialog"));
        
        if (im instanceof ExternalImage)
            exportButton.setEnabled(false);
        else
        {
            refreshButton.setEnabled(false);
            reconnectButton.setEnabled(false);
            convertButton.setEnabled(false);
        }
        
        defaultTextColor = currentTextColor = UIManager.getColor("Label.foreground");

        
        data[0].addEventLink(MouseClickedEvent.class, this, "nameClicked");
        data[0].addEventLink(MouseEnteredEvent.class, this, "nameEntered");
        data[0].addEventLink(MouseExitedEvent.class,  this, "nameExited");
        title[0].addEventLink(MouseClickedEvent.class, this, "nameClicked");
        title[0].addEventLink(MouseEnteredEvent.class, this, "nameEntered");
        title[0].addEventLink(MouseExitedEvent.class,  this, "nameExited");
        
        addAsListener();
        addEventLink(WindowClosingEvent.class, this, "closeDetailsDialog");
        setDataTexts();
        pack();
        setResizable(false);

        UIUtilities.centerDialog(this, parent);
        setVisible(true);
    }

    private void setDataTexts()
    {
        
        for (int d = 0; d < 7; d++)
        {
            data[d].setText("");
            if (im instanceof ExternalImage && !((ExternalImage)im).isConnected())
                currentTextColor = errorTextColor;
            else
                 currentTextColor = defaultTextColor;
            data[d].getComponent().setForeground(currentTextColor);
        }
    
        data[0].setText(im.getName());
        data[1].setText(im.getType());
        data[2].setText(im.getWidth() + " x " + im.getHeight());
        if (im instanceof ExternalImage)
            data[3].setText(((ExternalImage)im).getPath());
        if (!im.getUserCreated().isEmpty())
            data[4].setText(im.getUserCreated() + " - " + im.getDateCreated() + " - " + im.getZoneCreated());
        if (!im.getUserEdited().isEmpty())            
            data[5].setText(im.getUserEdited() + " - " + im.getDateEdited() + " - " + im.getZoneEdited());
    }

    private static BufferedImage createBackground()
    {
        int tone = 185;
        Color bgColorDark = new Color(tone, tone, tone);
        tone = 219;
        Color bgColorLight = new Color(tone, tone, tone);

        BufferedImage ci = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = ci.createGraphics();
        graphics.setBackground(bgColorDark);
        graphics.clearRect(0, 0, 600, 600);
        graphics.setColor(bgColorLight);
        for (int x = 0; x < 61; x++) {
            for (int y = 0; y < 60; y++) {
                if ((x % 2) != (y % 2)) {
                    graphics.fillRect(x * 10, y * 10, 10, 10);
                }
            }
        }
        return ci;
    }
    
    private void paintImage()
    {
        try
        {
            
            Graphics2D g = (Graphics2D)canvasImage.createGraphics();
            Image image = im.getPreview(600);
            if (image == null)
                return;
            int xOffset = (600-image.getWidth(null))/2;
            int yOffset = (600-image.getHeight(null))/2;
            g.drawImage(image, xOffset, yOffset, null);
            imageField.setIcon(new ImageIcon(canvasImage));
        }
        catch (Exception e)
        {
            // "What could possibly go wrong?" :)
        }
    }

    private void refreshImage()
    {
        if(refreshButton.isEnabled())
        {
            ((ExternalImage)im).refreshImage();

            paintImage();
            setDataTexts();   
        }
    }

    private void reconnectImage()
    {
        if (! reconnectButton.isEnabled())
            return;
            
        BFileChooser fc = new ImageFileChooser(Translate.text("selectImageToLink"));
        fc.setMultipleSelectionEnabled(false);
        if (!fc.showDialog(this))
            return;
        File file = fc.getSelectedFile();
        
        try
        {
            Scene sc = null;
            if (parent instanceof EditingWindow)
                sc = ((EditingWindow)parent).getScene();
            ((ExternalImage)im).reconnectImage(file, sc);
            
            paintImage();
            setDataTexts();            
            if (parent instanceof EditingWindow)
                ((EditingWindow)parent).setModified();
        }
        catch(Exception e)
        {
            new BStandardDialog("", Translate.text("errorLoadingImage " + file.getName()), BStandardDialog.ERROR).showMessageDialog(this);
        }
    }

    private void exportImage()
    {
        if (! exportButton.isEnabled())
            return;

        String ext = ".png";
        if (im instanceof SVGImage)
            ext = ".svg";
        if (im instanceof HDRImage)
            ext = ".hdr";

        BFileChooser chooser = new BFileChooser(BFileChooser.SAVE_FILE, Translate.text("exportImage"));
        
        String imageName = im.getName();
        if (imageName.isEmpty())
          imageName = Translate.text("unTitled");
        chooser.setSelectedFile(new File(imageName+ext));
        if (!chooser.showDialog(this))
            return;

        // Make sure the file extension is correct

        String fileName = chooser.getSelectedFile().getName();
        if (!fileName.toLowerCase().endsWith(ext))
            fileName = fileName+ext;
        File imageFile = new File(chooser.getDirectory(), fileName);
        
        // Check if the file already exist and the user wants to overwrite it.
        
        if (imageFile.isFile())
        {
            String options[] = new String [] {Translate.text("Yes"), Translate.text("No")};
            int choice = new BStandardDialog("", Translate.text("overwriteFile", fileName), BStandardDialog.QUESTION).showOptionDialog(this, options, options[1]);
            if (choice == 1)
            return;
        }
        
        // Write the file
        try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile)))
        {
            if(im instanceof HDRImage)
            {                
                HDREncoder.writeImage((HDRImage)im, out);
            }
            else if(im instanceof SVGImage)
            {
                out.write(((SVGImage)im).getXML());
            }
            else // MIPMappedImage
            {
                ImageIO.write(((MIPMappedImage)im).getImage(), "png", out); // getImage returns BufferedImage
            }
        }
        catch (Exception ex)
        {
            setCursor(Cursor.getDefaultCursor());
            new BStandardDialog("", Translate.text("errorExportingImage", im.getName()), BStandardDialog.ERROR).showMessageDialog(this);
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("empty-statement")
    private void convertToLocal()
    {
        if (! convertButton.isEnabled())
            return;

        String name = im.getName();
        if (name.isEmpty());
            name = Translate.text("unNamed");

        if (confirmConvert(name))
        {
            int s;
            for (s = 0; s < scene.getNumImages() && scene.getImage(s) != im; s++);

            ((ExternalImage)im).getImageMap().setName(im.getName());
            im = ((ExternalImage)im).getImageMap();
            scene.replaceImage(s, im);

            exportButton.setEnabled(true);
            refreshButton.setEnabled(false);
            reconnectButton.setEnabled(false);
            convertButton.setEnabled(false);
            setDataTexts();
        }
    }
    
    private boolean confirmConvert(String name)
    {
      String convertTitle = Translate.text("confirmTitle");
      String question = Translate.text("convertQuestion", name);

      BStandardDialog confirm = new BStandardDialog(convertTitle, question, BStandardDialog.QUESTION);
      String[] options = new String[]{Translate.text("Yes"), Translate.text("No")};
      return (confirm.showOptionDialog(this, options, options[1]) == 0);
    }

    private void closeDetailsDialog()
    {
      dispose();
      removeAsListener();
    }

    
    /** Pressing Return and Escape are equivalent to clicking OK and Cancel. */
     
    private void keyPressed(KeyPressedEvent ev)
    {
        int code = ev.getKeyCode();
        if (code == KeyPressedEvent.VK_ESCAPE)
            closeDetailsDialog();
    }
  
    /** Add keyboard event listener to every Widget. */
    
    private void addAsListener()
    {        
        addEventLink(KeyPressedEvent.class, this, "keyPressed");
        for (Widget cw : UIUtilities.findAllChildren(this))
            cw.addEventLink(KeyPressedEvent.class, this, "keyPressed");
    }
    
    /** Remove keyboard listener before returning. */
    
    private void removeAsListener()
    {
        removeEventLink(KeyPressedEvent.class, this);
        for (Widget cw : UIUtilities.findAllChildren(this))
            cw.removeEventLink(KeyPressedEvent.class, this);
    }
    
    private void nameEntered()
    {
        title[0].getComponent().setForeground(hilightTextColor);
        data[0].getComponent().setForeground(hilightTextColor);
    }
    
    private void nameExited()
    {        
        title[0].getComponent().setForeground(defaultTextColor);
        data[0].getComponent().setForeground(currentTextColor);
    }
    
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    private void nameClicked()
    {
        new ImageNameEditor(im, this);
    }

    private class ImageLabel extends JLabel
    {
        private final BufferedImage canvasImage = ImageDetailsDialog.createBackground();
        private final Dimension pf = new Dimension(600, 600);
        
        @Override
        public Dimension getPreferredSize() {
            return pf;
        }

        @Override
        protected void paintComponent(Graphics graph) {
            graph.drawImage(canvasImage, 0, 0, null);
            ImageIcon icon = (ImageIcon)getIcon();
            if(null == icon) return;
            graph.drawImage(icon.getImage(), 0, 0, null);
        }
    }
    /** Dialog for setting the name of the image */

    private class ImageNameEditor extends BDialog
    {
        private ColumnContainer content;
        private BTextField nameField;
        private BCheckBox autoBox;

        private String autoText, userText;
        private boolean automatic = false;
        
        private ImageNameEditor(ImageMap im, WindowWidget parent)
        {
            setTitle(Translate.text("nameDialogTitle"));
            content = new ColumnContainer();
            RowContainer buttons = new RowContainer();
            setContent(content);
            content.add(nameField = new BTextField(im.getName()));
            autoText = userText = im.getName();
            
            if (im instanceof ExternalImage)
            {
                try 
                {
                    String fileName = im.getFile().getName();
                    autoText = fileName.substring(0, fileName.lastIndexOf('.'));
                }
                catch (Exception e)
                {
                    // Just display the saved name
                }
                automatic = ((ExternalImage)im).isNameAutomatic();
                content.add(autoBox = new BCheckBox(Translate.text("Automatic"), automatic));
                autoBox.addEventLink(ValueChangedEvent.class, this , "autoChanged");
                autoChanged();
            }
            nameField.setColumns(50);
            nameField.addEventLink(ValueChangedEvent.class, this, "textChanged");
            content.add(buttons);
            buttons.add(Translate.button("ok", this, "okNameEditor"));
            buttons.add(Translate.button("cancel", this, "dispose"));
            addEventLink(WindowClosingEvent.class, this, "dispose");
            addAsListener();
            layoutChildren();            
            pack();
            setResizable(false);
            setModal(true); // I wonder, why this dialog requres setModal() and the other don't.
            
            Rectangle pb = parent.getBounds();
            Rectangle tb = getBounds();
            getComponent().setLocation(pb.x + (pb.width-tb.width)/2, pb.y + (625-tb.height));
            
            setVisible(true);
        }

        private void textChanged()
        {
            if(automatic) return;
            userText = nameField.getText();
        }
        
        private void autoChanged()
        {
            automatic = autoBox.getState();
            nameField.setEnabled(! automatic);
            if (automatic)
                nameField.setText(autoText);
            else
                nameField.setText(userText);
        }
        
        private void cancelNameEditor()
        {
            dispose();
            removeAsListener();
        }
        
        private void okNameEditor()
        {
            if (automatic)
                im.setName(autoText);
            else
                im.setName(userText);
            if (im instanceof ExternalImage)
                ((ExternalImage)im).setNameAutomatic(automatic);
            im.setDataEdited();
            setDataTexts();
            dispose();
            removeAsListener();
        }
        
        /** Pressing Return and Escape are equivalent to clicking OK and Cancel. */
        
        private void keyPressed(KeyPressedEvent ev)
        {
            int code = ev.getKeyCode();
            if (code == KeyPressedEvent.VK_ESCAPE)
                cancelNameEditor();
            if (code == KeyPressedEvent.VK_ENTER)
                okNameEditor();
        }
    
        /** Add keyboard event listener to every Widget. */

        private void addAsListener()
        {        
            addEventLink(KeyPressedEvent.class, this, "keyPressed");
            for (Widget cw : UIUtilities.findAllChildren(this))
                cw.addEventLink(KeyPressedEvent.class, this, "keyPressed");
        }
        
        /** Remove keyboard listener before returning. */

        private void removeAsListener()
        {
            removeEventLink(KeyPressedEvent.class, this);
            for (Widget cw : UIUtilities.findAllChildren(this))
                cw.removeEventLink(KeyPressedEvent.class, this);
        }
    }
}
