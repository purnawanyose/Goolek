package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.CategoriesAdapter;
import com.septiprima.tugasakhir.constructor.Categories;
import com.septiprima.tugasakhir.constructor.StoreItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRegistryActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = StoreRegistryActivity.class.getSimpleName();
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button step1, step2, step3, select_photo;
    private EditText input_name, input_email, input_password,
            input_name_store, store_address, input_phone;
    Spinner category, payment, park;
    TextView login_link;
    FirebaseFirestore db;

    //==============================================================================================
    private String txt_name, txt_email, txt_pass, txt_name_store,
            txt_store_category, txt_store_address, txt_store_park, txt_paymentmethod,
            txt_phone;

    String extraName, extraEmail, extraPass, extraAddress, extraKota;
    Double extraLat, extraLng;

    boolean parameter = false;
    boolean parameter1 = false;
    boolean parameterEmail = false;

    List<Categories> categoriesList;

    private String url_email = "https://septyprimabp.000webhostapp.com/emailsend.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_registry);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        db = FirebaseFirestore.getInstance();

        step1 = findViewById(R.id.btn_step_1);
        step3 = findViewById(R.id.btn_step_3);
        login_link = findViewById(R.id.link_login);

        step1.setOnClickListener(this);
        step3.setOnClickListener(this);
        login_link.setOnClickListener(this);

        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_name_store = findViewById(R.id.input_name_store);
        store_address = findViewById(R.id.store_address);
        park = findViewById(R.id.parking);
        viewPager = findViewById(R.id.registry_viewpager);
        category = findViewById(R.id.category_spin);
        payment = findViewById(R.id.payment);
        input_phone = findViewById(R.id.input_phone);

        //========================================================
        categoriesList = new ArrayList<Categories>();


        @Nullable
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            extraName = extras.getString("nama");
            extraEmail = extras.getString("email");
            extraPass = extras.getString("pass");
            extraAddress = extras.getString("address");
            extraLat = extras.getDouble("lat");
            extraLng = extras.getDouble("lng");
            extraKota = extras.getString("kota");

            if (!extraName.equalsIgnoreCase("")) {
                input_name.setText(extraName);
                input_email.setText(extraEmail);
                input_password.setText(extraPass);
                store_address.setText(extraAddress);
            }
        }

        getMenu();

        ArrayList<String> paymentmethod = new ArrayList<String>();
        paymentmethod.add("Select Payment Method");
        paymentmethod.add("Only Cash");
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

        store_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(StoreRegistryActivity.this, FindLocActivity.class);
                intent.putExtra("nama", txt_name);
                intent.putExtra("email", txt_email);
                intent.putExtra("pass", txt_pass);
                startActivity(intent);
                return false;
            }
        });

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_step_1:

                validate_step1();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, parameter + " " + parameterEmail);
                        if (parameter1 && parameterEmail) {
                            viewPager.setCurrentItem(1);
                        } else {
                            Log.e(TAG,"error disini");
                        }
                        // checks if the field is valid

                        //Do something after 100ms
                    }
                }, 700);
                break;




            case R.id.btn_step_3:

                if (validate_step2()) {
                    register();
                } else {

                }

                break;


            case R.id.link_login:
                Intent intent = new Intent(StoreRegistryActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.step_1;
                    break;
                case 1:
                    resId = R.id.step_2;
                    break;
            }
            return findViewById(resId);
        }
    }

    private boolean validate_step1() {
        txt_name = input_name.getText().toString();
        txt_email = input_email.getText().toString();
        txt_pass = input_password.getText().toString();
        txt_name_store = input_name_store.getText().toString();

        if (txt_name != null && txt_name.length() > 1) {
            parameter1 = true;
        } else {
            input_name.setError("This field is required");
            parameter1 = false;
        }
        if (txt_pass != null && txt_pass.length() > 1) {
            parameter1 = true;
        } else {
            input_password.setError("This field is required");
            parameter1 = false;
        }
        if (txt_email != null && txt_email.length() > 1) {
            /*FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference colRef = db.collection("store_users");
            colRef
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "test");
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String email = document.getData().get("email").toString();
                                        if (email.equalsIgnoreCase(txt_email)){
                                            Log.d(TAG, "ketemu");
                                            parameterEmail = false;
                                        }else {
                                            Log.d(TAG, "gak ketemu");
                                            parameterEmail = true;
                                        }
                                }
                            }else {
                                parameterEmail = false;
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            parameterEmail = true;
                            Log.e(TAG, "Error getting documents: ", e);
                        }
                    });*/
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference colRef = db.collection("store_users");
            colRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.e(TAG, document.getId() + " => " + document.getData().get("email").toString());
                                    if (document.getData().get("email").toString().equalsIgnoreCase(txt_email)){
                                            Log.d(TAG, "ketemu");
                                        Toast.makeText(getApplicationContext(),
                                                "This Email has been registered", Toast.LENGTH_LONG).show();
                                            input_email.setError("This Email has been used");
                                            parameterEmail = false;
                                        break;
                                    }else {
                                        Log.d(TAG, "gak ketemu");
                                        parameterEmail = true;
                                    }
                                }
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
        } else {
            input_email.setError("This field is required");
            parameter1 = false;
        }
        return parameter1;
    }

    private boolean validate_step2() {
        txt_name_store = input_name_store.getText().toString();
        txt_store_address = store_address.getText().toString();
        txt_phone = input_phone.getText().toString();
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
                txt_paymentmethod = parent.getItemAtPosition(position).toString() + position;
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


        boolean parameter = false;

        if (txt_name_store != null && txt_name_store.length() > 1) {
            parameter = true;
        } else {
            input_name_store.setError("This field is required");
            parameter = false;
        }
        if (txt_store_address != null && txt_store_address.length() > 1) {
            parameter = true;
        } else {
            store_address.setError("This field is required");
            parameter = false;
        }
        if (txt_phone != null && txt_phone.length() > 1) {
            parameter = true;
        } else {
            input_phone.setError("This field is required");
            parameter = false;
        }

        return parameter;
    }

    private void register() {

        Map<String, Object> store_user = new HashMap<>();
        store_user.put("alamat", txt_store_address);
        store_user.put("category", txt_store_category);
        store_user.put("email", txt_email);
        store_user.put("password", txt_pass);
        store_user.put("lat", extraLat);
        store_user.put("lng", extraLng);
        store_user.put("kota", extraKota);
        store_user.put("parkir", txt_store_park);
        store_user.put("pemilik", txt_name);
        store_user.put("toko", txt_name_store);
        store_user.put("metode bayar", txt_paymentmethod);
        store_user.put("telepon", txt_phone);
        store_user.put("status", 0);
        store_user.put("photo", "");
        store_user.put("visitor", 1);

        Log.e(TAG, "register: " + store_user);

        db.collection("store_users").document()
                .set(store_user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        sendemail(txt_email);
                        Toast.makeText(StoreRegistryActivity.this, "Register Complete", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StoreRegistryActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(StoreRegistryActivity.this, "Register Error", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StoreRegistryActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
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
                                Categories cat = new Categories(document.getData().get("name").toString(),
                                        document.getData().get("images").toString());
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

    private void sendemail(final  String email){
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url_email, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response ok: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.e(TAG,"email send");
                        Toast.makeText(StoreRegistryActivity.this,"email has been send",Toast.LENGTH_SHORT).show();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG,  "Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

}
