package com.smb_business_chain_management.func_login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smb_business_chain_management.LoginRESTClient;
import com.smb_business_chain_management.LoginRESTService;
import com.smb_business_chain_management.R;
import com.smb_business_chain_management.Utils.AppUtils;
import com.smb_business_chain_management.Utils.LoginInfo;
import com.smb_business_chain_management.func_main.MainActivity;
import com.smb_business_chain_management.models.LoginToken;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    TextInputEditText usernameInput;
    TextInputEditText passwordInput;
    Button loginButton;
    Button.OnClickListener loginButtonOnClickListener = (view -> {
        if (!usernameInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty()) {
            ((TextInputLayout) findViewById(R.id.username)).setError(null);
            ((TextInputLayout) findViewById(R.id.password)).setError(null);
            userLogin(usernameInput.getText().toString(), passwordInput.getText().toString());
        } else {
            if (usernameInput.getText().toString().isEmpty()) {
                ((TextInputLayout) findViewById(R.id.username)).setError("Tên đăng nhập trống");
            } else if(((TextInputLayout) findViewById(R.id.username)).getError() != null){
                ((TextInputLayout) findViewById(R.id.username)).setError(null);
            }
            if (passwordInput.getText().toString().isEmpty()){
                ((TextInputLayout) findViewById(R.id.password)).setError("Mật khẩu trống");
            } else if(((TextInputLayout) findViewById(R.id.password)).getError() != null){
                ((TextInputLayout) findViewById(R.id.password)).setError(null);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if UserResponse is Already Logged In
//        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//        }

        setStatusBarGradient(this);
        setContentView(R.layout.activity_login);
        viewLookup();
        loginButton.setOnClickListener(loginButtonOnClickListener);
    }

    private void viewLookup(){
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradient(Activity activity) {
        Window window = activity.getWindow();
        Drawable background = activity.getResources().getDrawable(R.drawable.login_background_gradient, activity.getTheme());
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent, activity.getTheme()));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent, activity.getTheme()));
        window.setBackgroundDrawable(background);
    }
    private void userLogin(String username, String password){
        LoginRESTService loginRESTService;
        loginRESTService = LoginRESTClient.getClient(getApplicationContext()).create(LoginRESTService.class);
        HashMap<String, String> params = mapParams(username, password);
        Call<LoginToken> call = loginRESTService.login(params);
        call.enqueue(new Callback<LoginToken>() {
            @Override
            public void onResponse(Call<LoginToken> call, Response<LoginToken> response) {
                if (response.isSuccessful()){
                    String token = response.body().getAccessToken();
                    String base64EncodedBody = token.split("\\.")[1];
                    String decodedBody = new String(Base64.decode(base64EncodedBody, Base64.DEFAULT));

                    Gson gson = new Gson();
                    LoginInfo loginInfo = gson.fromJson(decodedBody, LoginInfo.class);

                    SaveSharedPreference.setName(getApplicationContext(), loginInfo.getName());
                    SaveSharedPreference.setExpireTime(getApplicationContext(), loginInfo.getExpireTime());
                    SaveSharedPreference.setLoggedIn(getApplicationContext(), true);
                    SaveSharedPreference.setTokenString(getApplicationContext(), response.body().getAccessToken());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Log.e(TAG, response.code() + ": " + response.message());
                    Toast.makeText(getApplicationContext(), "Tên đăng nhập hoặc mật khẩu không chính xác.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginToken> call, Throwable t) {
                Log.e(TAG, "=======onFailure: " + t.toString());
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Không thể kết nối tới server.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private HashMap<String, String> mapParams(String username, String password){
        HashMap<String, String> params = new HashMap<>(0);
        params.put("username", username);
        params.put("grant_type", "password");
        params.put("password", password);
        params.put("client_id", "ro.client");
        params.put("client_secret", "secret");
        params.put("scope", "api1");
        return params;
    }
}
