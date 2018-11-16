package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.CategoriesAdapter;
import com.septiprima.tugasakhir.config.StoreAdapter;
import com.septiprima.tugasakhir.constructor.Categories;
import com.septiprima.tugasakhir.constructor.Store;
import com.septiprima.tugasakhir.fragment.Login_Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static SwipeRefreshLayout swipe_category;
    public static RecyclerView main_recyler;
    @BindView(R.id.openmap)
    RelativeLayout openmap;
    @BindView(R.id.nearme)
    RelativeLayout nearme;
    @BindView(R.id.favorite)
    RelativeLayout favorite;

    List<Categories> categoriesList;
    private CategoriesAdapter mAdapter;
    public static String iduser;
    public AutoCompleteTextView mAutocompleteTextView;

    String TAG = Login_Store.class.getSimpleName();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    List<Store> storelist = new ArrayList<Store>();
    List<String> responseList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        Bundle extras = getIntent().getExtras();
        iduser = extras.getString("iduser");

        //initiate
        categoriesList = new ArrayList<Categories>();
        swipe_category = (SwipeRefreshLayout) findViewById(R.id.swipe_category);
        main_recyler = (RecyclerView) findViewById(R.id.main_recyler);

        ButterKnife.bind(this);

        getMenu();
        getStore();

        swipe_category.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMenu();
            }
        });
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.ambil);
        mAutocompleteTextView.setSingleLine(true);
        mAutocompleteTextView.setThreshold(3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, responseList);
        mAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG," apakaha masuk sini? => " + responseList.get(position));
                getStorebyId(responseList.get(position));
            }
        });
        mAutocompleteTextView.setAdapter(adapter);

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }*/

    @OnClick(R.id.openmap)
    void setOpenmap(){
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.nearme)
    void setNearme(){
        Intent intent = new Intent(MainActivity.this, NearMeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.favorite)
    void setFavorite(){
        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
        intent.putExtra("id_store",iduser);
        startActivity(intent);
    }

    void getStore(){
        storelist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("store_users");
        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.e(TAG, document.getId() + " => " + document.getData());
                                if (document.getData().get("status").toString().equalsIgnoreCase("1")){
                                    Store store = new Store();
                                    store.setId(document.getId());
                                    store.setAlamat(document.getData().get("alamat").toString());
                                    store.setToko(document.getData().get("toko").toString());
                                    storelist.add(store);
                                    responseList.add(document.getData().get("toko").toString());
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

    void getStorebyId(final String id){
        storelist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("store_users");
        colRef
                .whereEqualTo("toko",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Intent intent = new Intent(MainActivity.this, StoreDetailActivity.class);
                                intent.putExtra("nama",document.getData().get("toko").toString());
                                intent.putExtra("alamat",document.getData().get("alamat").toString());
                                intent.putExtra("parkir",document.getData().get("parkir").toString());
                                intent.putExtra("id_store", document.getId());
                                startActivity(intent);
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

    void getMenu(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("categories");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            categoriesList.clear();
                            swipe_category.setRefreshing(true);
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("name"));
                                if (document.getId().equalsIgnoreCase("1")){

                                }else {
                                    Categories cat = new Categories(document.getData().get("name").toString(),document.getData().get("images").toString());
                                    Log.e(TAG, "onComplete: "+document.getData().get("images").toString() );
                                    categoriesList.add(cat);
                                }
                            }
                            Log.e(TAG, "onCreate: "+categoriesList.size());
                            mAdapter = new CategoriesAdapter(categoriesList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            main_recyler.setLayoutManager(mLayoutManager);
                            main_recyler.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            swipe_category.setRefreshing(false);
                            main_recyler.invalidate();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure to Log Out?");
            builder.setCancelable(true);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int i) {
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed(){

    }


}
