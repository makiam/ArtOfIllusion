/* Copyright (C) 2023 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package artofillusion;

import artofillusion.material.Material;
import artofillusion.material.MaterialMapping;
import artofillusion.object.ObjectInfo;

/**
 *
 * @author MaksK
 */
public class SetMaterialEdit implements UndoableEdit
{

  private final ObjectInfo object;
  private final Material oldMaterial;
  private final Material newMaterial;
  
  private final MaterialMapping oldMapping;
  private final MaterialMapping newMapping;
  
  public SetMaterialEdit(ObjectInfo target, Material material, MaterialMapping mapping) {
    this.object = target;
    this.oldMaterial = target.getObject().getMaterial();
    this.oldMapping = target.getObject().getMaterialMapping();
    this.newMaterial = material;
    this.newMapping = mapping;
    execute();
  }
  
  @Override
  public void undo()
  {
    object.setMaterial(oldMaterial, oldMapping);
  }

  @Override
  public void redo()
  {
    object.setMaterial(newMaterial, newMapping);
  }

  @Override
  public String getName()
  {
    return "Set Material";
  }
  
  
}
