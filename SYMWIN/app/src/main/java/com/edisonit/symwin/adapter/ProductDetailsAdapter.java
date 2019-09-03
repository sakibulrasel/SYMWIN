package com.edisonit.symwin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.edisonit.symwin.R;
import com.edisonit.symwin.model.ProductDetails;

import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.ViewHolder> {
    private Context context;
    private List<ProductDetails> list;

    public ProductDetailsAdapter(Context context, List<ProductDetails> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ProductDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_details, parent, false);
        return new ProductDetailsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductDetailsAdapter.ViewHolder holder, int position) {
        ProductDetails productDetails = list.get(position);

        holder.model_names.setText("Model : "+productDetails.getShortName());
        holder.imei1.setText("IMEI1 : "+String.valueOf(productDetails.getIMEI1()));
        holder.sales_date.setText("Date : "+ String.valueOf(productDetails.getSalesDate()));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView model_names, imei1, sales_date;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            model_names = itemView.findViewById(R.id.model_names);
            imei1 = itemView.findViewById(R.id.imei1);
            sales_date = itemView.findViewById(R.id.sales_date);

        }
    }
}