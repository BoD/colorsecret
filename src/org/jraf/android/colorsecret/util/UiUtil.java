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
package org.jraf.android.colorsecret.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class UiUtil {

    public static void setInvisible(final View v) {
        v.setVisibility(View.INVISIBLE);
        final Animation animFadeOut = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out);
        animFadeOut.setDuration(300);
        v.setAnimation(animFadeOut);
    }

    public static void setGone(final View v) {
        v.setVisibility(View.GONE);
        final Animation animFadeOut = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out);
        animFadeOut.setDuration(300);
        v.setAnimation(animFadeOut);
    }

    public static void setVisible(final View v) {
        v.setVisibility(View.VISIBLE);
        final Animation animFadeIn = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
        animFadeIn.setDuration(300);
        v.setAnimation(animFadeIn);
    }
}
