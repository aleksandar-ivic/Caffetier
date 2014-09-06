package com.example.caffetier.app.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

import com.example.caffetier.app.domain.Area;

import java.util.ArrayList;

/**
 * Created by Aleksandar on 29-Jul-14.
 */
public class Util {

    public static Typeface FONT_HEADINGS;
    public static ArrayList<Area> allAreas = new ArrayList<Area>();
    public static String projectID = "204476649954";

    public static void setActionBarTitleFont(Activity activity){
        int titleId = activity.getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) activity.findViewById(titleId);
        yourTextView.setTextSize(16);
        yourTextView.setTypeface(Util.FONT_HEADINGS);
    }

}
