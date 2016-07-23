/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2011 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.android.colorsecret.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A game board. Contains the code row, and an array of guess rows.
 */
public class Board {
    private final int mNbRows;

    /**
     * A row on the board. Contains ordered code pegs and unordered key pegs.
     */
    public static class Row {
        private int mNbHoles;
        private CodePeg[] mCodePegs;
        private List<HintPeg> mHintPegs;

        public Row(int nbHoles) {
            mNbHoles = nbHoles;
            mCodePegs = new CodePeg[nbHoles];
            mHintPegs = new ArrayList<>(nbHoles);
        }

        public CodePeg[] getCodePegs() {
            CodePeg[] res = new CodePeg[mNbHoles];
            System.arraycopy(mCodePegs, 0, res, 0, mNbHoles);
            return res;
        }

        public void setCodePeg(int position, CodePeg codePeg) {
            mCodePegs[position] = codePeg;
        }

        public void setCodePegs(CodePeg... codePegs) {
            if (codePegs.length != mNbHoles) {
                throw new IllegalArgumentException("You must pass exactly " + mNbHoles + " code pegs");
            }
            System.arraycopy(codePegs, 0, mCodePegs, 0, mNbHoles);
        }

        public List<HintPeg> getHintPegs() {
            ArrayList<HintPeg> res = new ArrayList<HintPeg>(mHintPegs);
            Collections.sort(res, HintPeg.COMPARATOR);
            return res;
        }

        public void addHintPeg(HintPeg hintPeg) {
            if (mHintPegs.size() == mNbHoles) {
                throw new IndexOutOfBoundsException("Cannot add more HintPegs");
            }
            mHintPegs.add(hintPeg);
        }
    }

    private Row mSecretRow;
    private Row[] mGuessRows;

    public Board(int nbHoles, int nbRows) {
        mNbRows = nbRows;
        mSecretRow = new Row(nbHoles);
        mGuessRows = new Row[nbRows];
        for (int i = 0; i < nbRows; i++) {
            mGuessRows[i] = new Row(nbHoles);
        }
    }

    Row getSecretRow() {
        return mSecretRow;
    }

    public Row[] getGuessRows() {
        Row[] res = new Row[mNbRows];
        System.arraycopy(mGuessRows, 0, res, 0, mNbRows);
        return res;
    }
}
