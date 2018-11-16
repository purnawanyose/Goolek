package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{


    String TAG = RegisterActivity.class.getSimpleName();
    EditText name,email,password;
    Button register;
    TextView login_link;
    String txt_name,txt_email,txt_password;

    String url_email = "https://septyprimabp.000webhostapp.com/emailsend.php";

    FirebaseFirestore db;

    boolean parameter = false;
    boolean parameterEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        name = findViewById(R.id.input_name);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        register = findViewById(R.id.btn_signup);
        register.setOnClickListener(this);
        login_link = findViewById(R.id.link_login);

        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_signup:

                validate();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, parameter + " " + parameterEmail);
                        if (parameter && parameterEmail) {
                            register();
                        } else {
                            Log.e(TAG,"error disini");
                        }
                        // checks if the field is valid

                        //Do something after 100ms
                    }
                }, 700);
                break;

            case R.id.link_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private boolean validate(){
        txt_name = name.getText().toString();
        txt_email = email.getText().toString();
        txt_password = password.getText().toString();

        if (txt_name != null  && txt_name.length() > 1 ) {
            parameter = true;
        }else {
            name.setError("This field is required");
            parameter = false;
        }
        if (txt_password != null  && txt_password.length() > 1 ) {
            parameter = true;
        }else {
            password.setError("This field is required");
            parameter = false;
        }
        if (txt_email != null  && txt_email.length() > 1 ) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference colRef = db.collection("users");
            colRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    if (document.getData().get("email").toString().equalsIgnoreCase(txt_email)){
                                        Log.d(TAG, "ketemu");
                                        Toast.makeText(getApplicationContext(),
                                                "This Email has been registered", Toast.LENGTH_LONG).show();
                                        email.setError("This Email has been registered");
                                        parameterEmail = false;
                                        break;
                                    }else {
                                        Log.d(TAG, "gak ketemu");
                                        parameterEmail = true;
                                    }
                                }
                            }else {
                                parameterEmail = false;
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
        }else {
            email.setError("This field is required");
            parameter = false;
        }

        return parameter;
    }

    private void register(){

        Map<String, Object> user = new HashMap<>();
        user.put("email", txt_email);
        user.put("password", txt_password);
        user.put("name", txt_name);

        db.collection("users").document()
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        sendemail(txt_email);
                        Toast.makeText(RegisterActivity.this,"Register Complete",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(RegisterActivity.this,"Register Complete",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
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
                        Toast.makeText(RegisterActivity.this,"email has been send",Toast.LENGTH_SHORT).show();

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
                Log.e(TAG,  "Json error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", txt_email);
                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

}
