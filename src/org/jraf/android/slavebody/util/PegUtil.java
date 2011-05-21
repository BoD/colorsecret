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

import org.jraf.android.slavebody.R;
import org.jraf.android.slavebody.model.CodePeg;

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
}
