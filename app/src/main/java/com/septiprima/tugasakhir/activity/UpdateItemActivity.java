package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.config.CategoriesAdapter;
import com.septiprima.tugasakhir.constructor.Categories;
import com.septiprima.tugasakhir.constructor.StoreItems;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateItemActivity extends AppCompatActivity {

    String TAG = UpdateItemActivity.class.getSimpleName();
    String idstore;
    String txt_name, txt_harga, txt_image, txt_category;
    EditText name,harga,image;
    Spinner category;
    Button AddItem;
    private ArrayList<StoreItems> itemList;
    String getname,getprice,getimg,id;
    ImageView imagepick;
    TextView img_name;
    FirebaseFirestore db;
    String filename;
    public static final int PICK_IMAGE = 1;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        Bundle extras = getIntent().getExtras();
        idstore = extras.getString("idstore");
        getname = extras.getString("name");
        getprice = extras.getString("harga");
        getimg = extras.getString("img");
        id= extras.getString("id");

        filename = extras.getString("img");

        Log.e("test ", id);

        name = (EditText) findViewById(R.id.input_items);
        harga = (EditText) findViewById(R.id.input_harga);
        image = (EditText) findViewById(R.id.input_image);
        AddItem = (Button) findViewById(R.id.btn_Update);
        imagepick = (ImageView) findViewById(R.id.imagepick);
        img_name = (TextView) findViewById(R.id.img_name);

        name.setText(getname);
        harga.setText(getprice);
        image.setText(getimg);
        db = FirebaseFirestore.getInstance();

        itemList = new ArrayList<StoreItems>();

        AddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
                if (validate()){
                    progressDialog = new ProgressDialog(UpdateItemActivity.this);
                    progressDialog.setMessage("Loading..."); // Setting Message
                    progressDialog.setTitle("Add item"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    addItem();
                }else {
                    Log.e(TAG, "onClick: must not empty" );
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecImg();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();

            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
             filename= imagePath.substring(imagePath.lastIndexOf("/")+1);
            // Now we need to set the GUI ImageView data with data read from the picked file.
            imagepick.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            image.setText(imagePath);
            img_name.setText("Please check data and image before you create item.");

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

    private void selecImg(){
        // Create the Intent for Image Gallery.
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, PICK_IMAGE);
    }

    private boolean validate(){
        txt_name = name.getText().toString();
        txt_harga = harga.getText().toString();
        txt_image = image.getText().toString();

        boolean parameter = true;

        if (txt_name != null  && txt_name.length() > 1 ) {
            parameter = true;
        }else {
            name.setError("This field is required");
            parameter = false;
        }
        if (txt_harga != null  && txt_harga.length() > 1 ) {
            parameter = true;
        }else {
            harga.setError("This field is required");
            parameter = false;
        }
        if (txt_image != null  && txt_image.length() > 1 ) {
            parameter = true;
        }else {
            image.setError("This field is required");
            parameter = false;
        }
        return parameter;
    }

    private void addItem(){

        Map<String, Object> user = new HashMap<>();
        user.put("id", idstore);
        user.put("harga", txt_harga);
        user.put("nama", txt_name);
        user.put("image", filename);

        DocumentReference contact = db.collection("store_items").document(id);
        contact.update("harga", txt_harga);
        contact.update("id", idstore);
        contact.update("image", filename);
        contact.update("nama", txt_name)
                .addOnSuccessListener(new OnSuccessListener < Void > () {
                    @Override
                    public void onSuccess(Void aVoid) {


                        UploadTask uploadTask;
                        Uri file = Uri.fromFile(new File(txt_image));
                        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                        uploadTask = riversRef.putFile(file);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.e(TAG, "onFailure: "+exception.toString() );
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Toast.makeText(UpdateItemActivity.this, "Updated Successfully",
                                        Toast.LENGTH_SHORT).show();
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                progressDialog.dismiss();
                                Intent intent = new Intent(UpdateItemActivity.this, StoreMenuActivity.class);
                                intent.putExtra("idstore",idstore);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
                Toast.makeText(UpdateItemActivity.this,"Data not Updated",Toast.LENGTH_SHORT).show();
            }
        });


    }


}
