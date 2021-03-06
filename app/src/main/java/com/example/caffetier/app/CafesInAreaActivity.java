package com.example.caffetier.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.caffetier.app.adapter.LazyAdapter;
import com.example.caffetier.app.domain.Kafic;
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

/**
 * Created by Aleksandar on 23-Jul-14.
 */
//ubaci da se cuva stanje listViewa
public class CafesInAreaActivity extends FragmentActivity {

    ListView list;
    LazyAdapter lazyAdapter;
    ArrayList<Kafic> allCafes;
    ProgressDialog mProgressDialog;
    Opstina izabranaOpstina;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caffes_in_area);

        Util.setActionBarTitleFont(this);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Caffetier");

        Bundle b = getIntent().getExtras();
        if(b!=null && b.containsKey("Opstina")){
            izabranaOpstina = b.getParcelable("Opstina");
        }


        if (isConnected()) {
            mProgressDialog = new ProgressDialog(this, R.style.MyProgressDialog);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            new HttpAsyncTask(this).execute("https://178.33.216.114/kafici/");
        }


    }

    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new MyHttpClient(getApplicationContext());

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
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, String, Boolean> {

        CafesInAreaActivity activity;

        public HttpAsyncTask(CafesInAreaActivity allcaffesInAreaActivity) {
            activity = allcaffesInAreaActivity;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String result = GET(urls[0]);
            JSONObject json = null;
            allCafes = new ArrayList<Kafic>();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    int opstinaID = json.getInt("opstina");
                    String nazivOpstine = "";
                    if (opstinaID == izabranaOpstina.id){
                        nazivOpstine = izabranaOpstina.naziv;
                        String naziv = json.getString("naziv");
                        String adresa = json.getString("adresa");
                        String logoURL = "https://178.33.216.114/get_logo/?title=" + json.getString("logo") + ".png";
                        double lat = Double.parseDouble(json.getString("latitude"));
                        double lng = Double.parseDouble(json.getString("longitude"));
                        Opstina opstina = new Opstina(opstinaID, nazivOpstine);
                        String url = json.getString("json_url");
                        Kafic cafe = new Kafic(naziv, adresa, opstina, url, logoURL, 0, lat, lng);
                        allCafes.add(cafe);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Ucitavanje kafica u opstini " + izabranaOpstina.naziv);
            mProgressDialog.show();
            list = (ListView) findViewById(R.id.list);



        }

        // onPostExecute displays the results of the AsyncTask.

        @Override
        protected void onPostExecute(Boolean result) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (result == true) {
                lazyAdapter = new LazyAdapter(activity, allCafes);
                list.setAdapter(lazyAdapter);


                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), OneCafeActivity.class);
                        intent.putExtra("Naziv", allCafes.get(position).naziv);
                        intent.putExtra("Adresa", allCafes.get(position).adresa);
                        intent.putExtra("Logo", allCafes.get(position).logoID);
                        intent.putExtra("Latitude", allCafes.get(position).lat);
                        intent.putExtra("Longitude", allCafes.get(position).lng);
                        startActivity(intent);
                    }
                });
            }

        }
    }




}

