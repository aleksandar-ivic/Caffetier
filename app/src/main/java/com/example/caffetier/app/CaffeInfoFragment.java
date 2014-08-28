package com.example.caffetier.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caffetier.app.util.MyHttpClient;
import com.example.caffetier.app.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;


public class CaffeInfoFragment extends Fragment {

    WebView webView;
    View rootView;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;
    Context context;
    Button button;

    public final String EXTRA_MESSAGE = "message";
    public final String PROPERTY_REG_ID = "registration_id";
    private final String PROPERTY_APP_VERSION = "appVersion";
    private static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "204476649954";
    static final String TAG = "CAFFETIER";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_caffe_info, container, false);
        context = getActivity().getApplicationContext();
        Util.setActionBarTitleFont(getActivity());

        Intent intent = getActivity().getIntent();
        String naziv = intent.getStringExtra("Naziv");
        String adresa = intent.getStringExtra("Adresa");
        int logo = intent.getIntExtra("Logo", 0);

        TextView nazivView = (TextView) rootView.findViewById(R.id.caffeNaziv);
        nazivView.setText(naziv);
        nazivView.setTypeface(Util.FONT_HEADINGS);
        TextView adresaView = (TextView) rootView.findViewById(R.id.caffeAdresa);
        adresaView.setText(adresa);
        adresaView.setTypeface(Util.FONT_HEADINGS);
        ImageView logoView = (ImageView) rootView.findViewById(R.id.caffeLogo);
        logoView.setImageResource(logo);
        Spinner freeTablesSpinner = (Spinner) rootView.findViewById(R.id.freeTablesSpinner);
        //ovde treba da ubacis slobodne stolove

        button = (Button) rootView.findViewById(R.id.sendReservation);
        button.setText("Send reservation");
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(getActivity());
                    regid = getRegistrationId(context);
                    Toast.makeText(getActivity(), "Regid: " + regid, Toast.LENGTH_LONG).show();
                    GetContentTask getContentTask = new GetContentTask();
                    getContentTask.execute(regid.isEmpty());
                } else {
                    Toast.makeText(getActivity(), "Nije se registrovao", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }

            }
        });

        Toast.makeText(getActivity(), "Ucitavanje mape kafica...", Toast.LENGTH_LONG).show();
        webView = (WebView) rootView.findViewById(R.id.tables);
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.setWebViewClient(new MyWebViewClient());
            MyWebInterface myWebInterface = new MyWebInterface(getActivity());
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.addJavascriptInterface(myWebInterface, "Android");
            webView.setPadding(0, 0, 0, 0);
            webView.setInitialScale(getScale());
            webView.loadUrl("file:///android_asset/html/tables.html");
            Toast.makeText(getActivity(), myWebInterface.json, Toast.LENGTH_LONG).show();
        }

        //ovde ide kod za rezervaciju
        //ako uredjaj nije kompatibilan sa Google Play Serviceom, onda izbaci poruku o tome i on ne moze da rezervise


        return rootView;
    }


    private void storeRegistrationId(Context context, String regid) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regid);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void sendRegistrationIdToBackend() {
        //Toast.makeText(getActivity(), "https://178.33.216.114/reservation?title=Bistro&table_number=1&id=" + regid, Toast.LENGTH_LONG).show();
        GET("https://178.33.216.114/reservation?title=Bistro&table_number=1&id=" + regid);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getActivity().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private int getScale() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width) / 1000;
        val = val * 100d;
        return val.intValue();
    }


    private class GetContentTask extends AsyncTask<Boolean, Void, String> {

        @Override
        protected String doInBackground(Boolean... params) {
            //boolean regidEmpty = params[0];
            String msg = "";
            if (regid.isEmpty()) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getActivity().getParent());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend();

                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }
            }/* else {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getActivity().getParent());
                }
                try {
                    gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend();

                    storeRegistrationId(context, regid);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }*/
            

            return msg;

        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            Toast.makeText(getActivity(), "Ucitana je mapa, mozete izvrsiti rezervaciju.", Toast.LENGTH_LONG).show();
            button.setEnabled(true);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            handler.proceed("branko", "sifra");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    private class MyWebInterface {
        Context context;
        String json;

        public MyWebInterface(Context context) {
            this.context = context;
        }

        public void setJson(String json){
            this.json = json;
        }

        @JavascriptInterface
        public int getWidth() {
            int width = webView.getWidth();
            return 1000;
        }

        @JavascriptInterface
        public int getHeight() {
            int height = webView.getHeight();
            return 500;
        }
    }

    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new MyHttpClient(getActivity().getApplicationContext());

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            Toast.makeText(getActivity(), "Uspeno poslao zahtev", Toast.LENGTH_LONG).show();
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Toast.makeText(getActivity(), "Uspeno stigao odgovor", Toast.LENGTH_LONG).show();
            } else
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}


