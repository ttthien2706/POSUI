package com.smb_business_chain_management.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.smb_business_chain_management.BusinessChainRESTClient;
import com.smb_business_chain_management.BusinessChainRESTService;
import com.smb_business_chain_management.models.City;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();
    static String PADDING_START = "\u0020\u0020\u0020\u0020";
    static int COLUMN1_WIDTH = 4;
    static int COLUMN2_WIDTH = 18;
    static int COLUMN3_WIDTH = 4;
    static int COLUMN4_WIDTH = 18;

    public void fetchAllAdministrativeUnits(List<City> allCities, ArrayAdapter<City> cityAdapter){
        BusinessChainRESTService businessChainRESTService = BusinessChainRESTClient.getClient().create(BusinessChainRESTService.class);
        Call<List<City>> call = businessChainRESTService.getCities();

        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.code() == 200) {
                    List<City> responseList = response.body();
                    allCities.clear();
                    allCities.addAll(responseList);
                    cityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                throwable.printStackTrace();
            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String sAddress){
        Geocoder coder = new Geocoder(context);
        List<Address> addresses;
        LatLng returnLatLng = null;
        if (sAddress.length() < 1){
            return null;
        }
        else{
            try{
                addresses = coder.getFromLocationName(sAddress, 5);
                if (addresses == null || addresses.size() == 0){
                    return null;
                }

                Address location = addresses.get(0);
                returnLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            }
            catch (IOException io){
                io.printStackTrace();
            }
        }
        return returnLatLng;
    }

    public static Spanned formatStringToHTMLSpanned(String stringResource){
        return Html.fromHtml(stringResource, Html.FROM_HTML_MODE_LEGACY);
    }

    public static String formattedBigIntegerMoneyString(BigInteger bigInteger){
        return NumberFormat.getNumberInstance(Locale.GERMANY).format(bigInteger);
    }
    public static String formattedStringMoneyString(String string){
        return NumberFormat.getNumberInstance(Locale.GERMANY).format(Integer.parseInt(string));
    }
    public static String getCurrentFormattedDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * string to byte[]
     * */
    public byte[] strTobytes(String str){
        byte[] b=null,data=null;
        try {
            b = str.getBytes("utf-8");
            data=new String(b,"utf-8").getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
    /**
     * byte[] merger
     * */
    public byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
    public byte[] strTobytes(String str ,String charset){
        byte[] b=null,data=null;
        try {
            b = str.getBytes("utf-8");
            data=new String(b,"utf-8").getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
    public static String cutAndAddNewLine(String string, int columnWidth) {
        string = string.trim();
        if (string.length() < 1) {
            return PADDING_START;
        }
        else if (string.length() < columnWidth){
            return PADDING_START.concat(string).concat(fillWidth(columnWidth - string.length()-PADDING_START.length()));
        }
        String cutString = "";
        cutString = cutString.concat(PADDING_START)
                .concat(string.substring(0, columnWidth - 1))
                .concat("\n");
        cutString = cutString.concat(cutAndAddNewLine(string.substring(columnWidth - 1), columnWidth));
        return cutString;
    }

    public static String formattedOrderItem(String col1, String col2, String col3, String col4) {
        if (col1.length() < 1 && col2.length() < 1 && col3.length() < 1 && col4.length() < 1) {
            return "";
        }
        String tmpCol1 = col1;
        String tmpCol2 = col2;
        String tmpCol3 = col3;
        String tmpCol4 = col4;
        String cutString = "";
        while (tmpCol1.length() >= 1 || tmpCol2.length() >= 1 || tmpCol3.length() >= 1 || tmpCol4.length() >= 1) {
            cutString = cutString.concat(PADDING_START);
            //col1
            if (tmpCol1.length() < COLUMN1_WIDTH) {
                cutString = cutString.concat(tmpCol1).concat(fillWidth(COLUMN1_WIDTH - tmpCol1.length()));
                tmpCol1 = "";
            } else if(tmpCol1.length() == COLUMN1_WIDTH){
                cutString = cutString.concat(tmpCol1).concat(fillWidth(COLUMN1_WIDTH - tmpCol1.length()));
                tmpCol1 = "";
            } else {
                cutString = cutString.concat(tmpCol1.substring(0, COLUMN1_WIDTH - 1)).concat("\u0020");
                tmpCol1 = tmpCol1.substring(COLUMN1_WIDTH - 2).trim();
            }
            //col2
            if (tmpCol2.length() < COLUMN2_WIDTH) {
                cutString = cutString.concat(tmpCol2).concat(fillWidth(COLUMN2_WIDTH - tmpCol2.length()));
                tmpCol2 = "";
            } else if(tmpCol2.length() == COLUMN2_WIDTH){
                cutString = cutString.concat(tmpCol2).concat(fillWidth(COLUMN2_WIDTH - tmpCol2.length()));
                tmpCol2 = "";
            } else {
                cutString = cutString.concat(tmpCol2.substring(0, COLUMN2_WIDTH - 1)).concat("\u0020");
                tmpCol2 = tmpCol2.substring(COLUMN2_WIDTH - 2).trim();
            }
            //col3
            if (tmpCol3.length() < COLUMN3_WIDTH) {
                cutString = cutString.concat(tmpCol3).concat(fillWidth(COLUMN3_WIDTH - tmpCol3.length()));
                tmpCol3 = "";
            } else if(tmpCol3.length() == COLUMN3_WIDTH){
                cutString = cutString.concat(tmpCol3).concat(fillWidth(COLUMN3_WIDTH - tmpCol3.length()));
                tmpCol3 = "";
            } else {
                cutString = cutString.concat(tmpCol3.substring(0, COLUMN3_WIDTH - 1)).concat("\u0020");
                tmpCol3 = tmpCol3.substring(COLUMN3_WIDTH - 2).trim();
            }
            //col4
            if (tmpCol4.length() < COLUMN4_WIDTH) {
                cutString = cutString.concat(tmpCol4).concat(fillWidth(COLUMN4_WIDTH - tmpCol4.length())).concat("\n");
                tmpCol4 = "";
            } else if(tmpCol4.length() == COLUMN4_WIDTH){
                cutString = cutString.concat(tmpCol4).concat(fillWidth(COLUMN4_WIDTH - tmpCol4.length()));
                tmpCol4 = "";
            } else {
                cutString = cutString.concat(tmpCol4.substring(0, COLUMN4_WIDTH - 1)).concat("\n");
                tmpCol4 = tmpCol4.substring(COLUMN4_WIDTH - 1).trim();
            }
        }
        return cutString;
    }
    private static String fillWidth(int width){
        char[] chars = new char[width];
        Arrays.fill(chars, '\u0020');
        return new String(chars);
    }
}
