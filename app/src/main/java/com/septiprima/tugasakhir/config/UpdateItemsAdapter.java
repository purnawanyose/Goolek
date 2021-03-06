package com.septiprima.tugasakhir.config;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.activity.ItemsActivity;
import com.septiprima.tugasakhir.activity.Main2Activity;
import com.septiprima.tugasakhir.activity.MainActivity;
import com.septiprima.tugasakhir.activity.StoreDetailActivity;
import com.septiprima.tugasakhir.activity.StoreMenuActivity;
import com.septiprima.tugasakhir.activity.UpdateItemActivity;
import com.septiprima.tugasakhir.constructor.StoreItems;

import java.util.List;

/**
 * Created by yosep on 2/4/2018.
 */

public class UpdateItemsAdapter extends RecyclerView.Adapter<UpdateItemsAdapter.MyViewHolder> {

    private Context context;
    private List<StoreItems> items;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout store_item;
        TextView item_name, edit, delete;
        ImageView itemimg;

        private MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            item_name = (TextView) view.findViewById(R.id.item_name);
            store_item = view.findViewById(R.id.store_item);
            edit = view.findViewById(R.id.edit);
            itemimg = view.findViewById(R.id.itemimg);
            delete = view.findViewById(R.id.delete);
        }
    }

    @Override
    public UpdateItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit, parent, false);

        return new UpdateItemsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UpdateItemsAdapter.MyViewHolder holder, int position) {
        final StoreItems item = items.get(position);
        holder.item_name.setText(item.getName());

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

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateItemActivity.class);
                intent.putExtra("idstore",ItemsActivity.idstore);
                intent.putExtra("id",item.getId());
                intent.putExtra("getname",item.getName());
                intent.putExtra("harga",item.getPrice());
                intent.putExtra("img",item.getImage());
                v.getContext().startActivity(intent);
            }
        });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("store_items");

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                db.collection("store_items").document(item.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("TEST", "DocumentSnapshot successfully deleted!");
                                Intent intent = new Intent(context, StoreMenuActivity.class);
                                v.getContext().startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TEST", "Error deleting document", e);
                            }
                        });

            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public UpdateItemsAdapter(List<StoreItems> items){
        this.items = items;
    }

}
