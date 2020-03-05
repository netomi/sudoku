/*
 * Sudoku creator / solver / teacher.
 *
 * Copyright (c) 2020 Thomas Neidhart
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.netomi.sudoku.solver;

import org.netomi.sudoku.model.Grid;

import java.util.Objects;

public abstract class Hint {

    private final Grid.Type        type;
    private final SolvingTechnique solvingTechnique;

    protected Hint(Grid.Type type, SolvingTechnique solvingTechnique) {
        this.type             = type;
        this.solvingTechnique = solvingTechnique;
    }

    public Grid.Type getGridType() {
        return type;
    }

    public SolvingTechnique getSolvingTechnique() {
        return solvingTechnique;
    }

    public abstract void apply(Grid targetGrid, boolean updateGrid);

    @Override
    public int hashCode() {
        return Objects.hash(type, solvingTechnique);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hint hint = (Hint) o;
        return Objects.equals(type, hint.type) &&
               solvingTechnique == hint.solvingTechnique;
    }
}
