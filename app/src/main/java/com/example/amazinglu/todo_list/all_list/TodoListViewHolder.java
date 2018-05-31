package com.example.amazinglu.todo_list.all_list;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.amazinglu.todo_list.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_header) TextView header;
    @BindView(R.id.item_body_checkbox) AppCompatCheckBox checkBox;
    @BindView(R.id.item_body_text_title) TextView title;
    @BindView(R.id.item_body_text_description) TextView description;

    public TodoListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
