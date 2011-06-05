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
package org.jraf.android.slavebody.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import org.jraf.android.slavebody.R;

public class SoundUtil {

    private static final int[] SOUNDS = { R.raw.detect0, R.raw.drop0, R.raw.lost0, R.raw.lost1, R.raw.lost2, R.raw.newgame0, R.raw.nohint0,
            R.raw.pick0, R.raw.redhint0, R.raw.whitehint0, R.raw.win0, };
    private static SoundPool sSoundPool;
    private static boolean sInit;
    private static Map<Integer, Integer> sSoundIdMap;
    private static boolean sEnabled;

    private static void init(final Context context) {
        sSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        sSoundIdMap = new HashMap<Integer, Integer>(SOUNDS.length);
        for (final int soundId : SOUNDS) {
            sSoundIdMap.put(soundId, sSoundPool.load(context, soundId, 1));
        }
        sInit = true;
    }

    public static void play(final Context context, final int soundId) {
        if (!sEnabled) return;
        if (!sInit) init(context);
        sSoundPool.play(sSoundIdMap.get(soundId), 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public static void setEnabled(final boolean soundEnabled) {
        sEnabled = soundEnabled;
    }
}
