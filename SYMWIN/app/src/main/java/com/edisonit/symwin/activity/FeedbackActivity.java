package com.edisonit.symwin.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edisonit.symwin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    Spinner spinner;
    EditText contactName, contactNo, feedbackDetails;
    Button submitbtn;

    String category, name, phone, details;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    // Storing server url into String variable.
    String HttpUrl = "http://api.edison-bd.com/api/SalesAPP/API_PostUsrFeedback_EDISON_IT";


    String URL="http://api.edison-bd.com/api/SalesAPP/API_getFeedbackCat_EDISON_IT";

    ArrayList<String> categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.feedback_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.feedback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.feedback_nav_view_bottom);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.navigation_home){

                    Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }else if(menuItem.getItemId()==R.id.navigation_dashboard){

                }else{

                }

                return true;
            }
        });

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(FeedbackActivity.this);

        progressDialog = new ProgressDialog(FeedbackActivity.this);


        categoryName=new ArrayList<>();

        spinner=(Spinner)findViewById(R.id.feedback_spinner);
        contactName = (EditText) findViewById(R.id.user_name);
        contactNo = (EditText) findViewById(R.id.user_phone);
        feedbackDetails = (EditText) findViewById(R.id.feedback_details);
        submitbtn = (Button) findViewById(R.id.button_feedback);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        contactName.setText(preferences.getString("userName",""));

        spinner.setSelection(0, true);



        loadSpinnerData(URL);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String feedback_catetory=   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

//                Toast.makeText(getApplicationContext(),country,Toast.LENGTH_LONG).show();

            }

            @Override

            public void onNothingSelected(AdapterView<?> adapterView) {

                // DO Nothing here

            }

        });


        // Adding click listener to button.
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveData();

            }
        });


    }


    private void saveData(){
        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait, We are Inserting Your Data on Server");
        progressDialog.show();

        category=   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
        name = contactName.getText().toString();
        phone = contactNo.getText().toString();
        details = feedbackDetails.getText().toString();

        if(phone.length()==11){

            // Creating string request with post method.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String ServerResponse) {

                            // Hiding the progress dialog after all task complete.
                            progressDialog.dismiss();

                            contactNo.setText("");
                            feedbackDetails.setText("");

                            // Showing response message coming from server.
                            Toast.makeText(FeedbackActivity.this, "Feedback Successfully Submitted", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            // Hiding the progress dialog after all task complete.
                            progressDialog.dismiss();

                            // Showing error message if something goes wrong.
                            Toast.makeText(FeedbackActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();

                    // Adding All values to Params.
                    params.put("FCategory", category);
                    params.put("ContactNo", phone);
                    params.put("FeedbackDetails", details);
                    params.put("AddedBy", name);

                    return params;
                }

            };

            // Creating RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);

            // Adding the StringRequest object into requestQueue.
            requestQueue.add(stringRequest);


        }else{
            progressDialog.dismiss();
            Toast.makeText(FeedbackActivity.this,"Please Enter Valid Phone Number",Toast.LENGTH_LONG).show();

        }



    }


    private void loadSpinnerData(String url) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        categoryName.add(jsonObject.getString("FCatName"));

                        spinner.setAdapter(new ArrayAdapter<String>(FeedbackActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryName));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }


}
