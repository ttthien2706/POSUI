package com.smb_business_chain_management.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.smb_business_chain_management.R;

public class BaseActivity extends AppCompatActivity {
    private static final int HAMBURGER_DRAWABLE = R.drawable.ic_menu_24px;
    public static final int NAV_MENU_SELLING_ID = 0;
    public static final int NAV_MENU_RETURN_ID = 1;
    public static final int NAV_MENU_PRODUCT_ID = 2;
    public static final int NAV_MENU_SETTINGS_ID = 3;
    private DrawerLayout mDrawerLayout;

    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(R.dimen.cardview_default_elevation);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                mDrawerLayout = findViewById(R.id.drawerLayout);
                mDrawerLayout.setScrimColor(Color.TRANSPARENT);
                if (mDrawerLayout == null) return false;
                if (mDrawerLayout.isDrawerOpen(findViewById(R.id.navView))){
                    mDrawerLayout.closeDrawers();
                }
                else mDrawerLayout.openDrawer(Gravity.START);
        }
        return true;
    }
}
