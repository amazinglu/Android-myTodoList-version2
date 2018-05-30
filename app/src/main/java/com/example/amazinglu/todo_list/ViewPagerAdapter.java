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
        return AllListFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
