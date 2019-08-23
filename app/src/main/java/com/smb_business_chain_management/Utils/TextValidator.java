package com.smb_business_chain_management.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public abstract class TextValidator implements TextWatcher {
    private final TextView textView;

    public TextValidator(TextView textView) {
        this.textView = textView;
    }

    public abstract void validate(TextView textView, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        validate(textView, text);
        textView.removeTextChangedListener(this);
        text = AppUtils.formattedStringMoneyString(text);
        textView.setText(text);
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
