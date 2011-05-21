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
package org.jraf.android.slavebody.activity;

import java.util.ArrayList;

import org.jraf.android.slavebody.R;
import org.jraf.android.slavebody.model.CodePeg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class PegListAdapter extends ArrayAdapter<CodePeg> {
    private final Context mContext;

    public PegListAdapter(final Context context, final int layoutResourceId, final ArrayList<CodePeg> objects) {
        super(context, layoutResourceId, objects);
        mContext = context;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.peg_list_item, parent, false);
        }

        final ImageView imageView = (ImageView) view.findViewById(R.id.peg);

        final CodePeg codePeg = getItem(position);
        switch (codePeg) {
            case ORANGE:
                imageView.setImageResource(R.drawable.peg_code_empty);

            break;
        }
        return view;
    }
}