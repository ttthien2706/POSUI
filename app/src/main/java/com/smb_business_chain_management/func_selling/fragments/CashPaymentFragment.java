package com.smb_business_chain_management.func_selling.fragments;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.TextValidator;
import com.smb_business_chain_management.func_selling.OrderListenerInterface;
import com.smb_business_chain_management.func_selling.PaymentDialog;
import com.smb_business_chain_management.func_selling.SellingActivity;

import java.math.BigInteger;

public class CashPaymentFragment extends Fragment {
    private static final String TAG = CashPaymentFragment.class.getSimpleName();
    TextView totalAmountTextView;
    EditText receivedAmountTextInput;
    TextView changeAmountTextView;
    ImageView imageView;
    Button doneButton;
    Button cancelButton;

    AppUtils appUtils = new AppUtils();

    private Context mContext;
    BigInteger orderTotal;

    private OrderListenerInterface listener;
    private Button.OnClickListener onDoneButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PaymentDialog parentFragment = (PaymentDialog) getParentFragment();
            listener.completeOrderAndSubmit(receivedAmountTextInput.getText().toString(), calculateChange().toString(), parentFragment);
            imageView.setImageResource(R.drawable.ic_payment_done);
            imageView.setColorFilter(getResources().getColor(R.color.colorStoreActive, mContext.getTheme()));
//            animateDrawable(imageView);
//            new android.os.Handler().postDelayed(
//                    () -> {
//                        try {
//                            ((PaymentDialog) getParentFragment()).dismiss();
//                            listener.showDoneDialog(receivedAmountTextInput.getText().toString(), calculateChange().toString());
//                            listener.clearOrder();
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getContext(), "Fragment Null", Toast.LENGTH_LONG).show();
//                        }
//                    },
//                    600);
        }
    };
    private Button.OnClickListener onCancelButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((PaymentDialog) getParentFragment()).dismiss();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OrderListenerInterface) context;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cash_payment_fragment, container, false);
        mContext = inflater.getContext();
        viewLookup(view);
        setupViews();
        return view;
    }

    private  void viewLookup(View view){
        totalAmountTextView = view.findViewById(R.id.total);
        receivedAmountTextInput = view.findViewById(R.id.receivedInput);
        changeAmountTextView = view.findViewById(R.id.change);
        imageView = view.findViewById(R.id.imageCash);
        doneButton = view.findViewById(R.id.done);
        cancelButton = view.findViewById(R.id.cancel);
    }
    private void setupViews(){
        receivedAmountTextInput.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        receivedAmountTextInput.setSelection(receivedAmountTextInput.getText().length());
        changeAmountTextView.setText(R.string.order_payment_cash_received_invalid);
        orderTotal = ((SellingActivity) mContext).calculateOrderTotalPriceNumber();
        totalAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_total, AppUtils.formattedBigIntegerMoneyString(orderTotal))));
        changeAmountTextView.setText(R.string.order_payment_cash_received_invalid);
        doneButton.setBackground(getResources().getDrawable(R.drawable.inactive_outlined_button, mContext.getTheme()));
        doneButton.setTextColor(getResources().getColor(R.color.VectorColorActiveUnfocused, mContext.getTheme()));
        doneButton.setCompoundDrawableTintList(getResources().getColorStateList(R.color.VectorColorActiveUnfocused, mContext.getTheme()));
        doneButton.setEnabled(false);
        TextValidator receivedTextWatcher = new TextValidator(receivedAmountTextInput) {
            @Override
            public void validate(TextView textView, String text) {
                BigInteger change = calculateChange();
                if ((change.compareTo(BigInteger.ZERO) < 0)) {
                    changeAmountTextView.setText(R.string.order_payment_cash_received_invalid);
                    doneButton.setBackground(getResources().getDrawable(R.drawable.inactive_outlined_button, mContext.getTheme()));
                    doneButton.setTextColor(getResources().getColor(R.color.VectorColorActiveUnfocused, mContext.getTheme()));
                    doneButton.setCompoundDrawableTintList(getResources().getColorStateList(R.color.VectorColorActiveUnfocused, mContext.getTheme()));
                    doneButton.setEnabled(false);
                }
                else {
                    changeAmountTextView.setText(AppUtils.formatStringToHTMLSpanned(getString(R.string.order_payment_cash_change, AppUtils.formattedBigIntegerMoneyString(calculateChange()))));
                    doneButton.setBackground(getResources().getDrawable(R.drawable.ripple_outlined_button, mContext.getTheme()));
                    doneButton.setTextColor(getResources().getColor(R.color.colorPrimary, mContext.getTheme()));
                    doneButton.setCompoundDrawableTintList(getResources().getColorStateList(R.color.colorPrimary, mContext.getTheme()));
                    doneButton.setEnabled(true);
                }
            }
        };
        receivedAmountTextInput.addTextChangedListener(receivedTextWatcher);
        doneButton.setOnClickListener(onDoneButtonClicked);
        cancelButton.setOnClickListener(onCancelButtonClicked);
    }
    private BigInteger calculateChange() {
        String receivedString = receivedAmountTextInput.getText().toString();
        BigInteger received;

        if (receivedString.equals("")) received = BigInteger.ZERO;
        else received = new BigInteger(receivedAmountTextInput.getText().toString());

        return received.subtract(orderTotal);
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
