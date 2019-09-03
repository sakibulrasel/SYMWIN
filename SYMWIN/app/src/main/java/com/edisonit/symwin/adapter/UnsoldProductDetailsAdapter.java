package com.edisonit.symwin.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.edisonit.symwin.R;
import com.edisonit.symwin.model.UnsoldProductDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UnsoldProductDetailsAdapter extends RecyclerView.Adapter<UnsoldProductDetailsAdapter.ViewHolder> {

    private Context context;
    private List<UnsoldProductDetails> list;
    private int lastSelectedPosition = -1;


    RecyclerView mList;
    private String url;
    String imei;

    public UnsoldProductDetailsAdapter(Context context, List<UnsoldProductDetails> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public UnsoldProductDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.unsold_product, parent, false);
        return new UnsoldProductDetailsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UnsoldProductDetailsAdapter.ViewHolder holder, int position) {
        UnsoldProductDetails unsoldDetails = list.get(position);

        holder.modelNames.setText("Model : "+unsoldDetails.getShortName());
        holder.imei1_detail.setText("IMEI1 : "+ String.valueOf(unsoldDetails.getIMEI1()));
        holder.lifting_date.setText("Date : "+ String.valueOf(unsoldDetails.getLiftingDate()));
        imei =String.valueOf(unsoldDetails.getIMEI1());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView modelNames, imei1_detail, lifting_date;
        public Button selectionState;
        LinearLayout pending;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            modelNames = itemView.findViewById(R.id.modelNames);
            imei1_detail = itemView.findViewById(R.id.imei1_detail);
            lifting_date = itemView.findViewById(R.id.lifting_date);
            pending = itemView.findViewById(R.id.pending);

            selectionState = (Button) itemView.findViewById(R.id.btn_declear_sale);

            selectionState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UnsoldProductDetailsAdapter.this.context);
                    String loginName = preferences.getString("userName","");
                    String imei1 = imei;




                    String url = "http://api.edison-bd.com/api/SalesAPP/API0_AppActivationData_PostAppSalesByIMEI1_EDISON_IT?uname="+loginName+"&imei1="+imei1;


                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        Toast.makeText(UnsoldProductDetailsAdapter.this.context,
                                                "Sales Decleared Successfully... " + response.getString("code"),
                                                Toast.LENGTH_LONG).show();
                                        selectionState.setVisibility(View.GONE);
                                        pending.setVisibility(View.VISIBLE);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();

                                }
                            });

// Access the RequestQueue through your singleton class.
                    RequestQueue requestQueue = Volley.newRequestQueue(UnsoldProductDetailsAdapter.this.context);
                    requestQueue.add(jsonObjectRequest);



                }
            });

        }
    }
}
