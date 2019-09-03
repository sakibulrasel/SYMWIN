package com.edisonit.symwin.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.edisonit.symwin.adapter.LiftingDetailsAdapter;
import com.edisonit.symwin.adapter.ProductDetailsAdapter;
import com.edisonit.symwin.model.LiftingDetails;
import com.edisonit.symwin.model.ProductDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.edisonit.symwin.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LiftingDetailsActivity extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<LiftingDetails> liftingDetailsList;
    private List<LiftingDetails> liftingDetails;
    private RecyclerView.Adapter adapter;
    RecyclerView mList;
    private String url;
    String loginName, modelName, day;
    EditText imeiSearch;
    BottomNavigationView bottomNavigationView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.lifting);
        setContentView(R.layout.activity_lifting_details);
        Toolbar toolbar = findViewById(R.id.lifting_details_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imeiSearch = (EditText) findViewById(R.id.lifting_search_imei);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.lifting_details_nav_bottom);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.navigation_home){

                    Intent intent = new Intent(LiftingDetailsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }else if(menuItem.getItemId()==R.id.navigation_dashboard){

                }else{

                }

                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.lifting_details_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiftingDetailsActivity.this, FeedbackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginName = preferences.getString("userName","");
        modelName = preferences.getString("modelName","");
        day = preferences.getString("day","");

        mList = findViewById(R.id.lifting_details_products);


        url  = "http://api.edison-bd.com/api/SalesAPP/API6_getLiftingDataDetailsByRetailer_EDISON_IT?uname="+loginName+"&day="+day+"&pid="+modelName;

        getData();
        setRecycler();

        imeiSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


        // hide bottom navigation bar when keyboard open

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {

                        if(isOpen){

                            bottomNavigationView.setVisibility(View.INVISIBLE);

                        }else{

                            bottomNavigationView.setVisibility(View.VISIBLE);

                        }
                    }
                });
    }


    public void getData(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        LiftingDetails liftingDetail = new LiftingDetails();
                        liftingDetail.setShortName(jsonObject.getString("ShortName"));
                        liftingDetail.setIMEI1(jsonObject.getString("IMEI1"));
                        liftingDetail.setLiftingDate(jsonObject.getString("LiftingDate"));


                        liftingDetailsList.add(liftingDetail);
                        liftingDetails.add(liftingDetail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                adapter.notifyDataSetChanged();
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

    public void setRecycler(){
        liftingDetailsList = new ArrayList<>();
        liftingDetails = new ArrayList<>();
        adapter = new LiftingDetailsAdapter(getApplicationContext(),liftingDetails);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);


        // adding inbuilt divider line
        mList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mList.setItemAnimator(new DefaultItemAnimator());

        mList.setAdapter(adapter);


    }

    private void filter(String text){
        ArrayList<LiftingDetails> filteredList = new ArrayList<>();
        liftingDetails = new ArrayList<>();
        for (LiftingDetails item : liftingDetailsList) {
            if (item.getIMEI1().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                LiftingDetails ldtls = new LiftingDetails();
                ldtls.setShortName(item.getShortName());
                ldtls.setIMEI1(item.getIMEI1());
                ldtls.setLiftingDate(item.getLiftingDate());
                liftingDetails.add(ldtls);
            }
        }

        adapter = new LiftingDetailsAdapter(getApplicationContext(),filteredList);
        mList.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }

}
