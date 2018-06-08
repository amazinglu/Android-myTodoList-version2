package com.example.amazinglu.todo_list.base;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.amazinglu.todo_list.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_header) public TextView header;
    @BindView(R.id.item_body_checkbox) public AppCompatCheckBox checkBox;
    @BindView(R.id.item_body_text_title) public TextView title;
    @BindView(R.id.item_body_text_description) public TextView description;

    public View mainView;

    public ListViewHolder(View itemView) {
        super(itemView);
        mainView = itemView;
        ButterKnife.bind(this, itemView);
    }
}
