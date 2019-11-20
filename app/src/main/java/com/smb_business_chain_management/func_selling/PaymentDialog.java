package com.smb_business_chain_management.func_selling;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smb_business_chain_management.R;

import java.math.BigInteger;

public class PaymentDialog extends DialogFragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    PaymentPagerAdapter viewPagerAdapter;
    BigInteger orderTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.payment_dialog, container, false);
        viewLookup(dialog);
        pagerSetup(viewPager);
        tabSetup(tabLayout);
        return  dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderTotal = (BigInteger) getArguments().getSerializable("orderTotal");
    }

    @Override
    public void onResume() {
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
        super.onResume();
    }

    private void viewLookup(View dialog){
        tabLayout = dialog.findViewById(R.id.tabLayout);
        viewPager = dialog.findViewById(R.id.paymentViewPager);
    }

    private void pagerSetup(ViewPager viewPager){
        viewPagerAdapter = new PaymentPagerAdapter(getActivity(), getChildFragmentManager(), orderTotal);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void tabSetup(TabLayout tabLayout){
        tabLayout.setupWithViewPager(viewPager);
    }
}
