package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.CategoriesAdapter;
import com.septiprima.tugasakhir.config.StoreAdapter;
import com.septiprima.tugasakhir.constructor.Store;
import com.septiprima.tugasakhir.fragment.Login_Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class StoreActivity extends AppCompatActivity {

    LinearLayout store_id;
    SwipeRefreshLayout swipe_store;
    RecyclerView recyler_store;
    private StoreAdapter mAdapter;
    List<Store> storelist = new ArrayList<Store>();

    String TAG = StoreActivity.class.getSimpleName();
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        recyler_store = findViewById(R.id.recycler_view_store);
        swipe_store = findViewById(R.id.swipe_store);

        Bundle extras = getIntent().getExtras();
        category = extras.getString("category");

        getdata(category);

        swipe_store.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               getdata(category);
            }
        });

    }

    private void getdata(final String category){
        storelist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("store_users");
                colRef
                        .whereEqualTo("category",category)
                        .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                 if (document.getData().get("status").toString().equalsIgnoreCase("1")){
                                     Store store = new Store();
                                     store.setId(document.getId());
                                     store.setAlamat(document.getData().get("alamat").toString());
                                     store.setToko(document.getData().get("toko").toString());
                                     store.setVisitor(document.getData().get("visitor").toString());
                                     storelist.add(store);
                                 }
                            }
                            Log.e(TAG, "onCreate: "+category );
                            mAdapter = new StoreAdapter(storelist);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyler_store.setLayoutManager(mLayoutManager);
                            recyler_store.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            swipe_store.setRefreshing(false);

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

}


