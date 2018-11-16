package com.septiprima.tugasakhir.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by yosep on 2/6/2018.
 */

public class prefManager {

    // LogCat tag
    private static String TAG = prefManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Goolek";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_USER = "user";

    public prefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

}
