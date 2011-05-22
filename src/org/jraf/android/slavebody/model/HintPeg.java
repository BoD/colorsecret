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

import java.util.Comparator;

public enum HintPeg {
    COLOR_AND_POSITION, COLOR_ONLY, ;

    /**
     * Orders by COLOR_AND_POSITION always first.
     */
    public static Comparator<HintPeg> COMPARATOR = new Comparator<HintPeg>() {
        public int compare(final HintPeg a, final HintPeg b) {
            if (a == COLOR_AND_POSITION) {
                return -1;
            } else if (b == COLOR_AND_POSITION) {
                return 1;
            } else {
                return 0;
            }
        }

    };
}
