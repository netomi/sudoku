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
package org.netomi.sudoku.solver.techniques

import org.netomi.sudoku.solver.HintFinder

class NakedPairFinderTest : BaseHintFinderTest() {
    override fun createHintFinder(): HintFinder {
        return NakedPairFinder()
    }

    override fun matches(testCase: TechniqueTestCase): Boolean {
        return testCase.technique.startsWith("0200")
    }
}

class NakedTripleFinderTest : BaseHintFinderTest() {
    override fun createHintFinder(): HintFinder {
        return NakedTripleFinder()
    }

    override fun matches(testCase: TechniqueTestCase): Boolean {
        return testCase.technique.startsWith("0201")
    }
}

class NakedQuadrupleFinderTest : BaseHintFinderTest() {
    override fun createHintFinder(): HintFinder {
        return NakedQuadrupleFinder()
    }

    override fun matches(testCase: TechniqueTestCase): Boolean {
        return testCase.technique.startsWith("0202")
    }
}

class LockedPairFinderTest : BaseHintFinderTest() {
    override fun createHintFinder(): HintFinder {
        return LockedPairFinder()
    }

    override fun matches(testCase: TechniqueTestCase): Boolean {
        return testCase.technique.startsWith("0110")
    }
}

class LockedTripleFinderTest : BaseHintFinderTest() {
    override fun createHintFinder(): HintFinder {
        return LockedTripleFinder()
    }

    override fun matches(testCase: TechniqueTestCase): Boolean {
        return testCase.technique.startsWith("0111")
    }
}