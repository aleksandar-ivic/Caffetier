package com.example.caffetier.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.caffetier.app.adapter.MySpinnerAdapter;
import com.example.caffetier.app.domain.Opstina;
import com.example.caffetier.app.util.MyHttpClient;
import com.example.caffetier.app.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HomeFragment extends Fragment {

    Spinner spinner;
    ImageView imageViewLocationPin;
    ArrayList<Opstina> opstine;
    ProgressDialog mProgressDialog;

    public HomeFragment() {
    }
/*
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        super.onCreateView(parent, name, context, attrs);
        setContentView(R.layout.fragment_home);

        Util.setActionBarTitleFont(this);


        TextView textView1 = (TextView) parent.findViewById(R.id.textView);
        TextView textView2 = (TextView) parent.findViewById(R.id.textView2);

        textView1.setTypeface(Util.FONT_HEADINGS);
        textView2.setTypeface(Util.FONT_HEADINGS);



        mProgressDialog = new ProgressDialog(this, R.style.MyProgressDialog);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);


        imageViewLocationPin = (ImageView) parent.findViewById(R.id.imageView);
        imageViewLocationPin.setImageResource(R.drawable.pin);
        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this
                , R.anim.fadeout);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageViewLocationPin.startAnimation(animationFadeIn);
                imageViewLocationPin.startAnimation(animationFadeOut);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


        if (isConnected()) {
            new HttpAsyncTask(this, parent).execute("https://178.33.216.114/opstine/");
        }


        Button button = (Button) parent.findViewById(R.id.buttonIzaberi);
        button.setTypeface(Util.FONT_HEADINGS);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CaffesInAreaActivity.class);
                Opstina izabranaOpstina = (Opstina) spinner.getSelectedItem();
                intent.putExtra("Opstina", izabranaOpstina);
                startActivity(intent);

            }
        });

        return parent;
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Util.setActionBarTitleFont(getActivity());


        TextView textView1 = (TextView) rootView.findViewById(R.id.textView);
        TextView textView2 = (TextView) rootView.findViewById(R.id.textView2);

        textView1.setTypeface(Util.FONT_HEADINGS);
        textView2.setTypeface(Util.FONT_HEADINGS);



        mProgressDialog = new ProgressDialog(getActivity(), R.style.MyProgressDialog);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);


        imageViewLocationPin = (ImageView) rootView.findViewById(R.id.imageView);
        imageViewLocationPin.setImageResource(R.drawable.pin);
        final Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(getActivity()
                , R.anim.fadeout);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageViewLocationPin.startAnimation(animationFadeIn);
                imageViewLocationPin.startAnimation(animationFadeOut);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


        if (isConnected()) {
            new HttpAsyncTask(getActivity(), rootView).execute("https://178.33.216.114/opstine/");
        }


        Button button = (Button) rootView.findViewById(R.id.buttonIzaberi);
        button.setTypeface(Util.FONT_HEADINGS);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CaffesInAreaActivity.class);
                Opstina izabranaOpstina = (Opstina) spinner.getSelectedItem();
                intent.putExtra("Opstina", izabranaOpstina);
                startActivity(intent);

            }
        });


        return rootView;
    }

    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new MyHttpClient(getActivity());

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, String, Boolean> {

        View rootView;
        Activity activity;

        public HttpAsyncTask(Activity activity, View rootView) {
            this.activity = activity;
            this.rootView = rootView;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String result = GET(urls[0]);
            JSONObject json = null; // convert String to JSONObject
            opstine = new ArrayList<Opstina>();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    int id = json.getInt("id");
                    String naziv = json.getString("naziv");
                    Opstina opstina = new Opstina(id, naziv);
                    opstine.add(opstina);
                    Util.allAreas.add(opstina);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Ucitavanje opstina");
            mProgressDialog.show();

            spinner = (Spinner) rootView.findViewById(R.id.spinner);


        }

        // onPostExecute displays the results of the AsyncTask.

        @Override
        protected void onPostExecute(Boolean result) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (result == true) {

                if (spinner != null) {
                    Context context = getActivity();
                    MySpinnerAdapter adapter = new MySpinnerAdapter(context, R.layout.spinner_item, opstine);
                    //adapter.setDropDownViewResource(R.layout.spinner_item);
                    spinner.setAdapter(adapter);
                }

            }

        }
    }
}
