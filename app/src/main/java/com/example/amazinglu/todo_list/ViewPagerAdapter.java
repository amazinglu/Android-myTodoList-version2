package com.example.amazinglu.todo_list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.amazinglu.todo_list.all_list.AllListFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return AllListFragment.newInstance(AllListFragment.KEY_LIST_TYPE_TODO);
        } else if (position == 1) {
            return AllListFragment.newInstance(AllListFragment.KEY_LIST_TYPE_FINISH);
        } else { // error
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
