package com.example.amazinglu.todo_list;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.amazinglu.todo_list.all_list.AllListFragment;
import com.example.amazinglu.todo_list.finish_list.FinishListFragment;
import com.example.amazinglu.todo_list.model.Todo;
import com.example.amazinglu.todo_list.todoList.TodoListFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Todo> allList;
    private String curFolderID;
    private TodoListFragment todoListFragment;
    private FinishListFragment finishListFragment;

    public ViewPagerAdapter(FragmentManager fm, List<Todo> list, String curFolderID) {
        super(fm);
        allList = list;
        this.curFolderID = curFolderID;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            todoListFragment = TodoListFragment.newInstance(allList, curFolderID);
            return todoListFragment;
        } else { // error
            finishListFragment = FinishListFragment.newInstance(allList, curFolderID);
            return finishListFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        todoListFragment.updateTodo();
        finishListFragment.updateTodo();
        return POSITION_UNCHANGED;
    }
}
