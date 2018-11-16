package com.septiprima.tugasakhir.config;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.activity.StoreActivity;
import com.septiprima.tugasakhir.activity.StoreDetailActivity;
import com.septiprima.tugasakhir.constructor.Categories;
import com.septiprima.tugasakhir.constructor.Store;

import java.util.List;

/**
 * Created by yosep on 1/29/2018.
 */

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {

    private Context context;
    private List<Store> stores;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout list_menu;
        TextView store_name, store_loc;

        private MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            store_name = (TextView) view.findViewById(R.id.store_name);
            store_loc = (TextView) view.findViewById(R.id.store_loc);
            list_menu = view.findViewById(R.id.store_menu);
        }
    }

    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_list, parent, false);

        return new StoreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoreAdapter.MyViewHolder holder, int position) {
        Store store = stores.get(position);
        Log.e("TEST", "onBindViewHolder: "+store.getToko());
        holder.store_name.setText(store.getToko());
        holder.store_loc.setText(store.getAlamat());

        applyClickEvents(holder,position);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public StoreAdapter(List<Store> stores){
        this.stores = stores;
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.list_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Store store = stores.get(position);
                //======== Menuju ke form reason ========
                Intent intent = new Intent(context, StoreDetailActivity.class);
                intent.putExtra("nama",store.getToko());
                intent.putExtra("alamat",store.getAlamat());
                intent.putExtra("parkir",store.getParkir());
                intent.putExtra("id_store",store.getId());
                intent.putExtra("visitor",store.getVisitor());
                view.getContext().startActivity(intent);
            }
        });
    }

}
