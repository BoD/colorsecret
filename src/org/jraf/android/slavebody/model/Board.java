/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright 2011 Benoit 'BoD' Lubek (BoD@JRAF.org).  All Rights Reserved.
 */
package org.jraf.android.slavebody.model;

import java.util.ArrayList;
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
        private final int mNbHoles;
        private final CodePeg[] mCodePegs;
        private final List<HintPeg> mHintPegs;

        public Row(final int nbHoles) {
            mNbHoles = nbHoles;
            mCodePegs = new CodePeg[nbHoles];
            mHintPegs = new ArrayList<HintPeg>(nbHoles);
        }

        public CodePeg[] getCodePegs() {
            final CodePeg[] res = new CodePeg[mNbHoles];
            System.arraycopy(mCodePegs, 0, res, 0, mNbHoles);
            return res;
        }

        public void setCodePeg(final int position, final CodePeg codePeg) {
            mCodePegs[position] = codePeg;
        }

        public void setCodePegs(final CodePeg... codePegs) {
            if (codePegs.length != mNbHoles) {
                throw new IllegalArgumentException("You must pass exactly " + mNbHoles + " code pegs");
            }
            System.arraycopy(codePegs, 0, mCodePegs, 0, mNbHoles);
        }

        @SuppressWarnings("rawtypes")
        public List<HintPeg> getHintPegs() {
            return new ArrayList(mHintPegs);
        }

        public void addHintPeg(final HintPeg hintPeg) {
            if (mHintPegs.size() == mNbHoles) {
                throw new IndexOutOfBoundsException("Cannot add more HintPegs");
            }
            mHintPegs.add(hintPeg);
        }
    }

    private final Row mCodeRow;
    private final Row[] mGuessRows;

    public Board(final int nbHoles, final int nbRows) {
        mNbRows = nbRows;
        mCodeRow = new Row(nbHoles);
        mGuessRows = new Row[nbRows];
        for (int i = 0; i < nbRows; i++) {
            mGuessRows[i] = new Row(nbHoles);
        }
    }

    Row getCodeRow() {
        return mCodeRow;
    }

    public Row[] getGuessRows() {
        final Row[] res = new Row[mNbRows];
        System.arraycopy(mGuessRows, 0, res, 0, mNbRows);
        return res;
    }
}
