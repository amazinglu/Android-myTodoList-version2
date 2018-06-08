package com.example.amazinglu.todo_list.todoList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.todo_list.MainActivity;
import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.edit_list.EditListActivity;
import com.example.amazinglu.todo_list.edit_list.EditListFragment;
import com.example.amazinglu.todo_list.model.Folder;
import com.example.amazinglu.todo_list.model.Todo;
import com.example.amazinglu.todo_list.util.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoListFragment extends Fragment {

    public static final String KEY_TODO_FRAGMENT_ALL_LIST = "todo_all_list";
    public static final String KEY_TODO_FRAGMENT_FOLDER_ID = "todo_all_folder_id";
    public static final int REQ_EDIT_CODE_TODO = 10;


    @BindView(R.id.main_recycler_view) RecyclerView todoRecyclerView;
    @BindView(R.id.float_action_button_add) FloatingActionButton floatingActionButton;

    private List<Todo> allList;
    private List<Todo> todoList;
    private SearchView searchView;
    private String curFolderID;

    public TodoListAdapter getTodoAdapter() {
        return todoAdapter;
    }

    private TodoListAdapter todoAdapter;

    public static TodoListFragment newInstance(List<Todo> allList, String curFolderID) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_TODO_FRAGMENT_ALL_LIST, (ArrayList<? extends Parcelable>) allList);
        args.putString(KEY_TODO_FRAGMENT_FOLDER_ID, curFolderID);
        TodoListFragment todoListFragment = new TodoListFragment();
        todoListFragment.setArguments(args);
        return todoListFragment;
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
        allList = getArguments().getParcelableArrayList(KEY_TODO_FRAGMENT_ALL_LIST);
        curFolderID = getArguments().getString(KEY_TODO_FRAGMENT_FOLDER_ID);
        ButterKnife.bind(this, view);

        // handle raw data
        getTodoList(allList);
        sortList(todoList);

        todoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        todoRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        todoAdapter = new TodoListAdapter(todoList, this);
        todoRecyclerView.setAdapter(todoAdapter);

        floatctionButtonSetUp();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menus, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                todoAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter recycler view when text is changed
                todoAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: 在fragment中实现onBackPressed()

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_EDIT_CODE_TODO && resultCode == Activity.RESULT_OK) {
            String id = data.getStringExtra(EditListFragment.KEY_TODO_EDIT_ID);
            if (id != null) {
                deleteTodoItem(id);
            } else {
                Todo item = data.getParcelableExtra(EditListFragment.KEY_TODO_EDIT);
                updateTodo(item);
            }
        }
    }

    private void sortList(List<Todo> todoList) {
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
    }

    private void getTodoList(List<Todo> allList) {
        if (todoList == null) {
            todoList = new ArrayList<Todo>();
        } else {
            todoList.clear();
        }

        for (Todo element : allList) {
            if (!element.isFinished) {
                todoList.add(element);
            }
        }
    }

    public void updateTodo(int position, boolean isFinished) {
        String id = todoList.get(position).id;
        todoList.remove(position);

        for (int i = 0; i < allList.size(); ++i) {
            if (allList.get(i).id.equals(id)) {
                allList.get(i).isFinished = isFinished;
                break;
            }
        }

//        ModelUtils.save(getContext(), MainActivity.TODO, allList);
        updateAllList();
        /**
         * 只要在todoList有update的地方都要update DateUnfilter
         * */
        todoAdapter.setDataUnFilter(todoList);
        todoAdapter.notifyDataSetChanged();
    }

    public void updateTodo(Todo item) {
        boolean find = false;

        for (int i = 0; i < todoList.size(); ++i) {
            if (todoList.get(i).id.equals(item.id)) {
                todoList.set(i, item);
                find = true;
                break;
            }
        }

        if (!find) {
            todoList.add(item);
        }
        sortList(todoList);
        todoAdapter.setDataUnFilter(todoList);
        todoAdapter.notifyDataSetChanged();

        if (!find) {
            allList.add(item);
        } else {
            for (int i = 0; i < allList.size(); ++i) {
                if (allList.get(i).id.equals(item.id)) {
                    allList.set(i, item);
                    break;
                }
            }
        }

//        ModelUtils.save(getContext(), MainActivity.TODO, allList);
        updateAllList();
    }

    public void updateTodo() {
        getTodoList(allList);
        sortList(todoList);
        todoAdapter.setDataUnFilter(todoList);
        todoAdapter.notifyDataSetChanged();
    }

    /**
     * 一个道理，更新todoList指向的地址的数据
     * */
    public void updateTodo(List<Todo> newData) {
        todoList.clear();
        for (Todo element : newData) {
            todoList.add(element);
        }
    }

    public void updateAllList() {
        Map<String, Folder> folders = ModelUtils.read(getContext(),
                MainActivity.FOLDER_COLLECTION, new TypeToken<Map<String, Folder>>(){});
        List<Todo> newAllList = new ArrayList<Todo>(allList);
        folders.get(curFolderID).allList = newAllList;
        ModelUtils.save(getContext(), MainActivity.FOLDER_COLLECTION, folders);
    }

    private void deleteTodoItem(String id) {
        // update the todoList
        for (int i = 0; i < todoList.size(); ++i) {
            if (todoList.get(i).id.equals(id)) {
                todoList.remove(i);
                break;
            }
        }

        for (int i = 0; i < allList.size(); ++i) {
            if (allList.get(i).id.equals(id)) {
                allList.remove(i);
                break;
            }
        }

//        ModelUtils.save(getContext(), MainActivity.TODO, allList);
        updateAllList();
        todoAdapter.setDataUnFilter(todoList);
        todoAdapter.notifyDataSetChanged();
    }

    private void floatctionButtonSetUp() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditListActivity.class);
                intent.putExtra(EditListFragment.KEY_EDIT_TYPE, EditListFragment.KEY_EDIT_TYPE_ADD);
                startActivityForResult(intent, REQ_EDIT_CODE_TODO);
            }
        });
    }
}
