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
package org.jraf.android.slavebody.activity;

import java.util.List;

import org.jraf.android.slavebody.Constants;
import org.jraf.android.slavebody.R;
import org.jraf.android.slavebody.model.CodePeg;
import org.jraf.android.slavebody.model.Game;
import org.jraf.android.slavebody.model.Game.GuessResult;
import org.jraf.android.slavebody.model.HintPeg;
import org.jraf.android.slavebody.util.PegUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    private static final String TAG = Constants.TAG + MainActivity.class.getSimpleName();

    private static final int DIALOG_PICK_PEG = 0;

    private Game mGame;

    private ViewGroup mRootView;
    private LayoutInflater mLayoutInflater;

    protected int mCurrentRowIndex;
    protected int mSelectedPegHoleIndex;
    protected ImageView mSelectedPegView;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mGame = new Game(Constants.DEFAULT_NB_HOLES, Constants.DEFAULT_NB_ROWS);

        mGame.setCode(CodePeg.RED, CodePeg.GREEN, CodePeg.BLUE, CodePeg.YELLOW);

        mLayoutInflater = LayoutInflater.from(this);
        mRootView = (ViewGroup) findViewById(R.id.root);
        createRows(Constants.DEFAULT_NB_HOLES, Constants.DEFAULT_NB_ROWS);
        setActiveRow(mCurrentRowIndex);
    }


    /*
     * Layout.
     */

    private void createRows(final int nbHoles, final int nbRows) {
        for (int i = 0; i < nbRows; i++) {
            final View row = createRow(nbHoles, i);
            mRootView.addView(row);
        }
    }

    private View createRow(final int nbHoles, final int rowIndex) {
        final LinearLayout res = (LinearLayout) mLayoutInflater.inflate(R.layout.row, null, false);

        final LinearLayout containerCodePegs = (LinearLayout) res.findViewById(R.id.container_codePegs);
        createCodePegs(containerCodePegs, nbHoles);

        final LinearLayout containerHintPegs = (LinearLayout) res.findViewById(R.id.container_hintPegs);
        createHintPegs(containerHintPegs, nbHoles);

        res.findViewById(R.id.button_ok).setOnClickListener(mOkOnClickListener);

        return res;
    }

    private void createCodePegs(final LinearLayout containerCodePegs, final int nbHoles) {
        for (int i = 0; i < nbHoles; i++) {
            final ImageView peg = (ImageView) mLayoutInflater.inflate(R.layout.peg, containerCodePegs, false);
            peg.setClickable(true);
            final int selectingPegIndex = i;
            peg.setOnClickListener(new OnClickListener() {
                public void onClick(final View v) {
                    mSelectedPegHoleIndex = selectingPegIndex;
                    mSelectedPegView = peg;
                    showDialog(DIALOG_PICK_PEG);
                }
            });
            containerCodePegs.addView(peg);
        }
    }

    private void createHintPegs(final LinearLayout containerHintPegs, final int nbHoles) {
        for (int i = 0; i < nbHoles; i++) {
            final ImageView peg = (ImageView) mLayoutInflater.inflate(R.layout.peg, containerHintPegs, false);
            peg.setImageResource(R.drawable.peg_hint_empty);
            containerHintPegs.addView(peg);
        }
    }


    /*
     * Active / unactive rows.
     */

    private void setActiveRow(final int rowIndex) {
        final ViewGroup row = (ViewGroup) mRootView.getChildAt(rowIndex);
        row.setSelected(true);
        final LinearLayout containerCodePegs = (LinearLayout) row.findViewById(R.id.container_codePegs);
        final LinearLayout containerHintPegs = (LinearLayout) row.findViewById(R.id.container_hintPegs);

        // make holes focusable
        final int childCount = containerCodePegs.getChildCount();
        for (int i = 0; i < childCount; i++) {
            containerCodePegs.getChildAt(i).setFocusable(true);
        }

        // hide hint pegs which is the last child of the row
        containerHintPegs.setVisibility(View.GONE);

        // show the OK button
        row.findViewById(R.id.button_ok).setVisibility(View.VISIBLE);
    }

    private void setUnactiveRow(final int rowIndex) {
        final ViewGroup row = (ViewGroup) mRootView.getChildAt(rowIndex);
        final LinearLayout containerCodePegs = (LinearLayout) row.findViewById(R.id.container_codePegs);

        // make holes not focusable
        final int childCount = containerCodePegs.getChildCount();
        for (int i = 0; i < childCount; i++) {
            containerCodePegs.getChildAt(i).setFocusable(false);
        }
    }


    /*
     * Dialog.
     */

    @Override
    protected Dialog onCreateDialog(final int id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_PICK_PEG:
                builder.setTitle(R.string.dialog_pickPeg_title);
                builder.setSingleChoiceItems(new PegListAdapter(this), -1, mPickPegOnClickListener);
                builder.setNegativeButton(android.R.string.cancel, null);
            break;
        }
        return builder.create();
    }

    private final DialogInterface.OnClickListener mPickPegOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(final DialogInterface dialog, final int which) {
            dialog.dismiss();
            final CodePeg codePeg = CodePeg.values()[which];
            mSelectedPegView.setImageResource(PegUtil.getDrawable(codePeg));
            mGame.setGuess(mCurrentRowIndex, mSelectedPegHoleIndex, codePeg);
            updateOkButton();
        }
    };

    private void updateOkButton() {
        final ViewGroup row = (ViewGroup) mRootView.getChildAt(mCurrentRowIndex);
        row.findViewById(R.id.button_ok).setEnabled(mGame.isRowComplete(mCurrentRowIndex));
    }

    private final OnClickListener mOkOnClickListener = new OnClickListener() {
        public void onClick(final View v) {
            final GuessResult guessResult = mGame.validateGuess();
            switch (guessResult) {
                case TRY_AGAIN:
                    final List<HintPeg> hints = mGame.getHints(mCurrentRowIndex);
                    showHints(hints);
                    setUnactiveRow(mCurrentRowIndex);
                    mCurrentRowIndex++;
                    setActiveRow(mCurrentRowIndex);
                break;
            }
        }
    };


    protected void showHints(final List<HintPeg> hints) {
        final ViewGroup row = (ViewGroup) mRootView.getChildAt(mCurrentRowIndex);

        // hide ok button
        row.findViewById(R.id.button_ok).setVisibility(View.GONE);

        // show hints container and fill it
        final LinearLayout containerHintPegs = (LinearLayout) row.findViewById(R.id.container_hintPegs);
        containerHintPegs.setVisibility(View.VISIBLE);
        int i = 0;
        for (final HintPeg hintPeg : hints) {
            final ImageView hintPegImageView = (ImageView) containerHintPegs.getChildAt(i);
            hintPegImageView.setImageResource(PegUtil.getDrawable(hintPeg));
            i++;
        }
    }

}