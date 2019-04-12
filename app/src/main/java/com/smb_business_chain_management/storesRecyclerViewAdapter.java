package com.smb_business_chain_management;

import android.content.Context;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class storesRecyclerViewAdapter extends RecyclerView.Adapter<storesRecyclerViewAdapter.storesViewHolder> {

    private Context context;
    private List<Store> storeList;

    public static class storesViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        public TextView storeName, storePhoneNo, storeAddress, storeActiveStaff, storeActive;
        private GoogleMap mMap;
        private MapView mapView;

        public storesViewHolder(View v){
            super(v);
            storeName = (TextView) v.findViewById(R.id.StoreName);
            storePhoneNo = (TextView) v.findViewById(R.id.StorePhoneNo);
            storeAddress = (TextView) v.findViewById(R.id.AddressTextView);
            storeActiveStaff = (TextView) v.findViewById(R.id.ActiveStaff);
            storeActive = (TextView) v.findViewById(R.id.IsActive);
            mapView = (MapView) v.findViewById(R.id.mapPreview);
            if (mapView != null){
                mapView.onCreate(null);
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
//            mMap.setPadding(300, 0, 0, 0 );
            mMap.getUiSettings().setMapToolbarEnabled(false);

            LatLng storeLocation = getLocationFromAddress(storeActive.getContext(), storeAddress.getText().toString());
            Marker TP = mMap.addMarker(new MarkerOptions().position(storeLocation).title(storeName.getText().toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(storeLocation));
        }

        public LatLng getLocationFromAddress(Context context, String sAddress){
            Geocoder coder = new Geocoder(context);
            List<Address> addresses;
            LatLng returnLatLng = null;
            if (sAddress.length() < 1){
                return null;
            }
            else{
                try{
                    addresses = coder.getFromLocationName(sAddress, 5);
                    if (addresses == null){
                        return null;
                    }

                    Address location = addresses.get(0);
                    returnLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                }
                catch (IOException io){
                    io.printStackTrace();
                }
            }
            return returnLatLng;
        }

    }

    public storesRecyclerViewAdapter(Context context, List<Store> myData){
        this.storeList = myData;
        this.context = context;
    }

    @Override
    public storesViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View storeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_card_view, parent, false);

        return new storesViewHolder(storeView);
    }

    @Override
    public void onBindViewHolder(storesViewHolder holder, int position){
        Store store = storeList.get(position);
        holder.storeName.setText(store.getStoreName());
        holder.storePhoneNo.setText(store.getStorePhoneNumber());
        holder.storeAddress.setText(store.getStoreAddress());
        holder.storeActiveStaff.setText(store.getStoreActiveStaff().toString());
        if (store.isStoreActive()){
            holder.storeActive.setText(R.string.activeStore);
            Drawable drawable = holder.storeActive.getCompoundDrawables()[0];
            drawable.setTint(ContextCompat.getColor(holder.storeActive.getContext(), R.color.colorStoreActive));
        } else{
            holder.storeActive.setText(R.string.inActiveStore);
            Drawable drawable = holder.storeActive.getCompoundDrawables()[0];
            drawable.setTint(ContextCompat.getColor(holder.storeActive.getContext(), R.color.colorStoreInActive));
        }
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }
}
