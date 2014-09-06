package com.example.caffetier.app.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aleksandar on 27-Jul-14.
 */
public class Cafe implements Parcelable {

    public String naziv;
    public String adresa;
    public String url;
    public String logoUrl;
    public int logoID;
    public Area area;
    public double lat;
    public double lng;

    public Cafe() {
    }

    public Cafe(String naziv, String adresa, Area area, String url, String logoUrl, int logoID, double lat, double lng) {
        super();
        this.naziv = naziv;
        this.adresa = adresa;
        this.area = area;
        this.url = url;
        this.logoUrl = logoUrl;
        this.logoID = logoID;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naziv);
        dest.writeString(adresa);
        dest.writeValue(area);
        dest.writeString(url);
        dest.writeString(logoUrl);
        dest.writeInt(logoID);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public static final Parcelable.Creator<Cafe> CREATOR = new Creator<Cafe>() {
        @Override
        public Cafe createFromParcel(Parcel source) {
            return new Cafe(source);
        }

        @Override
        public Cafe[] newArray(int size) {
            return new Cafe[size];
        }
    };

    private Cafe(Parcel in){
        naziv = in.readString();
        adresa = in.readString();
        area = Area.CREATOR.createFromParcel(in);
        url = in.readString();
        logoUrl = in.readString();
        logoID = in.readInt();
        lat = in.readDouble();
        lng = in.readDouble();
    }
}
