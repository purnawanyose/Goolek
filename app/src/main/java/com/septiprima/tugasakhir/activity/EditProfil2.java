package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.constructor.Categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfil2 extends AppCompatActivity {

    private static String TAG = EditProfil2.class.getSimpleName();
    private Button step1, step2, step3, select_photo;
    private EditText input_name, input_email, input_password,
            input_name_store, store_address, input_phone;
    Spinner category, payment, park;
    TextView login_link;
    FirebaseFirestore db;
    String idstore;

    //==============================================================================================
    private String txt_name, txt_email, txt_pass, txt_name_store,
            txt_store_category, txt_store_address, txt_store_park, txt_paymentmethod,
            txt_phone;

    String extraName, extraEmail, extraPass, extraAddress, extraKota;
    Double extraLat, extraLng;

    List<Categories> categoriesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil2);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        db = FirebaseFirestore.getInstance();

        step3 = findViewById(R.id.btn_step_3);
        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        //input_password = findViewById(R.id.input_password);
        input_name_store = findViewById(R.id.input_name_store);
        store_address = findViewById(R.id.store_address);
        park = findViewById(R.id.parking);
        category = findViewById(R.id.category_spin);
        payment = findViewById(R.id.payment);
        input_phone = findViewById(R.id.input_phone);

        //========================================================
        categoriesList = new ArrayList<Categories>();



        Bundle extras = getIntent().getExtras();
        idstore = extras.getString("idstore");

        getMenu();
        getData(idstore);

        ArrayList<String> paymentmethod = new ArrayList<String>();
        paymentmethod.add("Select Payment Method");
        paymentmethod.add("Only Cash Cash");
        paymentmethod.add("Cash or Credit Card");
        paymentmethod.add("Only Credit Card");

        ArrayAdapter SpinAdapter2 = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paymentmethod);
        SpinAdapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        payment.setAdapter(SpinAdapter2);

        ArrayList<String> parkingspot = new ArrayList<String>();
        parkingspot.add("Select Store Parking Spot");
        parkingspot.add("Big Parking Spot");
        parkingspot.add("Medium Parking Spot");
        parkingspot.add("Small Parking Spot");

        parkingspot.add("No Parking Spot");

        ArrayAdapter SpinAdapter3 = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, parkingspot);
        SpinAdapter3.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        park.setAdapter(SpinAdapter3);

        step3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofil();
            }
        });


    }

    void getMenu() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("categories");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoriesList.clear();

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("name"));
                                Categories cat = new Categories(document.getData().get("name").toString(), document.getData().get("images").toString());
                                categoriesList.add(cat);
                            }
                            Log.e(TAG, "onCreate: " + categoriesList.size());
                            fillspinner();
                            return;
                        } else {
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

    private void fillspinner() {
        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray = new ArrayList<String>();

        for (int i = 0; i < categoriesList.size(); i++) {
            spinnerArray.add(categoriesList.get(i).getName());
            Log.e("spinner test: ", categoriesList.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        category.setAdapter(adapter);
    }

    void getData(String idstore){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("store_users").document(idstore);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        input_name.setText( document.getData().get("toko").toString());
                        store_address.setText( document.getData().get("alamat").toString());
                        input_phone.setText( document.getData().get("telepon").toString());
                        input_email.setText( document.getData().get("email").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void updateprofil(){

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt_store_category = categoriesList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt_paymentmethod = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        park.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt_store_park = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Map<String, Object> user = new HashMap<>();
        user.put("id", idstore);
        user.put("alamat", store_address.getText());
        user.put("pemilik", input_name.getText());
        user.put("email", input_email.getText());
        user.put("toko", input_name_store.getText());
        user.put("telepon", input_phone.getText());
        user.put("category", txt_store_category);
        user.put("parkir", txt_store_park);
        user.put("metode bayar", txt_paymentmethod);

        db.collection("store_items").document(idstore)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(EditProfil2.this,"Register Complete",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfil2.this, StoreMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(EditProfil2.this,"Register Complete",Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
