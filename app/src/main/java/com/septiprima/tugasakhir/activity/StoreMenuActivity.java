package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.fragment.Login_Store;

public class StoreMenuActivity extends AppCompatActivity{

    LinearLayout profile,manage_store,update_item;
    TextView updateitem,updateprofile,additem;
    String idstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_ment);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        profile = findViewById(R.id.profile);
        manage_store = findViewById(R.id.manage_store);
        update_item = findViewById(R.id.update_item);
        updateitem = findViewById(R.id.updateitem);
        updateprofile = findViewById(R.id.updateprofile);
        additem = findViewById(R.id.additems);

        Bundle extras = getIntent().getExtras();
        idstore = Login_Store.idstore;

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2= new Intent(StoreMenuActivity.this, UpdateProfilActivity.class);
                intent2.putExtra("idstore",idstore);
                startActivity(intent2);
            }
        });

        manage_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2= new Intent(StoreMenuActivity.this, Main2Activity.class);
                intent2.putExtra("idstore",idstore);
                startActivity(intent2);
            }
        });

        update_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3= new Intent(StoreMenuActivity.this, ItemsActivity.class);
                intent3.putExtra("idstore",idstore);
                startActivity(intent3);
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(StoreMenuActivity.this);
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
