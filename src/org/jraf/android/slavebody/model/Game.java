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

import org.jraf.android.slavebody.model.Board.Row;

/**
 * Represents an ongoing game.
 */
public class Game {
    private final int mNbHoles;
    private final int mNbRows;

    private final Board mBoard;

    private int mCurrentGuess = 0;

    /**
     * @param nbHoles
     *            number of holes for each row.
     * @param nbRows
     *            total number of rows.
     */
    public Game(final int nbHoles, final int nbRows) {
        mNbHoles = nbHoles;
        mNbRows = nbRows;
        mBoard = new Board(nbHoles, nbRows);
    }

    public Board getBoard() {
        return mBoard;
    }

    public void setCode(final CodePeg... codePegs) {
        mBoard.getCodeRow().setCodePegs(codePegs);
    }

    public int newGuess(final CodePeg... codePegs) {
        if (mCurrentGuess == mNbRows) {
            throw new IndexOutOfBoundsException("Allready reached the maximum number of guesses");
        }
        mBoard.getGuessRows()[mCurrentGuess].setCodePegs(codePegs);
        computeHints();
        mCurrentGuess++;
        return mCurrentGuess;
    }

    /**
     * Compute the hint pegs for the current guess and add them to the board.
     */
    private void computeHints() {
        final Row guessRow = mBoard.getGuessRows()[mCurrentGuess];
        final Row codeRow = mBoard.getCodeRow();

        // iterate over guess pegs
        for (int guessIdx = 0; guessIdx < mNbHoles; guessIdx++) {
            final CodePeg guessPeg = guessRow.getCodePegs()[guessIdx];

            // look for this guess peg in the code pegs
            for (int codeIdx = 0; codeIdx < mNbHoles; codeIdx++) {
                final CodePeg codePeg = codeRow.getCodePegs()[codeIdx];

                if (codePeg == guessPeg) {
                    // we found one!
                    if (guessIdx == codeIdx) {
                        // plus it's at the same index!
                        guessRow.addHintPeg(HintPeg.COLOR_AND_POSITION);
                    } else {
                        guessRow.addHintPeg(HintPeg.COLOR_ONLY);
                    }
                    // now that we found it, remove this code peg from further comparisons
                    codeRow.getCodePegs()[codeIdx] = null;
                    break;
                }
            }
        }
    }
}
