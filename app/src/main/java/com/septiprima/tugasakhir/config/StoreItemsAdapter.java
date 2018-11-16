package com.septiprima.tugasakhir.config;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.constructor.StoreItems;

import java.util.List;

/**
 * Created by yosep on 2/4/2018.
 */

public class StoreItemsAdapter extends RecyclerView.Adapter<StoreItemsAdapter.MyViewHolder> {

    private Context context;
    private List<StoreItems> items;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout store_item;
        TextView item_name, price;
        ImageView itemimg;

        private MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            item_name = (TextView) view.findViewById(R.id.item_name);
            store_item = view.findViewById(R.id.store_item);
            price = view.findViewById(R.id.item_price);
            itemimg = view.findViewById(R.id.itemimg);
        }
    }

    @Override
    public StoreItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item, parent, false);

        return new StoreItemsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StoreItemsAdapter.MyViewHolder holder, int position) {
        StoreItems item = items.get(position);
        holder.item_name.setText(item.getName());
        holder.price.setText(item.getPrice());

        StorageReference gsReference = storage.getReferenceFromUrl("gs://appproject-5c4b3.appspot.com/images/"+item.getImage());
        Log.e("test image", "gs://appproject-5c4b3.appspot.com/images/"+item.getImage());
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .thumbnail(0.5f)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.itemimg);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public StoreItemsAdapter(List<StoreItems> items){
        this.items = items;
    }

}
