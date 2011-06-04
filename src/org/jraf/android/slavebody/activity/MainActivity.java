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

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jraf.android.slavebody.Constants;
import org.jraf.android.slavebody.R;
import org.jraf.android.slavebody.model.CodePeg;
import org.jraf.android.slavebody.model.Game;
import org.jraf.android.slavebody.model.Game.GuessResult;
import org.jraf.android.slavebody.model.HintPeg;
import org.jraf.android.slavebody.util.IoUtil;
import org.jraf.android.slavebody.util.PegUtil;
import org.jraf.android.slavebody.util.StringUtil;
import org.jraf.android.slavebody.util.UiUtil;

public class MainActivity extends Activity {
    private static final String TAG = Constants.TAG + MainActivity.class.getSimpleName();

    private static final int DIALOG_PICK_PEG = 0;
    private static final int DIALOG_GAME_OVER = 1;
    private static final int DIALOG_YOU_WON = 2;
    private static final int DIALOG_ABOUT = 3;
    private static final int DIALOG_CONFIRM_EXIT = 4;
    private static final int DIALOG_HELP = 5;

    private int mNbHoles;
    private int mNbRows;

    private Game mGame;

    private ViewGroup mRootView;
    private final int[] mRootXy = new int[2];
    private ViewGroup mBoardView;
    private LayoutInflater mLayoutInflater;

    protected int mCurrentRowIndex;
    protected int mSelectedPegHoleIndex;
    protected View mSelectedPegView;

    protected CodePeg mDragingPeg;
    private boolean mDragging;
    private View mDraggingPegView;

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
        mNbHoles = Constants.DEFAULT_NB_HOLES;
        mNbRows = Constants.DEFAULT_NB_ROWS;

        mGame = new Game(mNbHoles, mNbRows);
        mGame.setRandomSecret();
//        mGame.setSecret(CodePeg.RED, CodePeg.GREEN, CodePeg.YELLOW, CodePeg.YELLOW);

        mRootView = (ViewGroup) findViewById(R.id.root);

        mDraggingPegView = mRootView.findViewById(R.id.draggingPeg);

        createPegPicker();
        mBoardView = (ViewGroup) findViewById(R.id.board);
        mBoardView.removeAllViews();
        createRows();
        mCurrentRowIndex = 0;
        setRowActive(mCurrentRowIndex);

        ((ScrollView) mRootView.findViewById(R.id.scrollView)).fullScroll(View.FOCUS_UP);

        refreshScore();
    }


    /*
     * Layout.
     */

    private void createRows() {
        for (int i = 0; i < mNbRows; i++) {
            final View row = createRow(i);
            mBoardView.addView(row);
        }
    }

    private View createRow(final int rowIndex) {
        final LinearLayout res = (LinearLayout) mLayoutInflater.inflate(R.layout.row, null, false);

        final LinearLayout containerCodePegs = (LinearLayout) res.findViewById(R.id.container_codePegs);
        createCodePegs(containerCodePegs);

        final LinearLayout containerHintPegs = (LinearLayout) res.findViewById(R.id.container_hintPegs);
        createHintPegs(containerHintPegs);

        res.findViewById(R.id.button_ok).setOnClickListener(mOkOnClickListener);

        return res;
    }

    private void createCodePegs(final LinearLayout containerCodePegs) {
        for (int i = 0; i < mNbHoles; i++) {
            final View peg = mLayoutInflater.inflate(R.layout.peg, containerCodePegs, false);
            containerCodePegs.addView(peg);
        }
    }

    private void createHintPegs(final LinearLayout containerHintPegs) {
        final LinearLayout containerHintPegs1 = (LinearLayout) containerHintPegs.findViewById(R.id.container_hintPegs1);
        final LinearLayout containerHintPegs2 = (LinearLayout) containerHintPegs.findViewById(R.id.container_hintPegs2);
        LinearLayout container;
        for (int i = 0; i < mNbHoles; i++) {
            if (i < mNbHoles / 2) {
                container = containerHintPegs1;
            } else {
                container = containerHintPegs2;
            }
            final View peg = mLayoutInflater.inflate(R.layout.peg, container, false);
            ((ImageView) peg.findViewById(R.id.peg)).setImageResource(R.drawable.peg_hint_empty);
            container.addView(peg);
        }
    }

    private void createPegPicker() {
        final ViewGroup pegPicker = (ViewGroup) mRootView.findViewById(R.id.pegPicker);
        pegPicker.removeAllViews();
        for (final CodePeg codePeg : CodePeg.values()) {
            final View pegView = mLayoutInflater.inflate(R.layout.peg, pegPicker, false);
            final LinearLayout.LayoutParams pegLayoutParams = (android.widget.LinearLayout.LayoutParams) pegView.getLayoutParams();
            pegLayoutParams.weight = 1;
            pegView.setLayoutParams(pegLayoutParams);

            final ImageView pegImageView = (ImageView) pegView.findViewById(R.id.peg);
            pegImageView.setImageResource(PegUtil.getDrawable(codePeg));

            final LinearLayout.LayoutParams pegImageLayoutParams = (android.widget.LinearLayout.LayoutParams) pegImageView
                    .getLayoutParams();
            pegImageLayoutParams.leftMargin = 1;
            pegImageLayoutParams.topMargin = 1;
            pegImageLayoutParams.bottomMargin = 1;
            pegImageLayoutParams.rightMargin = 1;
            pegImageView.setLayoutParams(pegImageLayoutParams);

            pegPicker.addView(pegView);

            pegView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(final View v, final MotionEvent event) {
                    v.setBackgroundResource(R.drawable.peg_code_bg_dragging);
                    mDragingPeg = codePeg;
                    handleDragEvent(event);
                    return true;
                }
            });
        }

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean pickerShown = sharedPreferences.getBoolean(Constants.PREF_PICKER_SHOWN, true);
        pegPicker.setVisibility(pickerShown ? View.VISIBLE : View.GONE);
    }

    private void refreshScore() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int totalGames = sharedPreferences.getInt(Constants.PREF_TOTAL_GAMES, 0);
        final int totalWon = sharedPreferences.getInt(Constants.PREF_TOTAL_WON, 0);
        final int totalScore = sharedPreferences.getInt(Constants.PREF_TOTAL_SCORE, 0);
        ((TextView) mRootView.findViewById(R.id.totalGames)).setText(getString(R.string.score_totalGames, totalGames));
        ((TextView) mRootView.findViewById(R.id.totalWon)).setText(getString(R.string.score_totalWon, totalWon));
        ((TextView) mRootView.findViewById(R.id.totalScore)).setText(getString(R.string.score_totalScore, totalScore));
    }


    /*
     * Drag and drop.
     */

    protected void handleDragEvent(final MotionEvent event) {
        final int eventX = (int) event.getRawX();
        final int eventY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setRowReceivingDrag(mCurrentRowIndex, true);
                mDragging = true;
                mDraggingPegView.setVisibility(View.VISIBLE);
                final ImageView pegImageView = (ImageView) mDraggingPegView.findViewById(R.id.peg);
                pegImageView.setImageResource(PegUtil.getDrawable(mDragingPeg));
                pegImageView.getDrawable().setAlpha(127);
                mRootView.getLocationOnScreen(mRootXy);
            break;

            case MotionEvent.ACTION_UP:
                setRowReceivingDrag(mCurrentRowIndex, false);
                mDragging = false;
                if (mSelectedPegHoleIndex != -1) {
                    ((ImageView) mSelectedPegView.findViewById(R.id.peg)).setImageResource(PegUtil.getDrawable(mDragingPeg));
                    mGame.setGuess(mCurrentRowIndex, mSelectedPegHoleIndex, mDragingPeg);
                    updateOkButton();
                }
                mDraggingPegView.setVisibility(View.GONE);
                moveDraggingPegView(-mDraggingPegView.getWidth(), -mDraggingPegView.getHeight());
            break;

            case MotionEvent.ACTION_MOVE:
                if (mDragging) {
                    final int newX = eventX - mRootXy[0] - mDraggingPegView.getWidth() / 2;
                    final int newY = eventY - mRootXy[1] - mDraggingPegView.getHeight() / 2;
                    moveDraggingPegView(newX, newY);

                    final ViewGroup row = (ViewGroup) mBoardView.getChildAt(mCurrentRowIndex);
                    final LinearLayout containerCodePegs = (LinearLayout) row.findViewById(R.id.container_codePegs);
                    final int childCount = containerCodePegs.getChildCount();
                    final int[] pegXy = new int[2];
                    mSelectedPegHoleIndex = -1;
                    for (int i = 0; i < childCount; i++) {
                        final View pegView = containerCodePegs.getChildAt(i);
                        pegView.getLocationOnScreen(pegXy);
                        final int pegX = pegXy[0];
                        final int pegY = pegXy[1];
                        final int pegWidth = pegView.getWidth();
                        final int pegHeight = pegView.getHeight();
                        if (pegX < eventX && eventX < pegX + pegWidth && pegY < eventY && eventY < pegY + pegHeight) {
                            pegView.setBackgroundResource(R.drawable.peg_code_bg_dragging);
                            mSelectedPegHoleIndex = i;
                            mSelectedPegView = pegView;
                        } else {
//                            pegView.setBackgroundResource(R.drawable.row_bg_dragging);
                            pegView.setBackgroundResource(0);
                        }
                    }
                }
            break;
        }
    }


    private void moveDraggingPegView(final int newX, final int newY) {
        final AbsoluteLayout.LayoutParams layoutParams = (LayoutParams) mDraggingPegView.getLayoutParams();
        layoutParams.x = newX;
        layoutParams.y = newY;
        mDraggingPegView.setLayoutParams(layoutParams);
    }


    /*
     * Active / inactive rows.
     */

    private void setRowActive(final int rowIndex) {
        final ViewGroup row = (ViewGroup) mBoardView.getChildAt(rowIndex);
        row.setBackgroundResource(R.drawable.row_bg_active);
        final LinearLayout containerCodePegs = (LinearLayout) row.findViewById(R.id.container_codePegs);
        final LinearLayout containerHintPegs = (LinearLayout) row.findViewById(R.id.container_hintPegs);

        // make holes focusable, clickable
        final int childCount = containerCodePegs.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View codePegView = containerCodePegs.getChildAt(i);
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
        UiUtil.hide(containerHintPegs);

        // show the OK button
        UiUtil.show(row.findViewById(R.id.button_ok));
    }

    private void setRowInactive(final int rowIndex) {
        final ViewGroup row = (ViewGroup) mBoardView.getChildAt(rowIndex);
        row.setBackgroundResource(R.drawable.row_bg_inactive);
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

    private void setRowReceivingDrag(final int rowIndex, final boolean receiving) {
        final ViewGroup row = (ViewGroup) mBoardView.getChildAt(rowIndex);
        if (receiving) {
            row.setBackgroundResource(R.drawable.row_bg_dragging);
        } else {
            row.setBackgroundResource(R.drawable.row_bg_active);
            // reset all the row holes / pegs to default bg
            final LinearLayout containerCodePegs = (LinearLayout) row.findViewById(R.id.container_codePegs);
            int childCount = containerCodePegs.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View codePegView = containerCodePegs.getChildAt(i);
                codePegView.setBackgroundResource(R.drawable.peg_bg);
            }

            // reset all the dragging pegs to default bg
            final ViewGroup pegPicker = (ViewGroup) mRootView.findViewById(R.id.pegPicker);
            childCount = pegPicker.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View codePegView = pegPicker.getChildAt(i);
                codePegView.setBackgroundResource(0);
            }
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
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                final View dialogContents = mLayoutInflater.inflate(R.layout.dialog_game_over, null, false);
                final LinearLayout container = (LinearLayout) dialogContents.findViewById(R.id.container_codePegs);
                for (final CodePeg codePeg : mGame.getSecret()) {
                    final View pegView = mLayoutInflater.inflate(R.layout.peg, container, false);
                    ((ImageView) pegView.findViewById(R.id.peg)).setImageResource(PegUtil.getDrawable(codePeg));
                    container.addView(pegView);
                }
                builder.setView(dialogContents);
                builder.setPositiveButton(R.string.dialog_gameOver_positive, mNewGameOnClickListener);
                builder.setCancelable(false);
            break;

            case DIALOG_YOU_WON:
                builder.setTitle(R.string.dialog_youWon_title);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage(getString(R.string.dialog_youWon_message, mGame.getCurrentGuess() + 1));
                builder.setPositiveButton(R.string.dialog_youWon_positive, mNewGameOnClickListener);
                builder.setCancelable(false);
            break;

            case DIALOG_ABOUT:
                builder.setTitle(R.string.dialog_about_title);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage(R.string.dialog_about_message);
                builder.setPositiveButton(android.R.string.ok, null);
            break;

            case DIALOG_CONFIRM_EXIT:
                builder.setTitle(android.R.string.dialog_alert_title);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage(R.string.dialog_confirmExit_message);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        finish();
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
            break;

            case DIALOG_HELP:
                builder.setTitle(R.string.dialog_help_title);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                final WebView webView = new WebView(this);
                String html;
                try {
                    html = IoUtil.inputStreamToString(getResources().openRawResource(R.raw.help));
                } catch (final IOException e) {
                    // should never happen
                    throw new AssertionError("Could not read eula file");
                }
                html = StringUtil.reworkForWebView(html);
                webView.loadData(html, "text/html", "utf-8");
                builder.setView(webView);
                builder.setPositiveButton(android.R.string.ok, null);
            break;

        }
        return builder.create();
    }

    private final DialogInterface.OnClickListener mPickPegOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(final DialogInterface dialog, final int which) {
            dialog.dismiss();
            final CodePeg codePeg = CodePeg.values()[which];
            ((ImageView) mSelectedPegView.findViewById(R.id.peg)).setImageResource(PegUtil.getDrawable(codePeg));
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
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    int totalGames = sharedPreferences.getInt(Constants.PREF_TOTAL_GAMES, 0);
                    int totalWon = sharedPreferences.getInt(Constants.PREF_TOTAL_WON, 0);
                    int totalScore = sharedPreferences.getInt(Constants.PREF_TOTAL_SCORE, 0);
                    totalGames++;
                    totalWon++;
                    totalScore += (mNbRows - mCurrentRowIndex) * 10;
                    Editor editor = sharedPreferences.edit();
                    editor.putInt(Constants.PREF_TOTAL_GAMES, totalGames);
                    editor.putInt(Constants.PREF_TOTAL_WON, totalWon);
                    editor.putInt(Constants.PREF_TOTAL_SCORE, totalScore);
                    editor.commit();
                break;

                case GAME_OVER:
                    List<HintPeg> hints = mGame.getHints(mCurrentRowIndex);
                    showHints(hints);
                    setRowInactive(mCurrentRowIndex);
                    showDialog(DIALOG_GAME_OVER);
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    totalGames = sharedPreferences.getInt(Constants.PREF_TOTAL_GAMES, 0);
                    totalGames++;
                    editor = sharedPreferences.edit();
                    editor.putInt(Constants.PREF_TOTAL_GAMES, totalGames);
                    editor.commit();
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
        UiUtil.hide(row.findViewById(R.id.button_ok));

        // show hints container and fill it
        final LinearLayout containerHintPegs = (LinearLayout) row.findViewById(R.id.container_hintPegs);
        UiUtil.show(containerHintPegs);

        final LinearLayout containerHintPegs1 = (LinearLayout) containerHintPegs.findViewById(R.id.container_hintPegs1);
        final LinearLayout containerHintPegs2 = (LinearLayout) containerHintPegs.findViewById(R.id.container_hintPegs2);
        LinearLayout container;
        int i = 0;
        for (final HintPeg hintPeg : hints) {
            if (i < mNbHoles / 2) {
                container = containerHintPegs1;
            } else {
                container = containerHintPegs2;
            }
            final View hintPegView = container.getChildAt(i % 2);
            ((ImageView) hintPegView.findViewById(R.id.peg)).setImageResource(PegUtil.getDrawable(hintPeg));
            i++;
        }
    }


    /*
     * Menu.
     */

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean pickerShown = sharedPreferences.getBoolean(Constants.PREF_PICKER_SHOWN, true);
        menu.findItem(R.id.menu_showPicker).setTitle(pickerShown ? R.string.menu_hidePicker : R.string.menu_showPicker);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                showDialog(DIALOG_ABOUT);
            break;

            case R.id.menu_showPicker:
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                boolean pickerShown = sharedPreferences.getBoolean(Constants.PREF_PICKER_SHOWN, true);
                pickerShown = !pickerShown;
                sharedPreferences.edit().putBoolean(Constants.PREF_PICKER_SHOWN, pickerShown).commit();
                final View picker = mRootView.findViewById(R.id.pegPicker);
                if (pickerShown) {
                    UiUtil.show(picker);
                } else {
                    UiUtil.hide(picker);
                }
            break;

            case R.id.menu_help:
                showDialog(DIALOG_HELP);
            break;
        }

        return super.onOptionsItemSelected(item);
    }



    /*
     * Intercept back key.
     * Cf: http://android-developers.blogspot.com/2009/12/back-and-other-hard-keys-three-stories.html
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        showDialog(DIALOG_CONFIRM_EXIT);
    }
}