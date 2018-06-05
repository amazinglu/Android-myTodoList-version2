package com.example.amazinglu.todo_list.edit_list;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.amazinglu.todo_list.R;
import com.example.amazinglu.todo_list.model.Todo;
import com.example.amazinglu.todo_list.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditListFragment extends Fragment {

    public static final String KEY_EDIT_TYPE = "edit_type";
    public static final String KEY_EDIT_TYPE_ADD = "edit_type_add";
    public static final String KEY_EDIT_TYPE_EDIT = "edit_type_edit";

    public static final String KEY_TODO_EDIT = "todo_edit";
    public static final String KEY_TODO_EDIT_ID = "todo_edit_id";

    @BindView(R.id.todo_edit_title) EditText title;
    @BindView(R.id.todo_edit_detail) EditText detail;
    @BindView(R.id.todo_edit_remain_date) TextView remainDate;
    @BindView(R.id.todo_detail_edit_add_button) FloatingActionButton save;
    @BindView(R.id.todo_edit_remain_time) TextView remainTime;

    private String action;
    private Todo data;

    public static EditListFragment newInstance(Bundle args) {
        EditListFragment editListFragment = new EditListFragment();
        editListFragment.setArguments(args);
        return editListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_list_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        action = getArguments().getString(KEY_EDIT_TYPE);
        data = getArguments().getParcelable(KEY_TODO_EDIT);

        // set up the data
        if (action.equals(KEY_EDIT_TYPE_EDIT)) {
            title.setText(data.title);
            if (data.description != null) {
                detail.setText(data.description);
            }
            if (data.remainDate != null) {
                remainDate.setText(DateUtil.dateToString(data.remainDate));
            } else {
                Calendar c = Calendar.getInstance();
                remainDate.setText(DateUtil.dateToString(c.getTime()));
            }
            save.setImageResource(R.drawable.baseline_save_white_18dp);

        } else {
            data = new Todo();
            Calendar c = Calendar.getInstance();
            remainDate.setText(DateUtil.dateToString(c.getTime()));
            save.setImageResource(R.drawable.baseline_add_white_18dp);
        }

        setUpDetailEditText();
        setUpDateAndTimePicker();
        setUpSaveButton();
    }

    /**
     * option menus (toolbar) part
     * */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_list_menus, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem delete = menu.findItem(R.id.edit_delete);
        if (action.equals(KEY_EDIT_TYPE_EDIT)) {
            delete.setVisible(true);
        } else {
            delete.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.edit_delete:
                deleteAndExit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAndExit() {
        Intent intent = new Intent();
        intent.putExtra(KEY_TODO_EDIT_ID, data.id);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private void setUpDateAndTimePicker() {
        final DatePickerOnSetListener DateSetListener = new DatePickerOnSetListener();
        remainDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c =  Calendar.getInstance();
                Dialog dialog = new DatePickerDialog(getContext(), DateSetListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        final TimePickerOnSetListener TimeSetListener = new TimePickerOnSetListener();
        remainTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                Dialog dialog = new TimePickerDialog(getContext(), TimeSetListener,
                        c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });
    }

    private void setUpSaveButton() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndExit();
            }
        });
    }

    private void setUpDetailEditText() {
        detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                data.description = detail.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void saveAndExit() {
        data.title = title.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(KEY_TODO_EDIT, data);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private Calendar getCalenderFromRemindDate() {
        Calendar c = Calendar.getInstance();
        if (data.remainDate != null) {
            c.setTime(data.remainDate);
        }
        return c;
    }


    class DatePickerOnSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = getCalenderFromRemindDate();
            c.set(year, month, day);
            data.remainDate = c.getTime();
            remainDate.setText(DateUtil.dateToString(data.remainDate));
        }
    }

    class TimePickerOnSetListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            Calendar c = getCalenderFromRemindDate();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            data.remainDate = c.getTime();
            remainTime.setText(DateUtil.TimeToString(data.remainDate));
        }
    }
}
