package com.smb_business_chain_management;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smb_business_chain_management.model.Store;

import java.util.List;

import static android.content.ContentValues.TAG;
import static com.smb_business_chain_management.Settings.utils;

class StoresRecyclerViewAdapter extends RecyclerView.Adapter<StoresRecyclerViewAdapter.storesViewHolder> {
    private Context context;
    private List<Store> storeList;
    private FragmentManager fragmentManager;

    class MenuItemListener implements View.OnClickListener {
        private int position;
        Store data;
        FragmentManager fragmentManager;

        MenuItemListener(int position, Store data, FragmentManager fragmentManager) {
            this.position = position;
            this.data = data;
            this.fragmentManager = fragmentManager;
        }

        @SuppressLint("UseValueOf")
        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.store_card_edit_pupup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.d(TAG, Integer.toString(position));
                    switch (menuItem.getTitle().toString().toLowerCase()){
                        case "edit":{
                            Bundle storeBundle = new Bundle();
                            storeBundle.putString("name", data.getName());
                            storeBundle.putString("address", data.getAddress());
                            storeBundle.putString("phone", data.getPhone());
                            storeBundle.putBoolean("isActive", data.isActive());
                            storeBundle.putLong("cityId", data.getCityId());
                            storeBundle.putLong("districtId", data.getDistrictId());
                            storeBundle.putLong("wardId", data.getWardId());

                            EditStoreDialogFragment editDialog = new EditStoreDialogFragment();

                            editDialog.setArguments(storeBundle);
                            editDialog.show(fragmentManager, "editDialog");

                            break;
                        }
                        case "delete":{
                            break;
                        }
                    }
                    return true;
                }
            });
            popup.show();
        }
    }

    public static class storesViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        public TextView storeName, storePhoneNo, storeAddress, storeActiveStaff, storeActive;
        private ImageButton editStoreButton;
        private GoogleMap mMap;
        private MapView mapView;

        public storesViewHolder(View v){
            super(v);
            storeName = (TextView) v.findViewById(R.id.StoreName);
            storePhoneNo = (TextView) v.findViewById(R.id.StorePhoneNo);
            storeAddress = (TextView) v.findViewById(R.id.AddressTextView);
            storeActiveStaff = (TextView) v.findViewById(R.id.ActiveStaff);
            storeActive = (TextView) v.findViewById(R.id.IsActive);
            editStoreButton = (ImageButton) v.findViewById(R.id.EditButton) ;
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

            LatLng storeLocation = utils.getLocationFromAddress(storeActive.getContext(), storeAddress.getText().toString());
            if (storeLocation != null){
                Marker TP = mMap.addMarker(new MarkerOptions().position(storeLocation).title(storeName.getText().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(storeLocation));
            } else{
                Toast.makeText(storeAddress.getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public StoresRecyclerViewAdapter(Context context, List<Store> myData, FragmentManager fragmentManager){
        this.storeList = myData;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public storesViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View storeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_card_view, parent, false);

        return new storesViewHolder(storeView);
    }

    @Override
    public void onBindViewHolder(storesViewHolder holder, int position){
        Store store = storeList.get(position);
        holder.storeName.setText(store.getName());
        holder.storePhoneNo.setText(store.getPhone());
        try {
            holder.storeAddress.setText(store.getAddress());
        } catch (NullPointerException e) {
//            holder.storeAddress.setText('0');
            e.printStackTrace();
        }
        try {
            holder.storeActiveStaff.setText(store.getAmountUser().toString());
        } catch (NullPointerException e) {
//            holder.storeActiveStaff.setText(0);
            e.printStackTrace();
        }
        if (store.isActive()){
            holder.storeActive.setText(R.string.activeStore);
            Drawable drawable = holder.storeActive.getCompoundDrawables()[0];
            drawable.setTint(ContextCompat.getColor(holder.storeActive.getContext(), R.color.colorStoreActive));
        } else{
            holder.storeActive.setText(R.string.inActiveStore);
            Drawable drawable = holder.storeActive.getCompoundDrawables()[0];
            drawable.setTint(ContextCompat.getColor(holder.storeActive.getContext(), R.color.colorStoreInActive));
        }
        holder.editStoreButton.setOnClickListener(new MenuItemListener(position, store, fragmentManager));
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }
}
