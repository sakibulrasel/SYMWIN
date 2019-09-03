package com.edisonit.symwin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.edisonit.symwin.R;
import com.edisonit.symwin.utils.SaveSharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button signInBtn;
    EditText userName, password;
    String imei;
    String lat, lan;

    boolean isSuccess;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String loginName = preferences.getString("userName","");


        mQueue = Volley.newRequestQueue(LoginActivity.this);

        //loadIMEI();

        imei = getUniqueIMEIId(this);


        lat = "45.0999";
        lan = "90.09998";





        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        userName.setText(loginName);

        password.requestFocus();




        signInBtn = (Button) findViewById(R.id.button_sign_in);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String uname = userName.getText().toString().trim();
                String pass = password.getText().toString().trim();

                makeLoginRequest(uname, pass, imei,lat,lan);


            }
        });


    }

    private void makeLoginRequest(String username, String pass, String imei1, String lat, String lan) {

        String url="http://api.edison-bd.com/api/SalesAPP/API0_Login_EDISON_IT?uname="+username+"&pass="+pass+"&imei1="+imei1+"&lat="+lat+"&lan="+lan+"";  //live server

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                                JSONObject user = response.getJSONObject(0);

                                String loginStatus = user.getString("ReturnMsg");


                                if(loginStatus.equals("SUCCESS")){
                                    isSuccess = true;
                                    SaveSharedPreference.setLoggedIn(getApplicationContext(), true);

                                    String username = user.getString("UserName");
                                    String fullName = user.getString("FullName");

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("uName",fullName);
                                    editor.putString("userName",username);
                                    editor.apply();

                                    goMainActivity();


                                }else{
                                    isSuccess = false;

                                    Toast.makeText(LoginActivity.this,"User Name or Password Mismatched...",Toast.LENGTH_LONG).show();

                                }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);



    }


    public static String getUniqueIMEIId(Context context) {


        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            String imei = telephonyManager.getDeviceId();

            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    private void goMainActivity(){
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Plase Wait...");
        progressDialog.show();


        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        progressDialog.dismiss();
    }



}
