package com.edisonit.symwin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edisonit.symwin.R;
import com.edisonit.symwin.model.Lifting;
import com.edisonit.symwin.model.ProductDetails;

import java.util.List;

public class LiftingAdapter extends RecyclerView.Adapter<LiftingAdapter.ViewHolder> {


    private Context context;
    private List<Lifting> list;

    public LiftingAdapter(Context context, List<Lifting> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public LiftingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lifting_list, parent, false);
        return new LiftingAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LiftingAdapter.ViewHolder holder, int position) {
        Lifting liftingDtls = list.get(position);

        holder.model_nam.setText(liftingDtls.getShortName());
        holder.lifting_qty.setText(String.valueOf(liftingDtls.getLiftingQty()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView model_nam, lifting_qty;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            model_nam = itemView.findViewById(R.id.lifting_model_no);
            lifting_qty = itemView.findViewById(R.id.lifting_quantity);

        }
    }
}
