package com.septiprima.tugasakhir.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.activity.MainActivity;
import com.septiprima.tugasakhir.activity.RegisterActivity;
import com.septiprima.tugasakhir.activity.StoreMenuActivity;
import com.septiprima.tugasakhir.activity.StoreRegistryActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login_Store extends Fragment {

    private String title;
    private int page;
    @BindView(R.id.input_email)
    EditText email;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.btn_login)
    Button login;
    @BindView(R.id.link_signup)
    TextView signup;
    String TAG = Login_Store.class.getSimpleName();
    public static String idstore;

    public Login_Store() {
        // Required empty public constructor
    }

    public static Login_Store newInstance(int page, String title) {
        Login_Store fragmentSecond = new Login_Store();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        fragmentSecond.setArguments(args);
        return fragmentSecond;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
        title = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login__store, container, false);
        ButterKnife.bind(this,view);
        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.btn_login)
    void submit(){
        String emailtxt = email.getText().toString();
        final String passwordtxt = password.getText().toString();
        //Log.w(TAG, "submit: "+ emailtxt+" "+passwordtxt );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("store_users");

        colRef.whereEqualTo("email",emailtxt)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                                String datapass = document.getData().get("password").toString();
                                if (passwordtxt.equals(datapass)){
                                    idstore = document.getId();
                                    Intent intent = new Intent(getActivity(), StoreMenuActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("idstore",idstore);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                        }else {
                            Toast.makeText(getActivity(), "Email or Password is wrong", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Email or Password is wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error getting documents: ", e);
                    }
                });
    }

    @OnClick(R.id.link_signup)
    void setSignup(){
        Intent intent = new Intent(getActivity(), StoreRegistryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();

    }

}
