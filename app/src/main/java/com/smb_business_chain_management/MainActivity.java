package com.smb_business_chain_management;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.smb_business_chain_management.views.BaseActivity;

import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.app_title));
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        CardView menuSettings = findViewById(R.id.settingsMenu);
        CardView menuProducts = findViewById(R.id.productsMenu);
        menuSettings.setOnClickListener(new MenuIconListener());
        menuProducts.setOnClickListener(new MenuIconListener());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    class MenuIconListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.newOrderMenu:
                    break;
                case R.id.returnOrderMenu:
                    break;
                case R.id.importOrderMenu:
                    break;
                case R.id.exportOrderMenu:
                    break;
                case R.id.customersMenu:
                    break;
                case R.id.productsMenu:
                    intent = new Intent(view.getContext(), ProductActivity.class);
                    view.getContext().startActivity(intent);
                    break;
                case R.id.reportMenu:
                    break;
                case R.id.settingsMenu:
                    intent = new Intent(view.getContext(), SettingsActivity.class);
                    intent.putExtra("isParentRoot", isTaskRoot());
                    view.getContext().startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
}
