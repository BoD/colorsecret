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

public class MainActivity extends Activity {
    private static final String TAG = Constants.TAG + MainActivity.class.getSimpleName();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}