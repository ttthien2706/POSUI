package com.smb_business_chain_management.views;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.smb_business_chain_management.R;

public class BaseActivity extends AppCompatActivity {
    private static final int HAMBURGER_DRAWABLE = R.drawable.ic_menu_24px;
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
        boolean isParentRoot = this.getIntent().getBooleanExtra("isParentRoot", false);
        if (getSupportParentActivityIntent() == null) {
            if (isTaskRoot()){
                actionBar.setHomeAsUpIndicator(HAMBURGER_DRAWABLE);
            }
        }
        else if(isParentRoot){
            actionBar.setHomeAsUpIndicator(HAMBURGER_DRAWABLE);
        }
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
                if (mDrawerLayout == null) return false;
                if (mDrawerLayout.isDrawerOpen(findViewById(R.id.navView))){
                    mDrawerLayout.closeDrawers();
                }
                else mDrawerLayout.openDrawer(Gravity.START);
        }
        return true;
    }

//    protected void setSearchActionBar() {
//        try {
//            actionBar = getSupportActionBar();
//            actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.white)));
//            actionBar.setElevation(R.dimen.search_actionbar_default_elevation);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
}
