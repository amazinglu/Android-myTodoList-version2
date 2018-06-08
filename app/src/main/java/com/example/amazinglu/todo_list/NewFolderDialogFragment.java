package com.example.amazinglu.todo_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * pass data between fragment and dialog and pass data between activity and dialog is diffenent
 * dialog => fragment
 * use setTargetFragment and intent
 * dialog => activity
 * use customer interface
 * https://www.youtube.com/watch?v=--dJm6z5b0s
 * */

public class NewFolderDialogFragment extends DialogFragment {

    @BindView(R.id.new_folder_name) EditText newFolderTitle;

    public static final String KEY_NEW_FOLDER_TITLE  = "new_folder_title";
    public static final String NEW_FOLDER_DIALOG_TAG  = "new_folder_dialog";

    public NewFolderTitleINputListener newFolderTitleINputListener;

    public static NewFolderDialogFragment newInstance() {
        return new NewFolderDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_folder, null);
        ButterKnife.bind(this, view);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.add_new_folder_title)
                .setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newTitle = newFolderTitle.getText().toString();
                        if (newTitle != null || !newTitle.equals("")) {
                            newFolderTitleINputListener.sentTitleInput(newTitle);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.negative_button_text, null)
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        newFolderTitleINputListener = (NewFolderTitleINputListener) getActivity();
    }

    public interface NewFolderTitleINputListener {
        void sentTitleInput(String newTitle);
    }
}
