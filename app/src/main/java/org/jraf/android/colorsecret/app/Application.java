/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 * 
 * Copyright (C) 2013 Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.colorsecret.app;

import android.os.Handler;
import android.os.StrictMode;

import org.jraf.android.colorsecret.BuildConfig;
import org.jraf.android.util.log.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {
    private static final String TAG = "ColorSecret";

    @Override
    public void onCreate() {
        super.onCreate();

        // Log
        Log.init(this, TAG);

        // Crash reporting
        if (BuildConfig.CRASH_REPORT) {
            // Crashlytics
            try {
                Fabric.with(this, new Crashlytics());
            } catch (Throwable t) {
                Log.w("Problem while initializing Crashlytics", t);
            }
        }

        // Strict mode
        if (BuildConfig.STRICT_MODE) setupStrictMode();
    }

    private void setupStrictMode() {
        // Do this in a Handler.post because of this issue: http://code.google.com/p/android/issues/detail?id=35298
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Do not detect disk reads/writes because it seems it causes bugs in Google Maps (?!)
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
            }
        });
    }
}
