package com.example.amazinglu.todo_list;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.view_page) ViewPager viewPager;
    @BindView(R.id.view_pager_tab) TabLayout tabLayout;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /**
         * enable the home button to show
         * ensble the sandwich button to show
         * */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setUpDrawer();
        setUpViewPager();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        /**
         * tell the drawerToggle the current state
         * state 1: drawer is open
         * state 2: drawer is closed
         * use for the icon and the animation of the sandwich button
         * */
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /**
         * when the drawer close and open, the configuration is change
         * this function help the OS to setup new configuration
         * */
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * the click event of the sandwich button
         * */
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void setUpViewPager() {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        /**
         * 让 toolbar和view pager tab之间没有突出的shadow，
         * 首先要让toolbar的elevation为0，然后让AppBarLayout的elevation也为0
         * */
        getSupportActionBar().setElevation(0);
        tabLayout.getTabAt(0).setText(R.string.all_list);
        tabLayout.getTabAt(1).setText(R.string.todo_list);
        tabLayout.getTabAt(2).setText(R.string.finish_list);
    }
}
