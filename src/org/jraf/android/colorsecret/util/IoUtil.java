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

import java.io.IOException;
import java.io.InputStream;

public class IoUtil {

    public static final String inputStreamToString(final InputStream in) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        final byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, read, "ISO-8859-1"));
        }
        return stringBuilder.toString();
    }
}
