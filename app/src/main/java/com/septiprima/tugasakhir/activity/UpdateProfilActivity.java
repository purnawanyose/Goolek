package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.StoreItemsAdapter;
import com.septiprima.tugasakhir.constructor.Store;
import com.septiprima.tugasakhir.constructor.StoreItems;

import java.util.ArrayList;
import java.util.List;

public class UpdateProfilActivity extends AppCompatActivity {

    SwipeRefreshLayout swipe_items;
    RecyclerView recycler_view_items;
    List<StoreItems> itemslist = new ArrayList<StoreItems>();
    private StoreItemsAdapter mAdapter;
    String idstore;

    TextView name, address, phone, email, payment, park;
    Button editprofile;
    ImageView swipe_more;

    String TAG = StoreDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profil);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        swipe_items = findViewById(R.id.swipe_items);
        recycler_view_items = findViewById(R.id.recycler_view_items);
        swipe_more = findViewById(R.id.swipe_more);

        name = findViewById(R.id.store_name);
        address = findViewById(R.id.store_loc);
        phone = findViewById(R.id.phone_store);
        email = findViewById(R.id.email_store);
        payment = findViewById(R.id.payment_store);
        park = findViewById(R.id.park_store);
        editprofile = findViewById(R.id.edit_profile);

        Bundle extras = getIntent().getExtras();
        idstore = extras.getString("idstore");

        getdata(idstore);
        getdataProfile(idstore);
        swipe_items.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata(idstore);
            }
        });

        swipe_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setVisibility(View.VISIBLE);
                payment.setVisibility(View.VISIBLE);
                park.setVisibility(View.VISIBLE);
                editprofile.setVisibility(View.VISIBLE);
                swipe_more.setVisibility(View.GONE);
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfilActivity.this, EditProfil2.class);
                intent.putExtra("idstore",idstore);
                startActivity(intent);
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
                                item.setName(document.getData().get("nama").toString());
                                item.setPrice(document.getData().get("harga").toString());
                                item.setImage(document.getData().get("image").toString());
                                itemslist.add(item);
                            }
                            Log.e(TAG, "onCreate: "+idstore );
                            mAdapter = new StoreItemsAdapter(itemslist);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recycler_view_items.setLayoutManager(mLayoutManager);
                            recycler_view_items.setAdapter(mAdapter);
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

    private void getdataProfile(final String idstore){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("store_users").document(idstore);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        name.setText( document.getData().get("toko").toString());
                        address.setText( document.getData().get("alamat").toString());
                        phone.setText( document.getData().get("telepon").toString());
                        email.setText( document.getData().get("email").toString());
                        park.setText( document.getData().get("parkir").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

/*        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                name.setText( document.getData().get("toko").toString());
                                address.setText( document.getData().get("alamat").toString());
                                phone.setText( document.getData().get("telepon").toString());
                                email.setText( document.getData().get("email").toString());
                                park.setText( document.getData().get("parkir").toString());
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
                });*/
    }

}
