package com.septiprima.tugasakhir.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.septiprima.tugasakhir.R;
import com.septiprima.tugasakhir.fragment.Login_Pengunjung;
import com.septiprima.tugasakhir.fragment.Login_Store;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    ViewPager login_viewpager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.main_actions);

        login_viewpager = (ViewPager) findViewById(R.id.login_viewpager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        login_viewpager.setAdapter(adapter);
        login_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        break;
                    case 1 :
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(browserIntent);*/



    }

    //viewpager adapter
    class ViewPagerAdapter extends FragmentPagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Login_Pengunjung.newInstance(0, "Page # 1");

                case 1 :
                    return Login_Store.newInstance(1, "Page # 2");

                default:
                    return Login_Pengunjung.newInstance(0,"Page # 1");
            }
        }
    }

}
