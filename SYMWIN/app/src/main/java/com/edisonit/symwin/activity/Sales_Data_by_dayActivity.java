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
import com.edisonit.symwin.model.ModelAndQty;
import com.edisonit.symwin.model.UnsoldQuantity;
import com.edisonit.symwin.utils.RecyclerTouchListener;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.edisonit.symwin.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Sales_Data_by_dayActivity extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<ModelAndQty> modelAndQtyList;
    private List<ModelAndQty> modelAndQty;
    private RecyclerView.Adapter adapter;
    private ModelAndQtyAdapter mAdapter;
    private Spinner daySpinner;
    private ProgressDialog progressDialog;
    //    private Button searchButton;
    private EditText searchModel;
    String day;
    String loginName, username;
    BottomNavigationView bottomNavigationView;

    RecyclerView mList;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales__data_by_day);
        setTitle(R.string.sales);
        Toolbar toolbar = findViewById(R.id.sales_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        day ="90";

        searchModel = (EditText) findViewById(R.id.search_model);
        searchModel.addTextChangedListener(new TextWatcher() {
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

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.sales_nav_view_bottom);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.navigation_home){

                    Intent intent = new Intent(Sales_Data_by_dayActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }else if(menuItem.getItemId()==R.id.navigation_dashboard){

                }else{

                }

                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.sales_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sales_Data_by_dayActivity.this, FeedbackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        daySpinner = (Spinner) findViewById(R.id.spinner_day);


//        searchButton = (Button) findViewById(R.id.button_search);

        username = preferences.getString("uName", "");
        loginName = preferences.getString("userName","");
        if(!username.equalsIgnoreCase(""))
        {
            username = username + "  Default";  /* Edit the value here*/
        }

        mList = findViewById(R.id.modelList);
        day = "90";

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(String.valueOf(daySpinner.getSelectedItem()).equals("Select Day Range")){
                    day = "90";
                }else{
                    day=String.valueOf(daySpinner.getSelectedItem());
                }
                url  = "http://api.edison-bd.com/api/SalesAPP/API1_getActDataByRetailer_EDISON_IT?uname="+loginName+"&day="+day;
                progressDialog.dismiss();
                getData();
                setRecyclerLine();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        url  = "http://api.edison-bd.com/api/SalesAPP/API1_getActDataByRetailer_EDISON_IT?uname="+loginName+"&day=90";

        getData();
        setRecycler();

        // row click listener
        mList.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ModelAndQty modelA = modelAndQty.get(position);
                String modelName = modelA.getShortName();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( Sales_Data_by_dayActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("modelName",modelName);
                editor.putString("day",day);
                editor.apply();

                Intent intent = new Intent(Sales_Data_by_dayActivity.this, ProductDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


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

                        ModelAndQty modelAndQ = new ModelAndQty();
                        modelAndQ.setShortName(jsonObject.getString("ShortName"));
                        modelAndQ.setSoldQty(jsonObject.getString("SoldQty"));
                        progressDialog.dismiss();

                        modelAndQtyList.add(modelAndQ);
                        modelAndQty.add(modelAndQ);
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

    private void setRecycler(){
        modelAndQtyList = new ArrayList<>();
        modelAndQty = new ArrayList<>();
        adapter = new ModelAndQtyAdapter(getApplicationContext(),modelAndQty);

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

    private void setRecyclerLine(){
        modelAndQtyList = new ArrayList<>();
        modelAndQty = new ArrayList<>();
        adapter = new ModelAndQtyAdapter(getApplicationContext(),modelAndQty);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        //mList.addItemDecoration(dividerItemDecoration);


        // adding inbuilt divider line
       // mList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //mList.setItemAnimator(new DefaultItemAnimator());

        mList.setAdapter(adapter);


    }

    private void filter(String text){
        ArrayList<ModelAndQty> filteredList = new ArrayList<>();
        modelAndQty = new ArrayList<>();
        for (ModelAndQty item : modelAndQtyList) {
            if (item.getShortName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                ModelAndQty modelAndQ = new ModelAndQty();
                modelAndQ.setShortName(item.getShortName());
                modelAndQ.setSoldQty(item.getSoldQty());
                modelAndQty.add(modelAndQ);
            }
        }


        adapter = new ModelAndQtyAdapter(getApplicationContext(),filteredList);
        mList.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }

}
