package com.example.amazinglu.todo_list.all_list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.edit_list.EditListActivity;
import com.example.amazinglu.todo_list.edit_list.EditListFragment;
import com.example.amazinglu.todo_list.model.Todo;
import com.example.amazinglu.todo_list.util.DateUtil;
import com.example.amazinglu.todo_list.util.UIutils;

import org.joda.time.DateTimeComparator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AllListAdapter extends RecyclerView.Adapter {

    private List<Todo> data;

    private Context context;
    private AllListFragment allListFragment;

    // for test purpose
    private boolean clickCheckBox = false;
    private int count = 0;

    public AllListAdapter(List<Todo> todoList, String listType, AllListFragment fragment) {
        data = todoList;
        allListFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo_list, parent, false);
        return new TodoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TodoListViewHolder viewHolder = (TodoListViewHolder) holder;
        final Todo todoItem = data.get(position);
        viewHolder.title.setText(todoItem.title);

        if (todoItem.description == null) {
            /**
             * 只要这个view有个set visibility的状态，其他所有的状态都要指明visibility，不然adapter会混淆的
             * */
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(todoItem.description);
        }

        // catelogy by date
        if (position == 0 ||
            position > 0 && todoItem.remainDate == null && data.get(position - 1).remainDate != null ||
            position > 0
            && dataCompare(todoItem.remainDate, getCurDay()) >= 0
            && (dataCompare(data.get(position - 1).remainDate, todoItem.remainDate) != 0)) {
            viewHolder.header.setText(getHeaderText(todoItem.remainDate));
            viewHolder.header.setVisibility(View.VISIBLE);
        } else {
            viewHolder.header.setVisibility(View.GONE);
        }

        // listener for the check box
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(todoItem.isFinished);
        UIutils.setTextViewStrikeThrough(viewHolder.title, todoItem.isFinished);
        final int i = position;
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                clickCheckBox = true;
                count++;
                allListFragment.updateTodo(i, !todoItem.isFinished);
            }
        });

        // listener for click the view but not the check box
        viewHolder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditListActivity.class);
                intent.putExtra(EditListFragment.KEY_EDIT_TYPE, EditListFragment.KEY_EDIT_TYPE_EDIT);
                intent.putExtra(EditListFragment.KEY_TODO_EDIT, todoItem);
                allListFragment.startActivityForResult(intent, AllListFragment.REQ_EDIT_CODE);
            }
        });
    }

    private int dataCompare(Date date1, Date date2) {
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        return dateTimeComparator.compare(date1, date2);
    }

    private Date getCurDay() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    private String getHeaderText(Date remainDate) {
        Calendar c = Calendar.getInstance();
        Date cur = c.getTime();
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        int cmpCur = dateTimeComparator.compare(remainDate, cur);
        int cmpT = dateTimeComparator.compare(remainDate, tomorrow);

        if (remainDate == null) {
            return getContext().getResources().getString(R.string.no_due_day);
        } else if (cmpCur < 0) {
            return getContext().getResources().getString(R.string.past_day);
        } else if (cmpCur == 0) {
            return getContext().getResources().getString(R.string.current_day);
        } else if (cmpT == 0) {
            return getContext().getResources().getString(R.string.tomorrow);
        } else {
            return DateUtil.dateToString(remainDate);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Context getContext() {
        return context;
    }
}
