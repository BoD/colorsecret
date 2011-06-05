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
package org.jraf.android.colorsecret.activity;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.jraf.android.colorsecret.model.CodePeg;
import org.jraf.android.colorsecret.util.PegUtil;
import org.jraf.android.colorsecret.R;

public class PegListAdapter extends ArrayAdapter<CodePeg> {
    private final Context mContext;

    public PegListAdapter(final Context context) {
        super(context, 0, new ArrayList<CodePeg>(Arrays.asList(CodePeg.values())));
        mContext = context;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.peg_list_item, parent, false);
        }

        final ImageView imageView = (ImageView) view.findViewById(R.id.pegView).findViewById(R.id.peg);

        final CodePeg codePeg = getItem(position);
        imageView.setImageResource(PegUtil.getDrawable(codePeg));
        return view;
    }
}