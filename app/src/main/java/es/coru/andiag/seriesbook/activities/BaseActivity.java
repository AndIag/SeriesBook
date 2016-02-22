package es.coru.andiag.seriesbook.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import java.util.List;

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

    //This method allow us to update the navigation drawer when we add elements to the menu
    protected void updateNavigationDrawerView(NavigationView navigationView, List<String> noDeletableOptions) {
        for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
            final View child = navigationView.getChildAt(i);
            final MenuItem menuItem = navigationView.getMenu().getItem(i);
            //If the child is a deletable option we add an onLongClickListener to remove it
            if (menuItem != null && !noDeletableOptions.contains(String.valueOf(menuItem.getTitle()))) {
                if (child != null) {
                    child.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                }
            }
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }
}
