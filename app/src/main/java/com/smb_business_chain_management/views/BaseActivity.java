package com.smb_business_chain_management.views;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.smb_business_chain_management.R;

public class BaseActivity extends AppCompatActivity {
    private static final int HAMBURGER_DRAWABLE = R.drawable.ic_menu_24px;

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
        if (getParent() == null) {
            if (isTaskRoot()){
                actionBar.setHomeAsUpIndicator(HAMBURGER_DRAWABLE);
            }
        }
        else if (getParent().isTaskRoot()){
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
        return super.onOptionsItemSelected(item);
    }
}
