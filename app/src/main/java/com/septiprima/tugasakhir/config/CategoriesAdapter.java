package com.septiprima.tugasakhir.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.activity.MainActivity;
import com.septiprima.tugasakhir.activity.StoreActivity;
import com.septiprima.tugasakhir.constructor.Categories;
import com.septiprima.tugasakhir.constructor.Store;

import java.util.List;

/**
 * Created by yosep on 1/28/2018.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    private Context context;
    private List<Categories> categories;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout list_menu;
        TextView category_name;
        ImageView image;

        private MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            list_menu = (LinearLayout) view.findViewById(R.id.list_menu);
            category_name = (TextView) view.findViewById(R.id.category_name);
            image = (ImageView) view.findViewById(R.id.image_bg);
            image.setImageAlpha(60);
        }
    }

    public CategoriesAdapter(List<Categories> categories){
        this.categories = categories;
    }

    @Override
    public CategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_menu, parent, false);

        return new CategoriesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.MyViewHolder holder, int position) {
        Categories cat = categories.get(position);
        holder.category_name.setText(cat.getName());

        StorageReference gsReference = storage.getReferenceFromUrl("gs://appproject-5c4b3.appspot.com/images/"+cat.getImages());
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(gsReference)
                .thumbnail(0.5f)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

        //Log.e("TAG", "onBindViewHolder: "+ gsReference);
        applyClickEvents(holder,position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.list_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Categories cat = categories.get(position);
                //======== Menuju ke form reason ========
                Intent intent = new Intent(context, StoreActivity.class);
                intent.putExtra("category",cat.getName());
                intent.putExtra("iduser",MainActivity.iduser);
                view.getContext().startActivity(intent);
            }
        });
    }

}
