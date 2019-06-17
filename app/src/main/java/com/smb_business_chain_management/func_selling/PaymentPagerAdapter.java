package com.smb_business_chain_management.func_selling;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.func_selling.fragments.CashPaymentFragment;

class PaymentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public PaymentPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return CashPaymentFragment.newInstance();
            case 1:
                return CashPaymentFragment.newInstance();
            case 2:
                return CashPaymentFragment.newInstance();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getResources().getString(R.string.order_payment_cash_label);
            case 1:
                return mContext.getResources().getString(R.string.order_payment_card_label);
            case 2:
                return mContext.getResources().getString(R.string.order_payment_code_label);
        }
        return "";
    }


}
