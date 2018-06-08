package com.example.amazinglu.todo_list.finish_list;

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
import android.widget.Toast;

import com.example.amazinglu.todo_list.MainActivity;
import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.edit_list.EditListFragment;
import com.example.amazinglu.todo_list.model.Folder;
import com.example.amazinglu.todo_list.model.Todo;
import com.example.amazinglu.todo_list.util.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FinishListFragment extends Fragment {

    private static final String FINFISH_FREGMENT_KEY_ALL_LIST = "all_list";
    private static final String FINFISH_FREGMENT_KEY_FOLDER_ID = "folder_id";
    public static final int REQ_EDIT_CODE_FINISH = 11;

    private List<Todo> allList;
    private List<Todo> finishedList;
    private String curFolderID;
    private FinishedListAdapter adapter;

    // test purpose
    int count = 0;

    @BindView(R.id.main_recycler_view) RecyclerView finishedRecyclerView;
    @BindView(R.id.float_action_button_add) FloatingActionButton floatingActionButton;

    public static FinishListFragment newInstance(List<Todo> allList, String curFolderID) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(FINFISH_FREGMENT_KEY_ALL_LIST, (ArrayList<? extends Parcelable>) allList);
        args.putString(FINFISH_FREGMENT_KEY_FOLDER_ID, curFolderID);
        FinishListFragment finishListFragment = new FinishListFragment();
        finishListFragment.setArguments(args);
        return finishListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getActivity(), "go back to finish fragment again", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_list_fragment, container, false);

        count++;
        if (count == 2) {
            System.out.println("here");
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allList = getArguments().getParcelableArrayList(FINFISH_FREGMENT_KEY_ALL_LIST);
        curFolderID = getArguments().getString(FINFISH_FREGMENT_KEY_FOLDER_ID);
        ButterKnife.bind(this, view);

        getFinishedList();
        floatingActionButton.setVisibility(View.GONE);

        finishedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        finishedRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new FinishedListAdapter(finishedList, this);
        finishedRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_EDIT_CODE_FINISH && resultCode == Activity.RESULT_OK) {
            String id = data.getStringExtra(EditListFragment.KEY_TODO_EDIT_ID);
            if (id != null) {
                deleteTodo(id);
            } else {
                Todo item = data.getParcelableExtra(EditListFragment.KEY_TODO_EDIT);
                updateTodo(item);
            }
        }
    }

    private void deleteTodo(String id) {
        for (int i = 0; i < finishedList.size(); ++i) {
            if (finishedList.get(i).id.equals(id)) {
                finishedList.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }

        for (int i = 0; i < allList.size(); ++i) {
            if (allList.get(i).id.equals(id)) {
                allList.remove(i);
                break;
            }
        }

//        ModelUtils.save(getContext(), MainActivity.TODO, allLIst);
        updateAllList();
    }

    public void getFinishedList() {
        if (finishedList == null) {
            finishedList = new ArrayList<Todo>();
        } else {
            finishedList.clear();
        }

        for (Todo element : allList) {
            if (element.isFinished) {
                finishedList.add(element);
            }
        }
    }

    public void updateTodo(int position, boolean isFinished) {
        String id = finishedList.get(position).id;
        finishedList.remove(position);
        for (int i = 0; i < allList.size(); ++i) {
            if (allList.get(i).id.equals(id)) {
                allList.get(i).isFinished = isFinished;
                break;
            }
        }

//        ModelUtils.save(getContext(), MainActivity.TODO, allList);
        updateAllList();
        adapter.notifyDataSetChanged();
    }

    private void updateTodo(Todo item) {
        boolean find = false;
        for (int i = 0; i < finishedList.size(); ++i) {
            if (finishedList.get(i).id.equals(item.id)) {
                find = true;
                finishedList.set(i, item);
                break;
            }
        }
        if (!find) {
            finishedList.add(item);
        }
        adapter.notifyDataSetChanged();

        if (find) {
            for (int i = 0; i < allList.size(); ++i) {
                if (allList.get(i).id.equals(item.id)) {
                    allList.set(i, item);
                    break;
                }
            }
        } else {
            allList.add(item);
        }

//        ModelUtils.save(getContext(), MainActivity.TODO, allList);
        updateAllList();
    }

    public void updateTodo() {
        getFinishedList();
        adapter.notifyDataSetChanged();
    }

    public void updateAllList() {
        Map<String, Folder> folders = ModelUtils.read(getContext(),
                MainActivity.FOLDER_COLLECTION, new TypeToken<Map<String, Folder>>(){});
        List<Todo> newAllList = new ArrayList<Todo>(allList);
        folders.get(curFolderID).allList = newAllList;
        ModelUtils.save(getContext(), MainActivity.FOLDER_COLLECTION, folders);
    }
}
