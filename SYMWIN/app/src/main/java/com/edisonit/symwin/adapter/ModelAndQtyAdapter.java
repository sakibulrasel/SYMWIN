package com.edisonit.symwin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.edisonit.symwin.R;
import com.edisonit.symwin.model.ModelAndQty;

import java.util.ArrayList;
import java.util.List;

public class ModelAndQtyAdapter extends RecyclerView.Adapter<ModelAndQtyAdapter.ViewHolder> {
    private Context context;
    private List<ModelAndQty> list;

    public ModelAndQtyAdapter(Context context, List<ModelAndQty> list) {
        this.context = context;
        this.list = list;
    }

    public ModelAndQtyAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.detail_sale, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModelAndQty modelAndQty = list.get(position);

        holder.modelNo.setText(modelAndQty.getShortName());
        holder.soldQty.setText(String.valueOf(modelAndQty.getSoldQty()));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView modelNo, soldQty;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });

            modelNo = itemView.findViewById(R.id.model_no);
            soldQty = itemView.findViewById(R.id.quantity);

        }
    }

    public void filterList(ArrayList<ModelAndQty> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}
