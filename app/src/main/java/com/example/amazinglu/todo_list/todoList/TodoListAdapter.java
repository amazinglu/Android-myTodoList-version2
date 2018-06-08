package com.example.amazinglu.todo_list.todoList;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.base.ListViewHolder;
import com.example.amazinglu.todo_list.edit_list.EditListActivity;
import com.example.amazinglu.todo_list.edit_list.EditListFragment;
import com.example.amazinglu.todo_list.model.Todo;
import com.example.amazinglu.todo_list.util.DateUtil;
import com.example.amazinglu.todo_list.util.UIutils;

import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter implements Filterable {

    private List<Todo> data;
    private List<Todo> dataUnFilter;

    private Context context;
    private TodoListFragment totoListFragment;

    public TodoListAdapter(List<Todo> todoList, TodoListFragment fragment) {
        data = todoList;
        dataUnFilter = new ArrayList<>(data);
        totoListFragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ListViewHolder viewHolder = (ListViewHolder) holder;
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
                totoListFragment.updateTodo(i, !todoItem.isFinished);
            }
        });

        // listener for click the view but not the check box
        viewHolder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditListActivity.class);
                intent.putExtra(EditListFragment.KEY_EDIT_TYPE, EditListFragment.KEY_EDIT_TYPE_EDIT);
                intent.putExtra(EditListFragment.KEY_TODO_EDIT, todoItem);
                totoListFragment.startActivityForResult(intent, TodoListFragment.REQ_EDIT_CODE_TODO);
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

    /**
     * use for the search view to filter the content
     * 注意的问题：
     * 加入了这个filter以后，会对data进行修改
     * 但是注意不能改变data的指向，如果data指向其他的object，整个程序就乱了
     * 只能更新data里面的数据
     *
     * 同时第二个问题就因为filter，我们需要用另外一个list来记录没有filter之前的数据 => dataUnFilter
     * 而这个数组的数据在data update的时候也要同步更新，而且dataUnFilter应该是另外一个全新的list，而不是指向data
     *
     * 如何实现search view
     * https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/
     * */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    totoListFragment.updateTodo(dataUnFilter);
                } else {
                    List<Todo> filterList = new ArrayList<>();
                    // define the filter logic
                    for (Todo element : dataUnFilter) {
                        if (element.title.toLowerCase().contains(charString.toLowerCase())) {
                            filterList.add(element);
                        }
                    }
                    totoListFragment.updateTodo(filterList);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = data;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                // update the result to adapter
                data = (List<Todo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setDataUnFilter(List<Todo> dataUnFilter) {
        this.dataUnFilter = new ArrayList<Todo>(dataUnFilter);
    }
}
