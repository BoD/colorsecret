/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2011 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
