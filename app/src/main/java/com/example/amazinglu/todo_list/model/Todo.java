package com.example.amazinglu.todo_list.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.amazinglu.todo_list.util.DateUtil;

import java.util.Date;
import java.util.UUID;

public class Todo implements Parcelable{
    public String id;
    public String title;
    public String description;
    public Date remainDate;
    public boolean isFinished;

    public Todo() {
        id = UUID.randomUUID().toString();
        isFinished = false;
    }

    protected Todo(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        remainDate = DateUtil.stringToDate(in.readString());
        isFinished = in.readByte() != 0;
    }

    public static final Creator<Todo> CREATOR = new Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(DateUtil.dateToString(remainDate));
        parcel.writeByte((byte) (isFinished ? 1 : 0));
    }
}
