package com.edisonit.symwin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.edisonit.symwin.R;
import com.edisonit.symwin.model.UnsoldQuantity;

import java.util.List;

public class UnsoldProductAdapter extends RecyclerView.Adapter<UnsoldProductAdapter.ViewHolder> {



    private Context context;
    private List<UnsoldQuantity> list;


    public UnsoldProductAdapter(Context context, List<UnsoldQuantity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public UnsoldProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.unsold_product_details, parent, false);
        return new UnsoldProductAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UnsoldProductAdapter.ViewHolder holder, int position) {
        UnsoldQuantity unsoldQuantity = list.get(position);

        holder.model_name.setText(unsoldQuantity.getShortName());
        holder.soldQuantity.setText(String.valueOf(unsoldQuantity.getSoldQty()));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView model_name, soldQuantity;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            model_name = itemView.findViewById(R.id.model_name);
            soldQuantity = itemView.findViewById(R.id.soldQuantity);


        }
    }
}
