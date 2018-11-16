package com.septiprima.tugasakhir.config;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.constructor.StoreItems;

import java.util.List;

/**
 * Created by yosep on 2/12/2018.
 */

public class ManageItemAdapter extends RecyclerView.Adapter<ManageItemAdapter.MyViewHolder> {

    private Context context;
    private List<StoreItems> items;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout store_item;
        TextView item_name, edit;

        private MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            item_name = (TextView) view.findViewById(R.id.item_name);
            store_item = view.findViewById(R.id.store_item);
            edit = view.findViewById(R.id.edit);
        }
    }

    @Override
    public ManageItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit, parent, false);

        return new ManageItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ManageItemAdapter.MyViewHolder holder, int position) {
        StoreItems item = items.get(position);
        holder.item_name.setText(item.getName());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ManageItemAdapter(List<StoreItems> items){
        this.items = items;
    }

}
