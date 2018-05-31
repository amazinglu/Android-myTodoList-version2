package com.example.amazinglu.todo_list.all_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amazinglu.todo_list.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllListFragment extends Fragment {
    public static final String KEY_LIST_TYPE = "list_type";
    public static final String KEY_LIST_TYPE_TODO = "list_type_todo";
    public static final String KEY_LIST_TYPE_FINISH = "list_type_finish";

    private static final String KEY_PAGE_NUM = "page_num";
    private int pageNum;


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
        int pageNum = getArguments().getInt(KEY_PAGE_NUM);
        ButterKnife.bind(this, view);

    }
}
