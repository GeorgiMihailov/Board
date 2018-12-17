package com.george.board.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MyAccountActivityDetails implements  Parcelable {
     int Id;
     String Icon;
     String Name;
     String Status;
     String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public MyAccountActivityDetails(Parcel in) {
        Id = in.readInt();
        Icon = in.readString();
        Name = in.readString();
        Status = in.readString();
        date = in.readString();
    }

    public static final Creator<MyAccountActivityDetails> CREATOR = new Creator<MyAccountActivityDetails>() {
        @Override
        public MyAccountActivityDetails createFromParcel(Parcel in) {
            return new MyAccountActivityDetails(in);
        }

        @Override
        public MyAccountActivityDetails[] newArray(int size) {
            return new MyAccountActivityDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Icon);
        dest.writeString(Name);
        dest.writeString(Status);
        dest.writeString(date);
    }
    public MyAccountActivityDetails(){
        super();
    }
}
