package com.example.amazinglu.todo_list.finish_list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.base.ListViewHolder;
import com.example.amazinglu.todo_list.edit_list.EditListActivity;
import com.example.amazinglu.todo_list.edit_list.EditListFragment;
import com.example.amazinglu.todo_list.model.Todo;

import java.util.List;

public class FinishedListAdapter extends RecyclerView.Adapter {

    private Context context;
    private FinishListFragment finishListFragment;
    private  List<Todo> finishedList;

    public FinishedListAdapter(List<Todo> list, FinishListFragment fragment) {
        this.finishedList = list;
        finishListFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_todo_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Todo todoItem = finishedList.get(position);
        ListViewHolder viewHolder = (ListViewHolder) holder;
        viewHolder.title.setText(todoItem.title);
        viewHolder.description.setVisibility(View.GONE);
        viewHolder.header.setVisibility(View.GONE);

        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(todoItem.isFinished);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                finishListFragment.updateTodo(position, !todoItem.isFinished);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditListActivity.class);
                intent.putExtra(EditListFragment.KEY_EDIT_TYPE, EditListFragment.KEY_EDIT_TYPE_EDIT);
                intent.putExtra(EditListFragment.KEY_TODO_EDIT, todoItem);
                finishListFragment.startActivityForResult(intent, FinishListFragment.REQ_EDIT_CODE_FINISH);
            }
        });
    }

    @Override
    public int getItemCount() {
        return finishedList.size();
    }

    public Context getContext() {
        return context;
    }
}
