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

import java.util.List;

import org.jraf.android.slavebody.model.Board.Row;

/**
 * Represents an ongoing game.
 */
public class Game {
    public static enum GuessResult {
        TRY_AGAIN, GAME_OVER, YOU_WON,
    }

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

    public void setGuess(final int rowIndex, final int holeIndex, final CodePeg codePeg) {
        if (rowIndex >= mNbRows) {
            throw new IndexOutOfBoundsException("rowIndex >= mNbRows");
        }
        if (holeIndex >= mNbHoles) {
            throw new IndexOutOfBoundsException("holeIndex >= mNbHoles");
        }
        mBoard.getGuessRows()[rowIndex].setCodePeg(holeIndex, codePeg);
    }

    public boolean isRowComplete(final int rowIndex) {
        final Row row = mBoard.getGuessRows()[rowIndex];
        for (int i = 0; i < mNbHoles; i++) {
            if (row.getCodePegs()[i] == null) {
                return false;
            }
        }
        return true;
    }

    public GuessResult validateGuess() {
        if (mCurrentGuess == mNbRows) {
            throw new IndexOutOfBoundsException("Already reached the maximum number of guesses");
        }
        computeHints();
        mCurrentGuess++;
        return GuessResult.TRY_AGAIN; // TODO
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

    public List<HintPeg> getHints(final int rowIndex) {
        return mBoard.getGuessRows()[rowIndex].getHintPegs();
    }
}
