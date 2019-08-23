package com.smb_business_chain_management.Utils;

import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;

public abstract class MoneyTextWatcher implements TextWatcher {
    private final TextInputEditText textView;

    public MoneyTextWatcher(TextInputEditText textView) {
        this.textView = textView;
    }

    public abstract void validate(TextInputEditText textView, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        validate(textView, text);
        textView.removeTextChangedListener(this);
        text = AppUtils.formattedStringMoneyString(text);
        textView.setText(text);
        textView.setSelection(text.length());
        textView.addTextChangedListener(this);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        String text = textView.getText().toString();
//        validate(textView, text);
    }



    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {
//        String text = textView.getText().toString();
//        validate(textView, text);
    }
}
