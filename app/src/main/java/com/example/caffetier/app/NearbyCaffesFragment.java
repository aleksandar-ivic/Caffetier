package com.example.caffetier.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.caffetier.app.domain.Caffe;
import com.example.caffetier.app.domain.Opstina;
import com.example.caffetier.app.util.MyHttpClient;
import com.example.caffetier.app.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class NearbyCaffesFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    ArrayList<Caffe> allCaffes;
    ProgressDialog mProgressDialog;
    GoogleMap googleMap;
    LocationClient locationClient;
    View rootView;

    public NearbyCaffesFragment() {
        allCaffes = new ArrayList<Caffe>();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }



        if (isConnected()) {
            mProgressDialog = new ProgressDialog(getActivity(), R.style.MyProgressDialog);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            new HttpAsyncTask().execute("https://178.33.216.114/kafici/");
        }


        locationClient = new LocationClient(getActivity(), this, this);


        try{
            rootView = inflater.inflate(R.layout.fragment_nearby_caffes, container, false);
        } catch (InflateException ex){
            setUpMapIfNeeded();
            return rootView;
        }

        setUpMapIfNeeded();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (googleMap != null)
            setUpMap();

        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null)
                setUpMap();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        locationClient.connect();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (googleMap != null) {
            MainActivity.fragmentManager.beginTransaction()
                    .remove(MainActivity.fragmentManager.findFragmentById(R.id.location_map_all)).commit();
            googleMap = null;
        }

    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.location_map_all)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null)
                setUpMap();
        }
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
    }


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {
        Toast.makeText(getActivity(), "Map Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
    }

    private class HttpAsyncTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            String result = GET(urls[0]);
            JSONObject json = null;
            allCaffes = new ArrayList<Caffe>();
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    int opstinaID = json.getInt("opstina");
                    String nazivOpstine = "";
                    for (Opstina opstina : Util.allAreas) {
                        if (opstinaID == opstina.id) {
                            nazivOpstine = opstina.naziv;
                        }
                    }
                    String naziv = json.getString("naziv");
                    String adresa = json.getString("adresa");
                    int logoID = R.drawable.redbar;
                    String logoURL = "C:\\Users\\Aleksandar\\AndroidStudioProjects\\Caffetier\\app\\src\\main\\res\\drawable\\redbar.png";
                    double lat = Double.parseDouble(json.getString("latitude"));
                    double lng = Double.parseDouble(json.getString("longitude"));
                    Opstina opstina = new Opstina(opstinaID, nazivOpstine);
                    String url = json.getString("json_url");
                    Caffe caffe = new Caffe(naziv, adresa, opstina, url, logoURL, logoID, lat, lng);
                    allCaffes.add(caffe);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Ucitavanje kafica u vasoj blizini");
            mProgressDialog.show();
        }

        // onPostExecute displays the results of the AsyncTask.

        @Override
        protected void onPostExecute(Boolean result) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (result == true) {

                Location mylocation = locationClient.getLastLocation();
                LatLng myLatLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(myLatLng).zoom(17).bearing(90).tilt(30).build();

                for (Caffe caffe : allCaffes) {
                    LatLng latLng = new LatLng(caffe.lat, caffe.lng);
                    String naziv = caffe.naziv;
                    MarkerOptions marker = new MarkerOptions().position(latLng).title(naziv);
                    googleMap.addMarker(marker);
                }
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));

            }

        }
    }
}
