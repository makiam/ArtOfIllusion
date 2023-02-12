/* Copyright (C) 2023 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
package artofillusion;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MaksK
 */
public class ObservableList extends LinkedList<UndoRecord>
{
  private static final Logger logger = Logger.getLogger(ObservableList.class.getName());

  private final String name;
  public ObservableList(String name)
  {
    this.name = name;
  }
  
  @Override
  public void add(int index, UndoRecord element)
  {
    super.add(index, element);
    logger.log(Level.INFO , "Add {1} at {0} to {2}", new Object[] {index, element, name});
  }

  @Override
  public void addFirst(UndoRecord element)
  {
    super.addFirst(element); 
    logger.log(Level.INFO , "Add first {0} to {1}", new Object[] {element, name});
  }

  @Override
  public UndoRecord removeLast()
  {
    UndoRecord rec = super.removeLast(); 
    logger.log(Level.INFO , "Remove Last {0} from {1} left {2}", new Object[] {rec, name, this.size()});
    return rec;
  }

  @Override
  public UndoRecord removeFirst()
  {
    UndoRecord rec = super.removeFirst();
    logger.log(Level.INFO , "Remove First {0} from {1} left {2}", new Object[] {rec, name, this.size()});
    return rec;
  }
  
  
}
