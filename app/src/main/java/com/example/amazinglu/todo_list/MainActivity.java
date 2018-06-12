package com.example.amazinglu.todo_list;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.amazinglu.todo_list.finish_list.FinishListFragment;
import com.example.amazinglu.todo_list.model.Folder;
import com.example.amazinglu.todo_list.model.Todo;
import com.example.amazinglu.todo_list.todoList.TodoListFragment;
import com.example.amazinglu.todo_list.util.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NewFolderDialogFragment.NewFolderTitleINputListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.view_page) ViewPager viewPager;
    @BindView(R.id.view_pager_tab) TabLayout tabLayout;
    @BindView(R.id.nav_drawer) NavigationView navigationView;

    private static final int FOLDER_MENUS_GROUP_ID = 100;

    private ActionBarDrawerToggle drawerToggle;
    private List<Todo> allList;
    private ViewPagerAdapter viewPagerAdapter;
    private String curFolderID;

    private Map<String, Folder> folders;

//    public static final String TODO = "todo_list";
    public static final String LAST_FOLDER_ID = "last_folder_id";
    public static final String FOLDER_COLLECTION = "folder_collection";

    // test purpose
    int count = 0;

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

        /**
         * 要保持view pager中每一个fragment都是一个todo list，好让数据同步
         * 所以还是在main activity中建立这个list然后fragment中都是用这个list的引用吧
         * */
        loadData();
        setUpViewPager();
        setUpDrawer();
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

    // add all the folder onto the navigation drawer
    private void addMenusItemInNavDrawer() {

        /**
         * add menus and submenus in navigation drawer 动态的
         * 注意 R.id.todo_list_folder 这个item里面得有一个submenus的框架，不然的话subMenus会是null
         * */
        Menu menu = navigationView.getMenu();
        Menu subMenu = menu.findItem(R.id.todo_list_folder).getSubMenu();

        /**
         * 加入item的时候，要给item一个id， 如果要给item一个R.id的话，就要再res/value/ids的文件中先定义好id，
         * 然后再付给这些item
         * 但是resrouse file 里面的item是不可以动态添加的
         * */
//        subMenu.add(FOLDER_MENUS_GROUP_ID, R.id.folder1, 0, "Super Item1");
//        subMenu.add("Super Item2");
//        subMenu.add("Super Item3");

        for (String key : folders.keySet()) {
            subMenu.add(folders.get(key).title);
        }

        // force the view to draw again
        navigationView.invalidate();

    }

    // add one new folder onto navigation drawer
    private void addMenusItemInNavDrawer(String title) {
        Menu menu = navigationView.getMenu();
        Menu subMenu = menu.findItem(R.id.todo_list_folder).getSubMenu();
        subMenu.add(title);

        navigationView.invalidate();
    }

    private void setUpDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);

        // TODO: drawer的文件夹功能
        addMenusItemInNavDrawer();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }

                if (item.getItemId() == R.id.add_new_folder) {
                    NewFolderDialogFragment newFolderDialogFragment = NewFolderDialogFragment.newInstance();
                    newFolderDialogFragment.show(getSupportFragmentManager(), NewFolderDialogFragment.NEW_FOLDER_DIALOG_TAG);
                } else {
                    for (String key : folders.keySet()) {
                        if (folders.get(key).title.equals(item.getTitle())) {
                            Toast.makeText(MainActivity.this, folders.get(key).title, Toast.LENGTH_SHORT).show();
                            // a deep copy is needed here
                            curFolderID = folders.get(key).id;
                            updateAllList(folders.get(key));
                            break;
                        }
                    }

                    // TODO: update the view pager with new data
//                viewPagerAdapter.notifyDataSetChanged();
//                tabLayout.getTabAt(0).setText(R.string.todo_list);
//                tabLayout.getTabAt(1).setText(R.string.finish_list);
                    /**
                     * 更新整个view pager的数据
                     * my way is to setup the view pager again
                     * or we can use the way above, update the fragment inside the view page inside getItemPosition
                     * then set the title again
                     * */
                    setUpViewPager();
                }

                drawerLayout.closeDrawers();

                return false;
            }
        });
    }

    /**
     * main activity 和 view page 中的所有fragment的alllist都是指向同一个地址的，所以update allList的时候
     * 一定要改变给地址里面的内容，而不能让allList指向别的地址
     * */
    private void updateAllList(Folder folder) {
        if (allList == null) {
            allList = new ArrayList<>();
        }
        allList.clear();
        for (Todo element : folder.allList) {
            allList.add(element);
        }
    }

    private void setUpViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), allList, curFolderID);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        /**
         * 让 toolbar和view pager tab之间没有突出的shadow，
         * 首先要让toolbar的elevation为0，然后让AppBarLayout的elevation也为0
         * */
        getSupportActionBar().setElevation(0);
        tabLayout.getTabAt(0).setText(R.string.todo_list);
        tabLayout.getTabAt(1).setText(R.string.finish_list);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(MainActivity.this, "now is position " + position, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    int id = viewPager.getCurrentItem();
                    /**
                     * 更新view pager当前的显示的fragment的数据
                     * get the current fragment from view pager
                     * do not use getItem, the getItem only call when first create the fragment in viewpager
                     * after the view pager is created, the fragment is on FragmentManager
                     * */
                    TodoListFragment todoListFragment = (TodoListFragment) getSupportFragmentManager().
                            findFragmentByTag("android:switcher:"+R.id.view_page+":"+id);
                    todoListFragment.updateTodo();
                } else if (position == 1) {
                    int id = viewPager.getCurrentItem();
                    FinishListFragment finishListFragment = (FinishListFragment) getSupportFragmentManager().
                            findFragmentByTag("android:switcher:"+R.id.view_page+":"+id);
                    finishListFragment.updateTodo();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void createNewFolder(String title) {
        Folder folder = new Folder(title);
        folders.put(folder.id, folder);
        ModelUtils.save(MainActivity.this, FOLDER_COLLECTION, folders);
        addMenusItemInNavDrawer(title);
    }

    private void loadData() {
        // get the last folder id
        curFolderID = ModelUtils.read(MainActivity.this, LAST_FOLDER_ID, new TypeToken<String>(){});
        if (curFolderID == null) { // first load the app => fake data
            folders = new HashMap<>();
            fakeData();
            ModelUtils.save(MainActivity.this, FOLDER_COLLECTION, folders);
        } else {
            folders = ModelUtils.read(MainActivity.this, FOLDER_COLLECTION, new TypeToken<Map<String, Folder>>(){});
//            allList = folders.get(curFolderID).allList;
            updateAllList(folders.get(curFolderID));
        }

    }

    private void fakeData() {
        Folder folder1 = new Folder("amazing folder 1");
        fakeData1(folder1);

        Folder folder2 = new Folder("amazing folder 2");
        fakeData2(folder2);
        folders.put(folder1.id, folder1);
        folders.put(folder2.id, folder2);
        curFolderID = folder1.id;
//        allList = folder2.allList;
        updateAllList(folder1);
    }

    private void fakeData2(Folder folder) {
        Calendar c = Calendar.getInstance();
        Date cur = c.getTime();
        c.add(Calendar.DATE, -1);
        Date past = c.getTime();
        c.add(Calendar.DATE, 2);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 1);
        Date d = c.getTime();

        folder.allList.add(new Todo("past1", past));
        folder.allList.get(0).description = "avaduvewqbvnqevqwvqwvqwe";
        folder.allList.add(new Todo("past2", past));
        folder.allList.get(1).description = "avaduvewqbvnqevqwvqwvqwe";
    }

    private void fakeData1(Folder folder) {
        Calendar c = Calendar.getInstance();
        Date cur = c.getTime();
        c.add(Calendar.DATE, -1);
        Date past = c.getTime();
        c.add(Calendar.DATE, 2);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 1);
        Date d = c.getTime();

        folder.allList.add(new Todo("past1", past));
        folder.allList.get(0).description = "avaduvewqbvnqevqwvqwvqwe";
        folder.allList.add(new Todo("past2", past));
        folder.allList.get(1).description = "avaduvewqbvnqevqwvqwvqwe";
        folder.allList.add(new Todo("current1", cur));
        folder.allList.get(2).isFinished = true;
        folder.allList.add(new Todo("current2", cur));
        folder.allList.get(3).isFinished = true;
        folder.allList.add(new Todo("tomorrow1", tomorrow));
        folder.allList.add(new Todo("tomorrow2", tomorrow));
        folder.allList.add(new Todo("the day after tomorrow1", d));
    }

    @Override
    public void sentTitleInput(String newTitle) {
        createNewFolder(newTitle);
    }
}
