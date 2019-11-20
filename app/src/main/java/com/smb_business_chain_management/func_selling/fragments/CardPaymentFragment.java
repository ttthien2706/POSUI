package com.smb_business_chain_management.func_selling.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.TextValidator;
import com.smb_business_chain_management.func_selling.OrderListenerInterface;
import com.smb_business_chain_management.func_selling.PaymentDialog;
import com.smb_business_chain_management.func_selling.SellingActivity;

import java.math.BigInteger;

public class CardPaymentFragment extends Fragment implements SellingActivity.OrderTotalListener {
    private static final String TAG = CashPaymentFragment.class.getSimpleName();
    TextView totalAmountTextView;
    //    TextView receivedAmountTextInput;
    TextInputEditText receivedAmountTextInput;
    TextView changeAmountTextView;
    ImageView imageView;
    Button doneButton;
    //    Button saveButton;
    Button cancelButton;

    private Context mContext;
    BigInteger orderTotal = BigInteger.ZERO;

    private OrderListenerInterface listener;
    private Button.OnClickListener onDoneButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            listener.completeOrderAndSubmit(orderTotal.toString(), "0", changeAmountTextView, "card");
            ((PaymentDialog) getParentFragment()).dismiss();
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

    public CardPaymentFragment(){}

    public static CardPaymentFragment newInstance(BigInteger orderTotal) {
        CardPaymentFragment fragment = new CardPaymentFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable("orderTotal", orderTotal);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderTotal = (BigInteger) getArguments().getSerializable("orderTotal");
        ((SellingActivity) mContext).setOrderTotalListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_payment_fragment, container, false);
        viewLookup(view);
        setupViews();
//        changeListener();
        return view;
    }

    private  void viewLookup(View view){
        totalAmountTextView = view.findViewById(R.id.total);
//        receivedAmountTextInput = view.findViewById(R.id.receivedInput);
//        receivedAmountTextInput = view.findViewById(R.id.receivedInputEditText);
//        changeAmountTextView = view.findViewById(R.id.change);
        imageView = view.findViewById(R.id.imageCash);
        doneButton = view.findViewById(R.id.done);
//        saveButton = view.findViewById(R.id.save);
        cancelButton = view.findViewById(R.id.cancel);
    }
    private void setupViews(){
//        receivedAmountTextInput.setText("0");
//        TextValidator quantityTextWatcher = new TextValidator(receivedAmountTextInput) {
//            @Override
//            public void validate(TextView textView, String text) {
//                changeListener();
//            }
//        };
//        receivedAmountTextInput.addTextChangedListener(quantityTextWatcher);
//        changeAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_payment_cash_change, AppUtils.formattedBigIntegerMoneyString(calculateChange()))));
        orderTotal = ((SellingActivity) mContext).calculateOrderTotalPriceNumber();
        totalAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_total, AppUtils.formattedBigIntegerMoneyString(orderTotal))));
//        toggleButton(saveButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
//        toggleButton(doneButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
        doneButton.setOnClickListener(onDoneButtonClicked);
//        saveButton.setOnClickListener(onSaveButtonClicked);
        cancelButton.setOnClickListener(onCancelButtonClicked);
    }

//    private void changeListener(){
//        BigInteger change = calculateChange();
//        if ((change.compareTo(BigInteger.ZERO) < 0)) {
//            changeAmountTextView.setText(R.string.order_payment_cash_received_invalid);
//            toggleButton(doneButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
//        }
//        else {
//            changeAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_payment_cash_change, AppUtils.formattedBigIntegerMoneyString(calculateChange()))));
//            if (true/*SellingActivity.IS_PRINTER_CONNECTED*/) {
//                toggleButton(doneButton, R.drawable.pill_button, android.R.color.black, true);
//            }
//        }
//        if (orderTotal.compareTo(BigInteger.ZERO) < 1) {
//            Log.d(TAG, "empty order");
//            toggleButton(doneButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
////            toggleButton(saveButton, R.drawable.inactive_pill_button, R.color.VectorColorActiveUnfocused, false);
//        }
//        else {
//            Log.d(TAG, "savable order");
////            toggleButton(saveButton, R.drawable.pill_button, R.color.white, true);
//        }
//    }

    private void toggleButton(Button button, int drawable, int color, boolean isEnabled) {
        button.setBackground(getResources().getDrawable(drawable, mContext.getTheme()));
        button.setTextColor(getResources().getColor(color, mContext.getTheme()));
        button.setCompoundDrawableTintList(getResources().getColorStateList(color, mContext.getTheme()));
        button.setEnabled(isEnabled);
    }

//    private BigInteger calculateChange() {
//        String receivedString = AppUtils.removeFormattedDot(receivedAmountTextInput.getText().toString());
//        BigInteger received;
//
//        if (receivedString.equals("")) received = BigInteger.ZERO;
//        else received = new BigInteger(AppUtils.removeFormattedDot(receivedAmountTextInput.getText().toString()));
//
//        return received.subtract(orderTotal);
//    }

    @Override
    public void getOrderTotal(BigInteger orderTotal) {
        this.orderTotal = orderTotal;
        totalAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_total, AppUtils.formattedBigIntegerMoneyString(orderTotal))));
        if (orderTotal.compareTo(BigInteger.ZERO) >= 1) {
//            changeListener();
        }
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
