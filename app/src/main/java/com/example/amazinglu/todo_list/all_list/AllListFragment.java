package com.example.amazinglu.todo_list.all_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.edit_list.EditListActivity;
import com.example.amazinglu.todo_list.edit_list.EditListFragment;
import com.example.amazinglu.todo_list.model.Todo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllListFragment extends Fragment {
    public static final String KEY_LIST_TYPE = "list_type";
    public static final String KEY_LIST_TYPE_TODO = "list_type_todo";
    public static final String KEY_LIST_TYPE_FINISH = "list_type_finish";
    public static final String KEY_LIST_DATA = "list_data";

    public static final int REQ_EDIT_CODE = 1;

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.float_action_button_add) FloatingActionButton floatingActionButton;

    private int pageNum;
    private TodoListAdapter adapter;
    private List<Todo> todoList;

    public static AllListFragment newInstance(String listType) {
        Bundle args = new Bundle();
        args.putString(KEY_LIST_TYPE, listType);
        AllListFragment allListFragment = new AllListFragment();
        allListFragment.setArguments(args);
        return allListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_list_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String listType = getArguments().getString(KEY_LIST_TYPE);
        todoList = getArguments().getParcelableArrayList(KEY_LIST_DATA);
        ButterKnife.bind(this, view);

        fakeDate();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        /**
         * recycler view 中每个item都有下划线隔开，可以自己定义一个Decoration， 也可以用DividerItemDecoration
         * */
//        recyclerView.addItemDecoration(new com.example.amazinglu.todo_list.all_list.DividerItemDecoration(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new TodoListAdapter(todoList, listType, this);
        recyclerView.setAdapter(adapter);
        floatctionButtonSetUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_EDIT_CODE && resultCode == Activity.RESULT_OK) {
            String id = data.getStringExtra(EditListFragment.KEY_TODO_EDIT_ID);
            if (id != null) { // delete the item
                deleteItem(id);
            } else { // update item
                Todo todoItem = data.getParcelableExtra(EditListFragment.KEY_TODO_EDIT);
                updateItem(todoItem);
            }
        }
    }

    private void updateItem(Todo todoItem) {
        boolean find = false;
        for (int i = 0; i < todoList.size(); ++i) {
            if (todoList.get(i).id.equals(todoItem.id)) {
                todoList.set(i, todoItem);
                find = true;
                break;
            }
        }
        if (!find) {
            todoList.add(todoItem);
        }
        // sort the todoList base on remain date
        Collections.sort(todoList, new Comparator<Todo>() {
            @Override
            public int compare(Todo t1, Todo t2) {
                if (t1.remainDate == null || t2.remainDate == null) {
                    return 1;
                } else {
                    return t1.remainDate.compareTo(t2.remainDate);
                }
            }
        });

        adapter.notifyDataSetChanged();
    }

    private void deleteItem(String id) {
        for (int i = 0; i < todoList.size(); ++i) {
            if (todoList.get(i).id.equals(id)) {
                todoList.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void floatctionButtonSetUp() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditListActivity.class);
                intent.putExtra(EditListFragment.KEY_EDIT_TYPE, EditListFragment.KEY_EDIT_TYPE_ADD);
                startActivityForResult(intent, REQ_EDIT_CODE);
            }
        });
    }

    private void fakeDate() {
        todoList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        Date cur = c.getTime();
        c.add(Calendar.DATE, -1);
        Date past = c.getTime();
        c.add(Calendar.DATE, 2);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 1);
        Date d = c.getTime();

        todoList.add(new Todo("past1", past));
        todoList.get(0).description = "avaduvewqbvnqevqwvqwvqwe";
        todoList.add(new Todo("past2", past));
        todoList.get(1).description = "avaduvewqbvnqevqwvqwvqwe";
        todoList.add(new Todo("current1", cur));
        todoList.get(2).isFinished = true;
        todoList.add(new Todo("current2", cur));
        todoList.get(3).isFinished = true;
        todoList.add(new Todo("tomorrow1", tomorrow));
        todoList.add(new Todo("tomorrow2", tomorrow));
        todoList.add(new Todo("the day after tomorrow1", d));
    }

    public void updateTodo(int position, boolean isFinished) {
        todoList.get(position).isFinished = isFinished;
        adapter.notifyItemChanged(position);
    }
}
