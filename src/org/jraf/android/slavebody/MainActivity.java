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
package org.jraf.android.slavebody;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    private static final String TAG = Constants.TAG + MainActivity.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLayoutInflater = LayoutInflater.from(this);

        createRows((ViewGroup) findViewById(R.id.root), Constants.DEFAULT_NB_HOLES, Constants.DEFAULT_NB_ROWS);
    }

    private void createRows(final ViewGroup root, final int nbHoles, final int nbRows) {
        for (int i = 0; i < nbRows; i++) {
            root.addView(createRow(nbHoles));
        }
    }

    private View createRow(final int nbHoles) {
        final LinearLayout res = (LinearLayout) mLayoutInflater.inflate(R.layout.row, null, false);
        for (int i = 0; i < nbHoles; i++) {
            res.addView(mLayoutInflater.inflate(R.layout.peg_code, null, false));
        }
        return res;
    }
}