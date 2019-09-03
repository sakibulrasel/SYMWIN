package com.edisonit.symwin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.edisonit.symwin.R;
import com.edisonit.symwin.model.LiftingDetails;

import java.util.List;

public class LiftingDetailsAdapter extends RecyclerView.Adapter<LiftingDetailsAdapter.ViewHolder> {

    private Context context;
    private List<LiftingDetails> list;

    public LiftingDetailsAdapter(Context context, List<LiftingDetails> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public LiftingDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lifting_details_list, parent, false);
        return new LiftingDetailsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LiftingDetailsAdapter.ViewHolder holder, int position) {
        LiftingDetails liftingDetails= list.get(position);

        holder.lifting_model.setText("Model : "+liftingDetails.getShortName());
        holder.lifting_imei1.setText("IMEI1 : "+String.valueOf(liftingDetails.getIMEI1()));
        holder.lifting_deatil_date.setText("Date : "+ String.valueOf(liftingDetails.getLiftingDate()));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lifting_model, lifting_imei1, lifting_deatil_date;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            lifting_model = itemView.findViewById(R.id.l_details_model_names);
            lifting_imei1 = itemView.findViewById(R.id.lifting_imei1);
            lifting_deatil_date = itemView.findViewById(R.id.lifting_details_date);

        }
    }
}
