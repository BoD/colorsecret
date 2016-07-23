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

import org.jraf.android.colorsecret.model.CodePeg;
import org.jraf.android.colorsecret.model.HintPeg;
import org.jraf.android.colorsecret.R;

public class PegUtil {
    public static int getDrawable(final CodePeg codePeg) {
        switch (codePeg) {
            case ORANGE:
                return R.drawable.peg_code_orange;
            case RED:
                return R.drawable.peg_code_red;
            case GREEN:
                return R.drawable.peg_code_green;
            case YELLOW:
                return R.drawable.peg_code_yellow;
            case BLUE:
                return R.drawable.peg_code_blue;
            case MAGENTA:
                return R.drawable.peg_code_magenta;
        }
        throw new IllegalArgumentException("No drawable for code peg " + codePeg);

    }

    public static int getDrawable(final HintPeg hintPeg) {
        switch (hintPeg) {
            case COLOR_AND_POSITION:
                return R.drawable.peg_hint_color_and_position;
            case COLOR_ONLY:
                return R.drawable.peg_hint_color_only;
        }
        throw new IllegalArgumentException("No drawable for hint peg " + hintPeg);

    }
}
