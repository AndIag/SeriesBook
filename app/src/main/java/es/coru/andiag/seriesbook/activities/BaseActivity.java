package es.coru.andiag.seriesbook.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import es.coru.andiag.seriesbook.R;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public static final String PREF_THEME_KEY = "theme_list";

    public final static int THEME_LIGHT = 0;
    public final static int THEME_DARK = 1;

    public static int getTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(prefs.getString(PREF_THEME_KEY, "-1"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
    }

    private void updateTheme() { //Change theme to all activities that extends BaseActivity
        if (getTheme(getApplicationContext()) == THEME_DARK) {
            setTheme(R.style.AppTheme_Dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkPrimaryDark));
            }
            return;
        }
        if (getTheme(getApplicationContext()) == THEME_LIGHT) {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.lightPrimaryDark));
            }
            return;
        }
        setTheme(R.style.AppTheme);
    }
}
