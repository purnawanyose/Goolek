package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.StoreAdapter;
import com.septiprima.tugasakhir.constructor.Store;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    LinearLayout store_id;
    SwipeRefreshLayout swipe_store;
    RecyclerView recyler_store;
    private StoreAdapter mAdapter;
    List<Store> storelist = new ArrayList<Store>();

    String TAG = FavoriteActivity.class.getSimpleName();
    String userid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        recyler_store = findViewById(R.id.recycler_view_favorite_store);
        swipe_store = findViewById(R.id.swipe_favorite_store);

        Bundle extras = getIntent().getExtras();
        userid = extras.getString("id_store");

        swipe_store.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata(userid);
            }
        });

        getdata(userid);
    }

    private void getdata(final String userid){
        storelist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("favorited_store");
        colRef
                .whereEqualTo("id_user",userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.e(TAG, document.getId() + " => " + document.getData());
                                String storeid = document.getData().get("id_store").toString();
                                Log.e(TAG, document.getId() + " => " + storeid);
                                storelist.clear();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection("store_users").document(storeid);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                Store store = new Store();
                                                store.setId(document.getId());
                                                store.setAlamat(document.getData().get("alamat").toString());
                                                store.setToko(document.getData().get("toko").toString());
                                                storelist.add(store);
                                                Log.e(TAG, "storelist" + storelist);
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }

                                            mAdapter = new StoreAdapter(storelist);
                                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                            recyler_store.setLayoutManager(mLayoutManager);
                                            recyler_store.setAdapter(mAdapter);
                                            mAdapter.notifyDataSetChanged();
                                            swipe_store.setRefreshing(false);
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        }else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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

}
