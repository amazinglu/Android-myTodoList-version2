package com.example.amazinglu.todo_list.edit_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.amazinglu.todo_list.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        ButterKnife.bind(this);

        /**
         * enable the home button to show
         * ensble the sandwich button to show
         * */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.edit_fragment_container, EditListFragment.newInstance(getIntent().getExtras()))
                    .commit();
        }
    }
}
