package es.coru.andiag.seriesbook.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import es.coru.andiag.seriesbook.R;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public static final String PREF_THEME_KEY = "theme_list";

    public final static int THEME_LIGHT = 0;
    public final static int THEME_DARK = 1;
    public final static int THEME_GREEN = 2;

    public static int getTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(prefs.getString(PREF_THEME_KEY, "-1"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
    }


    //region Creating our own dialogs
    public void generateMaterialDialog(int titleResource, int customView, int positiveTextResource, MaterialDialog.SingleButtonCallback positiveCallback) {
        new MaterialDialog.Builder(this)
                .title(titleResource)
                .autoDismiss(false)
                .positiveText(positiveTextResource)
                .negativeText(R.string.cancel)
                .customView(customView, true)
                .onPositive(positiveCallback)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void generateMaterialDialogWithSpinner(int titleResource, int customView, int positiveTextResource, MaterialDialog.SingleButtonCallback positiveCallback, List<String> list) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title(titleResource)
                .autoDismiss(false)
                .positiveText(positiveTextResource)
                .negativeText(R.string.cancel)
                .customView(customView, true)
                .onPositive(positiveCallback)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        //Adding category names to spinner
        Spinner spinner = (Spinner) materialDialog.findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(arrayAdapter);
    }

    //endregion

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
        if (getTheme(getApplicationContext()) == THEME_GREEN) {
            setTheme(R.style.AppTheme_Green);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.greenPrimaryDark));
            }
            return;
        }
        setTheme(R.style.AppTheme);
    }
}
