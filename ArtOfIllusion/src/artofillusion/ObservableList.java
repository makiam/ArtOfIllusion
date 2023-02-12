/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
  public void addFirst(UndoRecord e)
  {
    super.addFirst(e); 
  }

  @Override
  public UndoRecord removeLast()
  {
    UndoRecord rec = super.removeLast(); 
    logger.log(Level.INFO , "Remove Last {0} from {1}", new Object[] {rec, name});
    return rec;
  }
  
}
