/* Copyright (C) 2023 by Maksim Khramov

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package artofillusion;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 *
 * @author MaksK
 */
public final class CompoundEdit implements UndoableEdit {

    private final List<UndoableEdit> edits = new ArrayList<>();
    private Optional<String> name  = Optional.empty();
    
    public CompoundEdit add(UndoableEdit edit) {
        edits.add(edit);
        return this;
    }
    
    @Override
    public void undo() {
        ListIterator<UndoableEdit> li = edits.listIterator(edits.size());
        
        while(li.hasPrevious()) {
            li.previous().undo();
        }
    }

    @Override
    public void redo() {
        edits.forEach(edit -> edit.redo());
    }

    @Override
    public String getName() {
        return name.orElse(edits.isEmpty() ? "" : edits.get(0).getName());
    }

    @Override
    public CompoundEdit setName(String name) {
        this.name = Optional.ofNullable(name);
        return this;
    }
    
    
}
