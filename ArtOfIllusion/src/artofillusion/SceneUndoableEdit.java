/* Copyright (C) 2023 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package artofillusion;

/**
 *
 * @author MaksK
 */
public final class SceneUndoableEdit implements UndoableEdit {

    private Runnable redo;
    private Runnable undo;
    private String name;
    
    public SceneUndoableEdit(Runnable redo, Runnable undo) {
        this.redo = redo;
        this.undo = undo;
    }

    @Override
    public void redo() {
        redo.run();
    }
    
    @Override
    public void undo() {
        undo.run();
    }

    @Override
    public UndoableEdit setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
