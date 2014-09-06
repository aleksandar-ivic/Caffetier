package com.example.caffetier.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caffetier.app.R;
import com.example.caffetier.app.domain.Area;
import com.example.caffetier.app.util.Util;


import java.util.List;


public class MySpinnerAdapter extends ArrayAdapter<Area> {

    public MySpinnerAdapter(Context context, int resource, List<Area> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_item, null);
        }
        //TextView spinnerItemView = (TextView) convertView.findViewById(R.id.spinnerItem);
        ((TextView) v).setTypeface(Util.FONT_HEADINGS);
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        ((TextView) v).setTypeface(Util.FONT_HEADINGS);
        return v;
    }
}
