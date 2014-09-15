package com.example.caffetier.app;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class CafeLocationFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    GoogleMap googleMap;
    LocationClient locationClient;
    String adresa;
    String naziv;
    LatLng latLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        naziv = intent.getStringExtra("Naziv");
        adresa = intent.getStringExtra("Adresa");
        double lat = intent.getDoubleExtra("Latitude", 0);
        double lng = intent.getDoubleExtra("Longitude", 0);

        latLng = new LatLng(lat, lng);

        View rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_caffe_location, container, false);
        googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.location_map)).getMap();

        locationClient = new LocationClient(getActivity(), this, this);
        setUpMapIfNeeded();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        locationClient.connect();
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) OneCafeActivity.fragmentManager
                    .findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null)
                setUpMap();
        }
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
        /*
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 12.0f));*/


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (googleMap != null)
            setUpMap();

        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) OneCafeActivity.fragmentManager
                    .findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null)
                setUpMap();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition myPosition = new CameraPosition.Builder()
                .target(latLng).zoom(17).bearing(90).tilt(30).build();
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(naziv));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(getActivity(), "Map Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
    }

    public LatLng getLatLngFromAdress(String strAddress) {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        LatLng p1 = null;
        try

        {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }
}
