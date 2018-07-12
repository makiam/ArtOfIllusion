/* Copyright (C) 2000-2004 by Peter Eastman
   Changes copyright (C) 2018 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY 
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package artofillusion.procedural;

import artofillusion.*;
import artofillusion.math.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/** This represents a procedure for calculating a set of values (typically, the parameters
    for a texture or material). */

public class Procedure
{
  OutputModule output[];
  private List<Module> module;
  Link link[];
  
  public Procedure(OutputModule output[])
  {
    this.output = output;
    module = new ArrayList<>(0);
    link = new Link [0];
  }
  
  /** Get the list of output modules. */
  
  public OutputModule [] getOutputModules()
  {
    return output;
  }
  
  /** Get the list of all other modules. */
  
  public Module [] getModules()
  {
    return module.toArray(new Module[0]);
  }
  
  /** Get the index of a particular module. */
  
  public int getModuleIndex(Module mod)
  {
      return module.indexOf(mod);
  }
  
  /** Get the index of a particular output module. */
  
  public int getOutputIndex(Module mod)
  {
    for (int i = 0; i < output.length; i++)
      if (output[i] == mod)
        return i;
    return -1;
  }

  /** Add a module to the procedure. */
  
  public void addModule(Module mod)
  {
      module.add(mod);
  }
  
  /** Delete a module from the procedure.  Any links involving this module should be deleted
      *before* calling this method. */

  public void deleteModule(int which)
  {
      module.remove(which);
  }

  /** Get the list of links between modules. */
  
  public Link [] getLinks()
  {
    return link;
  }
  
  /** Add a link to the procedure. */
  
  public void addLink(Link ln)
  {
    Link newlink[] = new Link [link.length+1];
    for (int i = 0; i < link.length; i++)
      newlink[i] = link[i];
    newlink[link.length] = ln;
    link = newlink;
    ln.to.getModule().setInput(ln.to, ln.from);
  }
  
  /** Delete a link from the procedure. */
  
  public void deleteLink(int which)
  {
    Link newlink[] = new Link [link.length-1];
    int i, j;

    if (link[which].to.getType() == IOPort.INPUT)
      link[which].to.getModule().setInput(link[which].to, null);
    else
      link[which].from.getModule().setInput(link[which].from, null);
    for (i = 0, j = 0; i < link.length; i++)
      if (i != which)
        newlink[j++] = link[i];
    link = newlink;
  }
  
  /** Check for feedback loops in this procedure. */
  
  public boolean checkFeedback()
  {
      for (OutputModule output1 : output) {
          for (OutputModule item : output) {
              item.checked = false;
          }
          for (Module item : module) {
              item.checked = false;
          }
          if (output1.checkFeedback()) {
              return true;
          }
      }
    return false;
  }
  
  /** This routine is called before the procedure is evaluated.  The PointInfo object 
      describes the point for which it is to be evaluated. */
  
  public void initForPoint(PointInfo p)
  {
    for (Module item : module) {
        item.init(p);
    }
  }
  
  /** This routine returns the value of the specified output module.  If that output does
      not have value type NUMBER, the results are undefined. */
  
  public double getOutputValue(int which)
  {
    return output[which].getAverageValue(0, 0.0);
  }
  
  /** This routine returns the gradient of the specified output module.  If that output does
      not have value type NUMBER, the results are undefined. */
  
  public void getOutputGradient(int which, Vec3 grad)
  {
    output[which].getValueGradient(0, grad, 0.0);
  }
  
  /** This routine returns the color of the specified output module.  If that output does
      not have value type COLOR, the results are undefined. */
  
  public void getOutputColor(int which, RGBColor color)
  {
    output[which].getColor(0, color, 0.0);
  }
  
  /** Make this procedure identical to another one.  The output modules must already
      be set up before calling this method. */
  
  public void copy(Procedure proc)
  {
    module = new ArrayList<>();
    for (Module item: proc.module)
      module.add(item.duplicate());
    
    link = new Link [proc.link.length];
    for (int i = 0; i < link.length; i++)
      {
        Module fromModule = proc.link[i].from.getModule();
        Module toModule = proc.link[i].to.getModule();
        int fromIndex = proc.getModuleIndex(fromModule);
        int toIndex = toModule instanceof OutputModule ? proc.getOutputIndex(toModule) : proc.getModuleIndex(toModule);
        IOPort from = module.get(fromIndex).getOutputPorts()[proc.module.get(fromIndex).getOutputIndex(proc.link[i].from)];
        IOPort to = toModule instanceof OutputModule ? 
                output[toIndex].getInputPorts()[proc.output[toIndex].getInputIndex(proc.link[i].to)] :
                module.get(toIndex).getInputPorts()[proc.module.get(toIndex).getInputIndex(proc.link[i].to)];
        link[i] = new Link(from, to);
        to.getModule().setInput(to, from);
      }
  }
  
  /** Write this procedure to an output stream. */
  
  public void writeToStream(DataOutputStream out, Scene theScene) throws IOException
  {
    out.writeShort(0);
    out.writeInt(module.size());
    for (Module item : module) {
        out.writeUTF(item.getClass().getName());
        out.writeInt(item.getBounds().x);
        out.writeInt(item.getBounds().y);
        item.writeToStream(out, theScene);
    }
    
    out.writeInt(link.length);
    
    for (Link item : link) {
        out.writeInt(getModuleIndex(item.from.getModule()));
        out.writeInt(item.from.getModule().getOutputIndex(item.from));
        if (item.to.getModule() instanceof OutputModule) {
            out.writeInt(-getOutputIndex(item.to.getModule()) - 1);
        } else {
            out.writeInt(getModuleIndex(item.to.getModule()));
            out.writeInt(item.to.getModule().getInputIndex(item.to));
        }
    }
  }
  
  /** Reconstruct this procedure from an input stream.  The output modules must already
      be set up before calling this method. */

  public void readFromStream(DataInputStream in, Scene theScene) throws IOException, InvalidObjectException
  {
    short version = in.readShort();
    
    if (version != 0) throw new InvalidObjectException("");
    for (OutputModule item : output) {
        item.setInput(item.getInputPorts()[0], null);
    }
    
    int pm = in.readInt();
    module = new ArrayList<>();
    try
    {
      for (int i = 0; i < pm; i++)
        {
          String classname = in.readUTF();
          Point p = new Point(in.readInt(), in.readInt());
          Class cls = ArtOfIllusion.getClass(classname);
          Constructor con = cls.getConstructor(new Class [] {Point.class});
          Module item = (Module) con.newInstance(new Object [] {p});
          item.readFromStream(in, theScene);
          module.add(item);
        }
    }
    catch (InvocationTargetException ex)
    {
      ex.getTargetException().printStackTrace();
      throw new IOException();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw new IOException();
    }
    link = new Link [in.readInt()];
    for (int i = 0; i < link.length; i++)
      {
        IOPort to, from = module.get(in.readInt()).getOutputPorts()[in.readInt()];
        int j = in.readInt();
        if (j < 0)
          to = output[-j-1].getInputPorts()[0];
        else
          to = module.get(j).getInputPorts()[in.readInt()];
        link[i] = new Link(from, to);
        to.getModule().setInput(to, from);
      }
  }
}