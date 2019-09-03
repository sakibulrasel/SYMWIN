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
import com.edisonit.symwin.adapter.ModelAndQtyAdapter;
import com.edisonit.symwin.adapter.UnsoldProductAdapter;
import com.edisonit.symwin.adapter.UnsoldProductDetailsAdapter;
import com.edisonit.symwin.model.ModelAndQty;
import com.edisonit.symwin.model.UnsoldProductDetails;
import com.edisonit.symwin.model.UnsoldQuantity;
import com.edisonit.symwin.utils.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
import android.view.View;
import android.widget.EditText;

import com.edisonit.symwin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnsoldDetailsActivity extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<UnsoldQuantity> unsoldQuantities;
    private List<UnsoldQuantity> unsoldQuantitie;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;

    RecyclerView mList;
    private String url;

    EditText search_unsold_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.unsold);
        setContentView(R.layout.activity_unsold_details);
        Toolbar toolbar = findViewById(R.id.unsold_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        search_unsold_model = (EditText) findViewById(R.id.search_unsold_model);

        search_unsold_model.addTextChangedListener(new TextWatcher() {
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

        FloatingActionButton fab = findViewById(R.id.unsold_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnsoldDetailsActivity.this, FeedbackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("uName", "");
        String loginName = preferences.getString("userName", "");

        mList = findViewById(R.id.unsold_product_detailss);

        url = "http://api.edison-bd.com/api/SalesAPP/API2_getUnsoldDataByRetailer_EDISON_IT?uname=" + loginName;

        getData();
        setRecycler();

        // row click listener
        onDataClick();


    }


    private void onDataClick(){

        mList.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                UnsoldQuantity unsoldQuantity = unsoldQuantitie.get(position);
                String modelName = unsoldQuantity.getShortName();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UnsoldDetailsActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("modelName", modelName);
                editor.apply();

                Intent intent = new Intent(UnsoldDetailsActivity.this, UnsoldProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    private void getData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        UnsoldQuantity unsoldquantity = new UnsoldQuantity();
                        unsoldquantity.setShortName(jsonObject.getString("ShortName"));
                        unsoldquantity.setSoldQty(jsonObject.getString("SoldQty"));


                        unsoldQuantities.add(unsoldquantity);
                        unsoldQuantitie.add(unsoldquantity);
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

    public void setRecycler() {
        unsoldQuantities = new ArrayList<>();
        unsoldQuantitie = new ArrayList<>();
        adapter = new UnsoldProductAdapter(getApplicationContext(), unsoldQuantitie);

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
        ArrayList<UnsoldQuantity> filteredList = new ArrayList<>();
        unsoldQuantitie  = new ArrayList<>();
        for (UnsoldQuantity item : unsoldQuantities) {
            if (item.getShortName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                UnsoldQuantity unsoldquantity = new UnsoldQuantity();
                unsoldquantity.setShortName(item.getShortName());
                unsoldquantity.setSoldQty(item.getSoldQty());
                unsoldQuantitie.add(unsoldquantity);
            }
        }

        adapter = new UnsoldProductAdapter(getApplicationContext(),filteredList);
        mList.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }
}