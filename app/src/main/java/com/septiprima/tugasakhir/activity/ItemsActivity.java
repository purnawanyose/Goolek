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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.ManageItemAdapter;
import com.septiprima.tugasakhir.config.StoreAdapter;
import com.septiprima.tugasakhir.config.StoreItemsAdapter;
import com.septiprima.tugasakhir.config.UpdateItemsAdapter;
import com.septiprima.tugasakhir.constructor.Store;
import com.septiprima.tugasakhir.constructor.StoreItems;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {

    SwipeRefreshLayout swipe_items;
    RecyclerView recyler_items;
    private UpdateItemsAdapter mAdapter;
    List<StoreItems> itemslist = new ArrayList<StoreItems>();
    TextView store_name,store_loc;

    String id_store, nama, alamat, parkir;

    String TAG = StoreDetailActivity.class.getSimpleName();
    public static String idstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        swipe_items = (SwipeRefreshLayout) findViewById(R.id.swipe_update);
        recyler_items = (RecyclerView) findViewById(R.id.recycler_update_items);

        Bundle extras = getIntent().getExtras();
        idstore = extras.getString("idstore");

        getdata(idstore);
        swipe_items.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata(idstore);
            }
        });

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
                                item.setId(document.getId());
                                item.setName(document.getData().get("nama").toString());
                                item.setPrice(document.getData().get("harga").toString());
                                item.setImage(document.getData().get("image").toString());
                                itemslist.add(item);
                            }
                            Log.e(TAG, "onCreate: "+idstore );
                            mAdapter = new UpdateItemsAdapter(itemslist);
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

}
