package com.smb_business_chain_management;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.InputStream;
import java.util.Map;

public class StoreCardView extends Fragment implements OnMapReadyCallback{
    private GoogleMap mMap;
    private MapView mapView;

    public TextView storeName;
    public TextView storePhoneNo;
    public TextView storeAddress;
    public TextView storeActiveStaff;
    public TextView storeIsActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.store_card_view, container, false);

        mapView = (MapView) returnView.findViewById(R.id.mapPreview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return returnView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity().getApplicationContext());
        mMap = googleMap;
        mMap.setPadding(125, 0, 0, 0 );
        double latEiffelTower = 48.858235;
        double lngEiffelTower = 2.294571;
        LatLng EiffelTowerLatLng = new LatLng(latEiffelTower, lngEiffelTower);
        Marker TP = mMap.addMarker(new MarkerOptions().position(EiffelTowerLatLng).title("Shop Map"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(EiffelTowerLatLng));
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}