package com.example.caffetier.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caffetier.app.R;
import com.example.caffetier.app.domain.Caffe;
import com.example.caffetier.app.util.MyHttpClient;
import com.example.caffetier.app.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Caffe> data;
    private static LayoutInflater inflater = null;
    static ImageView logo;
    View vi;
    File sdCardDirectory = Environment.getExternalStorageDirectory();

    public LazyAdapter(Activity a, ArrayList<Caffe> d) {
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



        for (Caffe caffe : data) {
            caffe = data.get(position);

            // Setting all values in listview
            naziv.setText(caffe.naziv);
            adresa.setText(caffe.adresa);
            //GetImageTask getImageTask = new GetImageTask(caffe);
            //getImageTask.execute();



        }

        return vi;
    }

    private class GetImageTask extends AsyncTask<Void, Void, String>{

        Caffe caffe;

        public GetImageTask(Caffe caffe){
            this.caffe = caffe;
        }

        @Override
        protected void onPreExecute() {
            logo = (ImageView) vi.findViewById(R.id.logo);
        }

        @Override
        protected String doInBackground(Void... params) {
            return "";
        }
    }
}