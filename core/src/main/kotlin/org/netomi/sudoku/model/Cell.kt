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
package org.netomi.sudoku.model

import java.util.*

/**
 * Represents a cell in a sudoku grid.
 */
class Cell internal constructor(val owner: Grid, val cellIndex: Int, val rowIndex: Int, val columnIndex: Int, val blockIndex: Int)
{
    private val _peers: MutableCellSet = MutableCellSet.empty(owner)
    val peers: CellSet
        get() = _peers.asCellSet()

    private var _value : Int = 0
    var value: Int
        get() = _value
        /**
         * Assigns the given value to the current cell.
         * Calling this method will update the internal state of the grid.
         *
         * @param value the value to assign this cell to
         * @throws IllegalArgumentException if the value is outside the allowed range [0, gridSize]
         * @throws IllegalStateException if the cell contains a given value (see [.isGiven])
         */
        set(value) {
            require(!(value < 0 || value > owner.gridSize)) {
                "invalid value for cell: " + value + " outside allowed range [0," + owner.gridSize + "]"
            }
            setValue(value, true)
        }

    /**
     * Indicates whether the cell has a fixed value.
     */
    var isGiven: Boolean = false

    internal var _possibleValues: MutableValueSet = MutableValueSet.fullySet(owner)
    val possibleValues: ValueSet
        get() {
            owner.throwIfStateIsInvalid()
            return _possibleValues.asValueSet()
        }

    private val _excludedValues: MutableValueSet = MutableValueSet.empty(owner)
    val excludedValues : ValueSet
        get() = _excludedValues.asValueSet()

    /**
     * Returns the [Row] this cell belongs to.
     */
    val row: Row
        get() = owner.getRow(rowIndex)

    /**
     * Returns the [Column] this cell belongs to.
     */
    val column: Column
        get() = owner.getColumn(columnIndex)

    /**
     * Returns the [Block] this cell belongs to.
     */
    val block: Block
        get() = owner.getBlock(blockIndex)

    /**
     * Returns whether a value has been assigned to this cell.
     */
    val isAssigned: Boolean
        get() = value > 0

    internal fun addPeers(cells: CellSet) {
        _peers.or(cells)
        _peers.clear(cellIndex)
    }

    /**
     * Returns a [Sequence] containing all cell that are visible from
     * this cell, i.e. are contained in the same row, column or block.
     */
    fun allPeers(): Sequence<Cell> {
        return _peers.allCells(owner)
    }

    /**
     * Returns the name of this cell in format rXcY, where X and Y are
     * respective row and column numbers this cell is contained in.
     */
    val name: String
        get() = "r%dc%d".format(rowIndex + 1, columnIndex + 1)

    /**
     * Assigns the given value to the current cell.
     *
     * @param value the value to assign this cell to
     * @param updateGrid if the internal state of the grid should be updated
     * @throws IllegalArgumentException if the value is outside the allowed range [0, gridSize]
     * @throws IllegalStateException if the cell contains a given value (see [.isGiven])
     */
    fun setValue(value: Int, updateGrid: Boolean) {
        check(!isGiven) { "cell value is fixed" }
        owner.invalidateState()
        val oldValue = this._value
        this._value = value
        if (updateGrid) {
            owner.notifyCellValueChanged(this, oldValue, value)
        }
    }


    /**
     * Excludes the given values from the set of possible values.
     * @param values the values to exclude
     */
    fun excludePossibleValues(updateGrid: Boolean, vararg values: Int) {
        owner.invalidateState()
        for (value in values) {
            _excludedValues.set(value)
        }
        _possibleValues.andNot(_excludedValues)

        if (updateGrid) {
            owner.notifyPossibleValuesChanged(this)
        }
    }

    /**
     * Excludes the given values from the set of possible values.
     * @param values the values to exclude
     */
    fun excludePossibleValues(values: ValueSet, updateGrid: Boolean = true) {
        owner.invalidateState()
        _excludedValues.or(values)
        _possibleValues.andNot(excludedValues)

        if (updateGrid) {
            owner.notifyPossibleValuesChanged(this)
        }
    }

    /**
     * Clears the set of excluded values for this cell.
     */
    fun clearExcludedValues(updateGrid: Boolean = true) {
        owner.invalidateState()
        _excludedValues.clearAll()
        resetPossibleValues()
        for (house in Arrays.asList(row, column, block)) {
            house.updatePossibleValuesInCell(this)
        }

        if (updateGrid) {
            owner.notifyPossibleValuesChanged(this)
        }
    }

    internal fun resetPossibleValues() {
        if (!isAssigned) {
            _possibleValues.setAll()
            _possibleValues.andNot(excludedValues)
        } else {
            _possibleValues.clearAll()
        }
    }

    internal fun updatePossibleValues(assignedValues: ValueSet) {
        _possibleValues.andNot(assignedValues)
    }

    /**
     * Fully clears this cell, including its value and given status.
     *
     * @param updateGrid whether the grid shall be updated (default: true)
     */
    fun clear(updateGrid: Boolean = true) {
        owner.invalidateState()
        isGiven = false
        _possibleValues.setAll()
        _excludedValues.clearAll()
        setValue(0, updateGrid)
    }

    /**
     * Resets this cell to its initial state, retaining given values.
     *
     * @param updateGrid whether the grid shall be updated (default: true)
     */
    fun reset(updateGrid: Boolean = true) {
        owner.invalidateState()
        _possibleValues.setAll()
        _excludedValues.clearAll()
        if (!isGiven) {
            setValue(0, updateGrid)
        } else if (updateGrid) {
            owner.notifyPossibleValuesChanged(this)
        }
    }

    // Visitor methods.

    /**
     * Visit this class with the specified visitor.
     */
    fun accept(visitor: CellVisitor) {
        visitor.visitCell(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val cell = other as Cell
        return cellIndex == cell.cellIndex
    }

    override fun hashCode(): Int {
        return Objects.hash(cellIndex)
    }

    override fun toString(): String {
        return "r%dc%d = %d (%s)".format(rowIndex + 1, columnIndex + 1, value, if (isGiven) "given" else _possibleValues)
    }
}