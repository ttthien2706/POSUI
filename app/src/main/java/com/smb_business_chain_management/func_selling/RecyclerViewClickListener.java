package com.smb_business_chain_management.func_selling;

import android.content.res.ColorStateList;
import android.view.View;

public interface RecyclerViewClickListener {
    void recyclerViewListClickListener(View v, int position, ColorStateList mTextColor);
    void recyclerViewListClickListener(View v, int position);
}
