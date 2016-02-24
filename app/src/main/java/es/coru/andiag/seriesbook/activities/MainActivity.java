package es.coru.andiag.seriesbook.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.ArrayList;
import java.util.List;

import es.coru.andiag.seriesbook.R;
import es.coru.andiag.seriesbook.db.DAO;
import es.coru.andiag.seriesbook.entities.Category;
import es.coru.andiag.seriesbook.fragments.SettingsFragment;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<Category> categoryList;

    private Drawer drawer = null;
    private AccountHeader header = null;

    private void createNavigationDrawer(Toolbar toolbar, Bundle savedInstanceState) {

        ArrayList<PrimaryDrawerItem> drawerItems = new ArrayList<>();
        DrawerBuilder builder = new DrawerBuilder(this).withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(header)
                .withSavedInstance(savedInstanceState)
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(android.R.drawable.ic_menu_manage).withIdentifier(10),
                        new SecondaryDrawerItem().withName(R.string.action_about).withIcon(android.R.drawable.ic_menu_manage)
                );

        for (Category c : categoryList) {
            builder.addDrawerItems(new PrimaryDrawerItem().withName(c.getName()).withIcon(android.R.drawable.ic_media_play));
        }
        builder.addDrawerItems(new PrimaryDrawerItem().withName(getString(R.string.add_category)).withIcon(R.drawable.ic_action_add));

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }


}
