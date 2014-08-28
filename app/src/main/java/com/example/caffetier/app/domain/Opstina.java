package com.example.caffetier.app.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aleksandar on 29-Jul-14.
 */
public class Opstina implements Parcelable{

    public int id;
    public String naziv;

    public Opstina() {
    }

    public Opstina(int id, String naziv) {
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

    public static final Parcelable.Creator<Opstina> CREATOR = new Creator<Opstina>() {
        @Override
        public Opstina createFromParcel(Parcel source) {
            return new Opstina(source);
        }

        @Override
        public Opstina[] newArray(int size) {
            return new Opstina[size];
        }
    };

    private Opstina(Parcel in){
        id = in.readInt();
        naziv = in.readString();
    }

    @Override
    public String toString() {
        return naziv;
    }
}
