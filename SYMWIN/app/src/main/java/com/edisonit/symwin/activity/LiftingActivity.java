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
import com.edisonit.symwin.adapter.LiftingAdapter;
import com.edisonit.symwin.adapter.ModelAndQtyAdapter;
import com.edisonit.symwin.adapter.ProductDetailsAdapter;
import com.edisonit.symwin.model.Lifting;
import com.edisonit.symwin.model.ModelAndQty;
import com.edisonit.symwin.model.ProductDetails;
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
import android.widget.EditText;
import android.widget.Spinner;

import com.edisonit.symwin.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LiftingActivity extends AppCompatActivity {


    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Lifting> liftingList;
    private List<Lifting> liftingDtls;
    private RecyclerView.Adapter adapter;
    RecyclerView mList;
    private String url;
    String loginName, modelName, day;
    BottomNavigationView bottomNavigationView;
    private ProgressDialog progressDialog;

    private Spinner daySpinner;
    //    private Button searchButton;
    private EditText searchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifting);
        setTitle(R.string.lifting);
        Toolbar toolbar = findViewById(R.id.lifting_toolbar);
        setSupportActionBar(toolbar);
        day ="90";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchModel = (EditText) findViewById(R.id.lifting_search_model);
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

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.lifting_nav_view_bottom);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.navigation_home){

                    Intent intent = new Intent(LiftingActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }else if(menuItem.getItemId()==R.id.navigation_dashboard){

                }else{

                }

                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.lifting_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiftingActivity.this, FeedbackActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginName = preferences.getString("userName","");
        modelName = preferences.getString("modelName","");

        daySpinner = (Spinner) findViewById(R.id.lifting_spinner_day);


        mList = findViewById(R.id.lifting_product_details);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(String.valueOf(daySpinner.getSelectedItem()).equals("Select Day Range")){
                    day = "90";
                }else{
                    day=String.valueOf(daySpinner.getSelectedItem());
                }
                url  = "http://api.edison-bd.com/api/SalesAPP/API5_getLiftingDataByRetailer_EDISON_IT?uname="+loginName+"&day="+day;
                progressDialog.dismiss();
                getData();
                setRecyclerLine();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        url  = "http://api.edison-bd.com/api/SalesAPP/API5_getLiftingDataByRetailer_EDISON_IT?uname="+loginName+"&day="+day;


        getData();
        setRecycler();

        // row click listener
        mList.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Lifting lift = liftingDtls.get(position);
                String modelName = lift.getShortName();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( LiftingActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("modelName",modelName);
                editor.putString("day",day);
                editor.apply();

                Intent intent = new Intent(LiftingActivity.this, LiftingDetailsActivity.class);
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

                        Lifting lifting = new Lifting();
                        lifting.setShortName(jsonObject.getString("ShortName"));
                        lifting.setLiftingQty(jsonObject.getString("LiftingQty"));

                        progressDialog.dismiss();

                        liftingList.add(lifting);
                        liftingDtls.add(lifting);
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
        liftingList = new ArrayList<>();
        liftingDtls = new ArrayList<>();
        adapter = new LiftingAdapter(getApplicationContext(),liftingDtls);

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



    public void setRecyclerLine(){
        liftingList = new ArrayList<>();
        liftingDtls = new ArrayList<>();
        adapter = new LiftingAdapter(getApplicationContext(),liftingDtls);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        //mList.addItemDecoration(dividerItemDecoration);


//        // adding inbuilt divider line
//        mList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        mList.setItemAnimator(new DefaultItemAnimator());

        mList.setAdapter(adapter);


    }


    private void filter(String text){
        ArrayList<Lifting> filteredList = new ArrayList<>();
        liftingDtls = new ArrayList<>();
        for (Lifting item : liftingList) {
            if (item.getShortName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
                Lifting lft = new Lifting();
                lft.setShortName(item.getShortName());
                lft.setLiftingQty(item.getLiftingQty());
                liftingDtls.add(lft);
            }
        }


        adapter = new LiftingAdapter(getApplicationContext(),filteredList);
        mList.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }

}
