package es.coru.andiag.seriesbook.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

import es.coru.andiag.seriesbook.R;
import es.coru.andiag.seriesbook.db.DAO;
import es.coru.andiag.seriesbook.entities.Category;
import es.coru.andiag.seriesbook.fragments.SettingsFragment;

public class MainActivity extends BaseActivity {


    private static final long NAV_ADD_CATEGORY = 5;
    private static final long NAV_SETTINGS_IDENTIFIER = 10;
    private static final long NAV_ABOUT_IDENTIFIER = 11;
    private final Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            long identifier = drawerItem.getIdentifier();
            if (identifier >= 0) { //Static features behavior
                if (identifier == NAV_ADD_CATEGORY) {
                    Log.d(TAG, "Adding category");
                    generateMaterialDialog(R.string.creating_category, R.layout.dialog_add_category, R.string.create, new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            EditText categoryName = (EditText) dialog.getView().findViewById(R.id.categoryNameText);
                            boolean a = !categoryName.getText().toString().matches("");
                            if (a) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.creating_category) + " : " + categoryName.getText().toString(),
                                        Toast.LENGTH_SHORT).show();

                                Category category = new Category();
                                category.setName(categoryName.getText().toString());
                            }

                        }
                    });
                    return true;
                }
                if (identifier == NAV_SETTINGS_IDENTIFIER) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, new SettingsFragment())
                            .commit();
                    return false;
                }
                if (identifier == NAV_ABOUT_IDENTIFIER) {
                    //Implement dialog about here
                    return false;
                }
            }
            //Categories fragment behavior
            return false;
        }
    };
    private List<Category> categoryList;
    private Drawer drawer = null;
    private AccountHeader header = null;

    //region Creating Navigation Drawer
    private void createNavigationDrawer(Toolbar toolbar, Bundle savedInstanceState) {
        DrawerBuilder builder = new DrawerBuilder(this).withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(header)
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(drawerItemClickListener)
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withName(R.string.action_settings)
                                .withIcon(android.R.drawable.ic_menu_manage)
                                .withIdentifier(NAV_SETTINGS_IDENTIFIER),
                        new SecondaryDrawerItem()
                                .withName(R.string.action_about)
                                .withIdentifier(NAV_ABOUT_IDENTIFIER)
                                .withIcon(android.R.drawable.ic_menu_manage)
                );

        for (Category c : categoryList) {
            builder.addDrawerItems(new PrimaryDrawerItem().withName(c.getName()).withIcon(android.R.drawable.ic_media_play));
        }

        builder.addDrawerItems(new PrimaryDrawerItem()
                .withName(getString(R.string.add_category))
                .withIdentifier(NAV_ADD_CATEGORY)
                .withIcon(R.drawable.ic_action_add)
                .withSelectable(false));

        drawer = builder.build();

    }
    private void buildHeader() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        int color = typedValue.data;
        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(color);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(drawable)
                .addProfiles(
                        new ProfileDrawerItem().withName("AndIag").withEmail("andiag.dev@gmail.com").withIcon(R.drawable.andiag)
                )
                .build();

    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        categoryList = DAO.getInstance(this).getCategories();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        buildHeader();
        createNavigationDrawer(toolbar, savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new SettingsFragment())
                        .commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
