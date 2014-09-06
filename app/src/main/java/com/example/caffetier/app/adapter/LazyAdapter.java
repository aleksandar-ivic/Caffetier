package com.example.caffetier.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caffetier.app.R;
import com.example.caffetier.app.domain.Cafe;
import com.example.caffetier.app.util.Util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Cafe> data;
    private static LayoutInflater inflater = null;
    static ImageView logo;
    View vi;
    File sdCardDirectory = Environment.getExternalStorageDirectory();

    public LazyAdapter(Activity a, ArrayList<Cafe> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row_caffe, null);

        TextView naziv = (TextView) vi.findViewById(R.id.naziv);
        naziv.setTypeface(Util.FONT_HEADINGS);
        TextView adresa = (TextView) vi.findViewById(R.id.adresa);
        adresa.setTypeface(Util.FONT_HEADINGS);


        for (Cafe cafe : data) {
            cafe = data.get(position);

            // Setting all values in listview
            naziv.setText(cafe.naziv);
            adresa.setText(cafe.adresa);
            GetImageTask getImageTask = new GetImageTask(cafe);
            getImageTask.execute();


        }

        return vi;
    }

    private class GetImageTask extends AsyncTask<Void, Void, Bitmap> {

        Cafe cafe;

        public GetImageTask(Cafe cafe) {
            this.cafe = cafe;
        }

        @Override
        protected void onPreExecute() {
            logo = (ImageView) vi.findViewById(R.id.logo);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap pic = null;
            try {
                Log.i("SMRDLJIVI URL", cafe.logoUrl);
                pic = BitmapFactory.decodeStream((InputStream) new URL(cafe.logoUrl).getContent());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pic;
        }

            @Override
            protected void onPostExecute (Bitmap bitmap){
                if (bitmap != null) {
                    logo.setImageBitmap(bitmap);
                } else {
                    logo.setImageResource(R.drawable.ic_launcher);
                }

            }
        }
    }