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
package org.jraf.android.colorsecret.model;

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
