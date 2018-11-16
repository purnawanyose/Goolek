package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.ManageItemAdapter;
import com.septiprima.tugasakhir.config.StoreAdapter;
import com.septiprima.tugasakhir.config.StoreItemsAdapter;
import com.septiprima.tugasakhir.constructor.Store;
import com.septiprima.tugasakhir.constructor.StoreItems;
import com.septiprima.tugasakhir.fragment.Login_Store;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreDetailActivity extends AppCompatActivity {

    SwipeRefreshLayout swipe_items;
    RecyclerView recyler_items;
    private StoreItemsAdapter mAdapter;
    List<StoreItems> itemslist = new ArrayList<StoreItems>();
    TextView store_name,store_loc;

    String id_store, nama, alamat, parkir, idstore, iduser, visitor;
    String id_user, docid;
    int visitorInt = 1;
    FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    MaterialFavoriteButton favorite;

    String TAG = StoreDetailActivity.class.getSimpleName();

    Boolean fav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        db = FirebaseFirestore.getInstance();

        swipe_items = findViewById(R.id.swipe_items);
        recyler_items = findViewById(R.id.recycler_view_items);
        store_name = findViewById(R.id.store_name);
        store_loc = findViewById(R.id.store_loc);

        Bundle extras = getIntent().getExtras();
        id_store = extras.getString("id_store");
        nama = extras.getString("nama");
        alamat = extras.getString("alamat");
        parkir = extras.getString("parkir");
        visitor =  extras.getString("visitor");
        iduser = MainActivity.iduser;

        visitorInt = Integer.parseInt(visitor);

        updateVisit(id_store);

        store_name.setText(nama);
        store_loc.setText(alamat);

        store_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

        getdata(id_store);
        swipe_items.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata(id_store);
            }
        });

        favorite = (MaterialFavoriteButton) findViewById(R.id.favorite);

        getFavorite(id_store);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                favorite.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (fav){
                            Log.e(TAG," apakaha masuk sini? => " + fav);
                            delFavorite();
                        }
                        else {
                            Log.e(TAG," apakaha masuk sini aja? => " + fav);
                            addFavorite();
                        }
                    }
                });
            }
        }, 100);



    }

    private void getdata(final String idstore){
        itemslist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("store_items");
        colRef
                .whereEqualTo("id",idstore)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                StoreItems item = new StoreItems();
                                item.setName(document.getData().get("nama").toString());
                                item.setPrice(document.getData().get("harga").toString());
                                item.setImage(document.getData().get("image").toString());
                                Log.e(TAG, "onComplete: "+document.getData().get("image").toString() );
                                itemslist.add(item);
                            }
                            Log.e(TAG, "onCreate: "+ idstore + " iduser: "+ iduser );
                            mAdapter = new StoreItemsAdapter(itemslist);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyler_items.setLayoutManager(mLayoutManager);
                            recyler_items.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            swipe_items.setRefreshing(false);

                            return;
                        }else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error getting documents: ", e);
                        return;
                    }
                });
    }

    private void getmap(){

    }



    private void getFavorite(final String idstore){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("favorited_store");
        colRef
                .whereEqualTo("id_store",idstore)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.getData().get("id_user").toString().equalsIgnoreCase(iduser)){
                                    favorite.setFavorite(true,false);
                                    fav=true;
                                    docid = document.getId();
                                    Log.e(TAG, "onComplete: check favorit: " + fav );
                                }else{
                                    
                                }
                            }


                            return;
                        }else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error getting documents: ", e);
                        return;
                    }
                });

    }

    private void addFavorite(){

        Map<String, Object> user = new HashMap<>();
        user.put("id_store", id_store);
        user.put("id_user", iduser);

        db.collection("favorited_store").document()
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(StoreDetailActivity.this,"Register Complete",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(StoreDetailActivity.this,"Register Fail",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void delFavorite(){

        db.collection("favorited_store").document(docid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        favorite.setFavorite(false,true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void updateVisit(String id){
        DocumentReference contact = db.collection("store_users").document(id);
        contact.update("visitor", visitorInt + 1)
                .addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
                Toast.makeText(StoreDetailActivity.this,"Data not Updated",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
