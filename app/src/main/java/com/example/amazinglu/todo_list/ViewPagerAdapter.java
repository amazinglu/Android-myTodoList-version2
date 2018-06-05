package com.example.amazinglu.todo_list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.amazinglu.todo_list.all_list.AllListFragment;
import com.example.amazinglu.todo_list.model.Todo;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Todo> todoList;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return AllListFragment.newInstance(AllListFragment.KEY_LIST_TYPE_TODO);
        } else { // error
            return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }
}
