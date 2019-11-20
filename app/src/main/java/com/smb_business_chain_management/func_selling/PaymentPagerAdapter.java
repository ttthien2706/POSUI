package com.smb_business_chain_management.func_selling;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.func_selling.fragments.CardPaymentFragment;
import com.smb_business_chain_management.func_selling.fragments.CashPaymentFragment;

import java.math.BigInteger;

class PaymentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    BigInteger mOrderTotal;

    public PaymentPagerAdapter(Context context, FragmentManager fragmentManager, BigInteger orderTotal) {
        super(fragmentManager);
        mContext = context;
        mOrderTotal = orderTotal;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return CashPaymentFragment.newInstance(mOrderTotal);
            case 1:
                return CardPaymentFragment.newInstance(mOrderTotal);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getResources().getString(R.string.order_payment_cash_label);
            case 1:
                return "Thẻ";
        }
        return "";
    }


}
