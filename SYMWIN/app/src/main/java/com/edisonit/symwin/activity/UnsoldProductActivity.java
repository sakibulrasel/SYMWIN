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
import com.edisonit.symwin.adapter.ProductDetailsAdapter;
import com.edisonit.symwin.adapter.UnsoldProductDetailsAdapter;
import com.edisonit.symwin.model.ProductDetails;
import com.edisonit.symwin.model.UnsoldProductDetails;
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

public class UnsoldProductActivity extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<UnsoldProductDetails> unsoldProductDetailsList;
    private List<UnsoldProductDetails> unsoldProductDetails;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;

    RecyclerView mList;
    private String url;
    BottomNavigationView bottomNavigationView;

    String loginName, modelName;
    EditText imeiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsold_product);
        setTitle(R.string.unsold);
        Toolbar toolbar = findViewById(R.id.unsold_details_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imeiSearch = (EditText) findViewById(R.id.search_by_imei);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.unsold_details_nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.navigation_home){

                    Intent intent = new Intent(UnsoldProductActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }else if(menuItem.getItemId()==R.id.navigation_dashboard){

                }else{

                }

                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.unsold_details_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnsoldProductActivity.this, FeedbackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginName = preferences.getString("userName","");
        modelName = preferences.getString("modelName","");



        mList = findViewById(R.id.unsold_product_details);

        //url  = "http://api.edison-bd.com/api/SalesAPP/API3_getActDataDetailsByRetailer_EDISON_IT?uname="+loginName+"&day="+day+"&pid="+modelName;
        url  = "http://api.edison-bd.com/api/SalesAPP/API4_getUnsoldDatadetailsByRetailer_EDISON_IT?uname="+loginName+"&pid="+modelName;

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

                        UnsoldProductDetails unsoldProductDetail = new UnsoldProductDetails();
                        unsoldProductDetail.setShortName(jsonObject.getString("ShortName"));
                        unsoldProductDetail.setIMEI1(jsonObject.getString("IMEI1"));
                        unsoldProductDetail.setLiftingDate(jsonObject.getString("LiftingDate"));


                        unsoldProductDetailsList.add(unsoldProductDetail);
                        unsoldProductDetails.add(unsoldProductDetail);
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
        unsoldProductDetailsList = new ArrayList<>();
        unsoldProductDetails = new ArrayList<>();
        adapter = new UnsoldProductDetailsAdapter(getApplicationContext(),unsoldProductDetails);

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
        ArrayList<UnsoldProductDetails> filteredList = new ArrayList<>();
        unsoldProductDetails = new ArrayList<>();
        for (UnsoldProductDetails item : unsoldProductDetailsList) {
            if (item.getIMEI1().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                UnsoldProductDetails pdtls = new UnsoldProductDetails();
                pdtls.setShortName(item.getShortName());
                pdtls.setIMEI1(item.getIMEI1());
                pdtls.setLiftingDate(item.getLiftingDate());
                unsoldProductDetails.add(pdtls);
            }
        }


        adapter = new UnsoldProductDetailsAdapter(getApplicationContext(),filteredList);
        mList.setAdapter(adapter);


    }


    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }



}
