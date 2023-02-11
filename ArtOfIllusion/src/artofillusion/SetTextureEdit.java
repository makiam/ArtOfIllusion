/* Copyright (C) 2023 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package artofillusion;

import artofillusion.object.ObjectInfo;
import artofillusion.texture.Texture;
import artofillusion.texture.TextureMapping;

/**
 *
 * @author MaksK
 */
public class SetTextureEdit implements UndoableEdit
{

  private final ObjectInfo item;
  private final TextureMapping oldMapping;
  private final Texture oldTexture;
  private final Texture newTexture;
  
  
  public SetTextureEdit(ObjectInfo target, Texture texture) {
    this.item = target;
    newTexture = texture;
    oldTexture = target.getObject().getTexture();
    oldMapping = target.getObject().getTextureMapping();
    execute();
  }
  
  @Override
  public void undo()
  {
    item.setTexture(oldTexture, oldMapping);
  }

  @Override
  public void redo()
  {
    item.setTexture(newTexture, newTexture.getDefaultMapping(item.getObject()));
  }

  @Override
  public String getName()
  {
    return "Set Texture";
  }
  
  
}
