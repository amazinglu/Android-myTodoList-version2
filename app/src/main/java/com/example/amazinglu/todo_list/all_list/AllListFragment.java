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

import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.edit_list.EditListActivity;
import com.example.amazinglu.todo_list.edit_list.EditListFragment;
import com.example.amazinglu.todo_list.model.Todo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private AllListAdapter adapter;
    private List<Todo> allList;
    private String listType;

    public static AllListFragment newInstance(String listType, List<Todo> allList) {
        Bundle args = new Bundle();
        args.putString(KEY_LIST_TYPE, listType);
        args.putParcelableArrayList(KEY_LIST_DATA, (ArrayList<? extends Parcelable>) allList);
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
        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listType = getArguments().getString(KEY_LIST_TYPE);
        allList = getArguments().getParcelableArrayList(KEY_LIST_DATA);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        /**
         * recycler view 中每个item都有下划线隔开，可以自己定义一个Decoration， 也可以用DividerItemDecoration
         * */
//        recyclerView.addItemDecoration(new com.example.amazinglu.todo_list.all_list.DividerItemDecoration(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new AllListAdapter(allList, listType, this);
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
        for (int i = 0; i < allList.size(); ++i) {
            if (allList.get(i).id.equals(todoItem.id)) {
                allList.set(i, todoItem);
                find = true;
                break;
            }
        }
        if (!find) {
            allList.add(todoItem);
        }
        // sort the allList base on remain date
        Collections.sort(allList, new Comparator<Todo>() {
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
        for (int i = 0; i < allList.size(); ++i) {
            if (allList.get(i).id.equals(id)) {
                allList.remove(i);
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

    public void updateTodo(int position, boolean isFinished) {
        allList.get(position).isFinished = isFinished;
        adapter.notifyItemChanged(position);
    }
}
