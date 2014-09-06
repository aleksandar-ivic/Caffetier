package com.example.caffetier.app.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aleksandar on 29-Jul-14.
 */
public class Area implements Parcelable{

    public int id;
    public String naziv;

    public Area() {
    }

    public Area(int id, String naziv) {
        super();
        this.id = id;
        this.naziv = naziv;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(naziv);
    }

    public static final Parcelable.Creator<Area> CREATOR = new Creator<Area>() {
        @Override
        public Area createFromParcel(Parcel source) {
            return new Area(source);
        }

        @Override
        public Area[] newArray(int size) {
            return new Area[size];
        }
    };

    private Area(Parcel in){
        id = in.readInt();
        naziv = in.readString();
    }

    @Override
    public String toString() {
        return naziv;
    }
}
