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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    private static final String TAG = Constants.TAG + MainActivity.class.getSimpleName();

    private static final int DIALOG_PICK_PEG = 0;
    private static final int DIALOG_GAME_OVER = 1;
    private static final int DIALOG_YOU_WON = 2;
    private static final int DIALOG_ABOUT = 3;

    private Game mGame;

    private ViewGroup mRootView;
    private ViewGroup mBoardView;
    private LayoutInflater mLayoutInflater;

    protected int mCurrentRowIndex;
    protected int mSelectedPegHoleIndex;
    protected ImageView mSelectedPegView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLayoutInflater = LayoutInflater.from(this);
        newGame();
    }


    /*
     * New game.
     */

    private final DialogInterface.OnClickListener mNewGameOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(final DialogInterface dialog, final int which) {
            removeDialog(DIALOG_GAME_OVER);
            removeDialog(DIALOG_YOU_WON);
            newGame();
        }
    };

    private void newGame() {
        mGame = new Game(Constants.DEFAULT_NB_HOLES, Constants.DEFAULT_NB_ROWS);
        mGame.setRandomSecret();
        //mGame.setSecret(CodePeg.RED, CodePeg.GREEN, CodePeg.YELLOW, CodePeg.YELLOW);

        mRootView = (ViewGroup) findViewById(R.id.root);
        createPegPicker();
        mBoardView = (ViewGroup) findViewById(R.id.board);
        mBoardView.removeAllViews();
        createRows(Constants.DEFAULT_NB_HOLES, Constants.DEFAULT_NB_ROWS);
        mCurrentRowIndex = 0;
        setRowActive(mCurrentRowIndex);
    }


    /*
     * Layout.
     */

    private void createRows(final int nbHoles, final int nbRows) {
        for (int i = 0; i < nbRows; i++) {
            final View row = createRow(nbHoles, i);
            mBoardView.addView(row);
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

    private void createPegPicker() {
        final ViewGroup pegPicker1 = (ViewGroup) mRootView.findViewById(R.id.pegPicker1);
        final ViewGroup pegPicker2 = (ViewGroup) mRootView.findViewById(R.id.pegPicker2);
        int i = 0;
        for (final CodePeg codePeg : CodePeg.values()) {
            ViewGroup pegPicker;
            if (i < CodePeg.values().length / 2) {
                pegPicker = pegPicker1;
            } else {
                pegPicker = pegPicker2;
            }
            final ImageView peg = (ImageView) mLayoutInflater.inflate(R.layout.peg, pegPicker, false);
            peg.setImageResource(PegUtil.getDrawable(codePeg));
            pegPicker.addView(peg);

            peg.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(final View v, final MotionEvent event) {
                    if (Constants.LOGD) Log.d(TAG, "" + event + " rawX=" + event.getRawX() + " rawY=" + event.getRawY());

                    return true;
                }
            });

            i++;
        }
    }


    /*
     * Active / inactive rows.
     */

    private void setRowActive(final int rowIndex) {
        final ViewGroup row = (ViewGroup) mBoardView.getChildAt(rowIndex);
        row.setBackgroundResource(R.color.row_bg_active);
        final LinearLayout containerCodePegs = (LinearLayout) row.findViewById(R.id.container_codePegs);
        final LinearLayout containerHintPegs = (LinearLayout) row.findViewById(R.id.container_hintPegs);

        // make holes focusable, clickable
        final int childCount = containerCodePegs.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final ImageView codePegView = (ImageView) containerCodePegs.getChildAt(i);
            codePegView.setFocusable(true);
            codePegView.setClickable(true);
            final int selectingPegIndex = i;
            codePegView.setOnClickListener(new OnClickListener() {
                public void onClick(final View v) {
                    mSelectedPegHoleIndex = selectingPegIndex;
                    mSelectedPegView = codePegView;
                    showDialog(DIALOG_PICK_PEG);
                }
            });
        }

        // hide hint pegs which is the last child of the row
        hide(containerHintPegs);

        // show the OK button
        show(row.findViewById(R.id.button_ok));
    }

    private void setRowInactive(final int rowIndex) {
        final ViewGroup row = (ViewGroup) mBoardView.getChildAt(rowIndex);
        row.setBackgroundResource(R.color.row_bg_inactive);
        final LinearLayout containerCodePegs = (LinearLayout) row.findViewById(R.id.container_codePegs);

        // make holes not focusable, not clickable
        final int childCount = containerCodePegs.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View codePegView = containerCodePegs.getChildAt(i);
            codePegView.setFocusable(false);
            codePegView.setOnClickListener(null);
            codePegView.setClickable(false);
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
            case DIALOG_GAME_OVER:
                builder.setTitle(R.string.dialog_gameOver_title);
                final View dialogContents = mLayoutInflater.inflate(R.layout.dialog_game_over, null, false);
                final LinearLayout container = (LinearLayout) dialogContents.findViewById(R.id.container_codePegs);
                for (final CodePeg codePeg : mGame.getSecret()) {
                    final ImageView pegView = (ImageView) mLayoutInflater.inflate(R.layout.peg, container, false);
                    pegView.setImageResource(PegUtil.getDrawable(codePeg));
                    container.addView(pegView);
                }
                builder.setView(dialogContents);
                builder.setPositiveButton(R.string.dialog_gameOver_positive, mNewGameOnClickListener);
                builder.setCancelable(false);
            break;
            case DIALOG_YOU_WON:
                builder.setTitle(R.string.dialog_youWon_title);
                builder.setMessage(getString(R.string.dialog_youWon_message, mGame.getCurrentGuess() + 1));
                builder.setPositiveButton(R.string.dialog_youWon_positive, mNewGameOnClickListener);
                builder.setCancelable(false);
            break;
            case DIALOG_ABOUT:
                builder.setTitle(R.string.dialog_about_title);
                builder.setMessage(R.string.dialog_about_message);
                builder.setPositiveButton(android.R.string.ok, null);
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
        final ViewGroup row = (ViewGroup) mBoardView.getChildAt(mCurrentRowIndex);
        row.findViewById(R.id.button_ok).setEnabled(mGame.isRowComplete(mCurrentRowIndex));
    }

    private final OnClickListener mOkOnClickListener = new OnClickListener() {
        public void onClick(final View v) {
            final GuessResult guessResult = mGame.validateGuess();
            switch (guessResult) {
                case YOU_WON:
                    showDialog(DIALOG_YOU_WON);
                break;

                case GAME_OVER:
                    List<HintPeg> hints = mGame.getHints(mCurrentRowIndex);
                    showHints(hints);
                    setRowInactive(mCurrentRowIndex);
                    showDialog(DIALOG_GAME_OVER);
                break;

                case TRY_AGAIN:
                    hints = mGame.getHints(mCurrentRowIndex);
                    showHints(hints);
                    setRowInactive(mCurrentRowIndex);
                    mCurrentRowIndex++;
                    setRowActive(mCurrentRowIndex);
                break;
            }
        }
    };


    protected void showHints(final List<HintPeg> hints) {
        final ViewGroup row = (ViewGroup) mBoardView.getChildAt(mCurrentRowIndex);

        // hide ok button
        hide(row.findViewById(R.id.button_ok));

        // show hints container and fill it
        final LinearLayout containerHintPegs = (LinearLayout) row.findViewById(R.id.container_hintPegs);
        show(containerHintPegs);
        int i = 0;
        for (final HintPeg hintPeg : hints) {
            final ImageView hintPegImageView = (ImageView) containerHintPegs.getChildAt(i);
            hintPegImageView.setImageResource(PegUtil.getDrawable(hintPeg));
            i++;
        }
    }


    /*
     * Misc.
     */

    private void hide(final View v) {
        v.setVisibility(View.INVISIBLE);
        final Animation animFadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animFadeOut.setDuration(200);
        v.setAnimation(animFadeOut);
    }

    private void show(final View v) {
        v.setVisibility(View.VISIBLE);
        final Animation animFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animFadeIn.setDuration(200);
        v.setAnimation(animFadeIn);
    }


    /*
     * Menu.
     */

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(Menu.NONE, 0, 0, R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        showDialog(DIALOG_ABOUT);
        return super.onOptionsItemSelected(item);
    }

}