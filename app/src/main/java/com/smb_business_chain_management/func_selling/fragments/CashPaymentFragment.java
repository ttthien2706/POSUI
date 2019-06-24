package com.smb_business_chain_management.func_selling.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.func_selling.OrderListenerInterface;
import com.smb_business_chain_management.func_selling.PaymentDialog;
import com.smb_business_chain_management.func_selling.SellingActivity;

import java.math.BigInteger;

public class CashPaymentFragment extends Fragment implements SellingActivity.OrderTotalListener{
    private static final String TAG = CashPaymentFragment.class.getSimpleName();
    TextView totalAmountTextView;
    TextView receivedAmountTextInput;
    TextView changeAmountTextView;
    ImageView imageView;
    Button doneButton;
    Button saveButton;
    Button cancelButton;

    // keyboard keys (buttons)
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton00;
    private Button mButton000;
    private Button mButton0;
    private AppCompatImageButton mButtonDelete;
    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    SparseArray<String> keyValues = new SparseArray<>();

    private Context mContext;
    BigInteger orderTotal = BigInteger.ZERO;

    private OrderListenerInterface listener;
    private Button.OnClickListener onDoneButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            listener.completeOrderAndSubmit(AppUtils.removeFormattedDot(receivedAmountTextInput.getText().toString()), calculateChange().toString());
//            imageView.setImageResource(R.drawable.ic_payment_done);
//            imageView.setColorFilter(getResources().getColor(R.color.colorStoreActive, mContext.getTheme()));
            receivedAmountTextInput.setText("0");
        }
    };
    private Button.OnClickListener onCancelButtonClicked = v -> {
        try {
            ((PaymentDialog) getParentFragment()).dismiss();
        } catch (NullPointerException e) {
            try {
                getActivity().finish();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    };
    private Button.OnClickListener onSaveButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.saveAndClearOrder();
            changeAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_payment_cash_change, "0")));
            receivedAmountTextInput.setText("0");
        }
    };
    private View.OnClickListener keyboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String oldText = AppUtils.removeFormattedDot(receivedAmountTextInput.getText().toString());
            if (v.getId() == R.id.button_backspace) {
                if (!oldText.isEmpty()) receivedAmountTextInput.setText(AppUtils.formattedStringMoneyString(oldText.substring(0, oldText.length() - 1)));
            } else {
                String value = keyValues.get(v.getId());
                receivedAmountTextInput.setText(AppUtils.formattedStringMoneyString(oldText.concat(value)));
            }
            if (orderTotal.compareTo(BigInteger.ZERO) >= 1) {
                changeListener();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OrderListenerInterface) context;
            mContext = context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement OrderListenerInterface");
        }
    }

    public CashPaymentFragment(){}

    public static CashPaymentFragment newInstance() {
        CashPaymentFragment fragment = new CashPaymentFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SellingActivity) mContext).setOrderTotalListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cash_payment_fragment, container, false);
        viewLookup(view);
        setupViews();
        initKeyboard(view);
        return view;
    }

    private  void viewLookup(View view){
        totalAmountTextView = view.findViewById(R.id.total);
        receivedAmountTextInput = view.findViewById(R.id.receivedInput);
        changeAmountTextView = view.findViewById(R.id.change);
        imageView = view.findViewById(R.id.imageCash);
        doneButton = view.findViewById(R.id.done);
        saveButton = view.findViewById(R.id.save);
        cancelButton = view.findViewById(R.id.cancel);
    }
    private void setupViews(){
        receivedAmountTextInput.setText("0");
        changeAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_payment_cash_change, AppUtils.formattedBigIntegerMoneyString(calculateChange()))));
        orderTotal = ((SellingActivity) mContext).calculateOrderTotalPriceNumber();
        totalAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_total, AppUtils.formattedBigIntegerMoneyString(orderTotal))));
        toggleButton(saveButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
        toggleButton(doneButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
        doneButton.setOnClickListener(onDoneButtonClicked);
        saveButton.setOnClickListener(onSaveButtonClicked);
        cancelButton.setOnClickListener(onCancelButtonClicked);
    }
    private void initKeyboard(View view){
        mButton1 = view.findViewById(R.id.button_1);
        mButton2 = view.findViewById(R.id.button_2);
        mButton3 = view.findViewById(R.id.button_3);
        mButton4 = view.findViewById(R.id.button_4);
        mButton5 = view.findViewById(R.id.button_5);
        mButton6 = view.findViewById(R.id.button_6);
        mButton7 = view.findViewById(R.id.button_7);
        mButton8 = view.findViewById(R.id.button_8);
        mButton9 = view.findViewById(R.id.button_9);
        mButton00 = view.findViewById(R.id.button_00);
        mButton000 = view.findViewById(R.id.button_000);
        mButton0 = view.findViewById(R.id.button_0);
        mButtonDelete = view.findViewById(R.id.button_backspace);

        // set button click listeners
        mButton1.setOnClickListener(keyboardListener);
        mButton2.setOnClickListener(keyboardListener);
        mButton3.setOnClickListener(keyboardListener);
        mButton4.setOnClickListener(keyboardListener);
        mButton5.setOnClickListener(keyboardListener);
        mButton6.setOnClickListener(keyboardListener);
        mButton7.setOnClickListener(keyboardListener);
        mButton8.setOnClickListener(keyboardListener);
        mButton9.setOnClickListener(keyboardListener);
        mButton00.setOnClickListener(keyboardListener);
        mButton000.setOnClickListener(keyboardListener);
        mButton0.setOnClickListener(keyboardListener);
        mButtonDelete.setOnClickListener(keyboardListener);

        // map buttons IDs to input strings
        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_00, "00");
        keyValues.put(R.id.button_000, "000");
        keyValues.put(R.id.button_0, "0");
    }


    private void changeListener(){
        BigInteger change = calculateChange();
        if ((change.compareTo(BigInteger.ZERO) < 0)) {
            changeAmountTextView.setText(R.string.order_payment_cash_received_invalid);
            toggleButton(doneButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
        }
        else {
            changeAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_payment_cash_change, AppUtils.formattedBigIntegerMoneyString(calculateChange()))));
            if (SellingActivity.IS_PRINTER_CONNECTED) {
                toggleButton(doneButton, R.drawable.pill_button, android.R.color.black, true);
            }
        }
        if (orderTotal.compareTo(BigInteger.ZERO) < 1) {
            Log.d(TAG, "empty order");
            toggleButton(doneButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
            toggleButton(saveButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
        }
        else {
            Log.d(TAG, "savable order");
            toggleButton(saveButton, R.drawable.pill_button, R.color.white, true);
        }
    }

    private void toggleButton(Button button, int drawable, int color, boolean isEnabled) {
        button.setBackground(getResources().getDrawable(drawable, mContext.getTheme()));
        button.setTextColor(getResources().getColor(color, mContext.getTheme()));
        button.setCompoundDrawableTintList(getResources().getColorStateList(color, mContext.getTheme()));
        button.setEnabled(isEnabled);
    }

    private BigInteger calculateChange() {
        String receivedString = AppUtils.removeFormattedDot(receivedAmountTextInput.getText().toString());
        BigInteger received;

        if (receivedString.equals("")) received = BigInteger.ZERO;
        else received = new BigInteger(AppUtils.removeFormattedDot(receivedAmountTextInput.getText().toString()));

        return received.subtract(orderTotal);
    }

    @Override
    public void getOrderTotal(BigInteger orderTotal) {
        this.orderTotal = orderTotal;
        totalAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_total, AppUtils.formattedBigIntegerMoneyString(orderTotal))));
        if (orderTotal.compareTo(BigInteger.ZERO) >= 1) {
            changeListener();
        }
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
