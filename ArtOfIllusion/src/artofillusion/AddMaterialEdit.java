/* Copyright (C) 2023 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
package artofillusion;

import artofillusion.material.Material;

/**
 *
 * @author MaksK
 */
public class AddMaterialEdit implements UndoableEdit
{

  private final Scene scene;
  private final Material material;
  private final int index;
  
  public AddMaterialEdit(Scene scene, Material material, int index)
  {
    this.scene = scene;
    this.material = material;
    this.index = index;
  }
  @Override
  public void undo()
  {
    scene.removeMaterial(index);
  }

  @Override
  public void redo()
  {
    scene.addMaterial(material, index);
  }

  @Override
  public String getName()
  {
    return "Add Material";
  }
  
}
