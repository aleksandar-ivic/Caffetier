package com.example.caffetier.app.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aleksandar on 27-Jul-14.
 */
public class Kafic implements Parcelable {

    public String naziv;
    public String adresa;
    public String url;
    public String logoUrl;
    public int logoID;
    public Opstina opstina;
    public double lat;
    public double lng;

    public Kafic() {
    }

    public Kafic(String naziv, String adresa, Opstina opstina, String url, String logoUrl, int logoID, double lat, double lng) {
        super();
        this.naziv = naziv;
        this.adresa = adresa;
        this.opstina = opstina;
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
        dest.writeValue(opstina);
        dest.writeString(url);
        dest.writeString(logoUrl);
        dest.writeInt(logoID);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public static final Parcelable.Creator<Kafic> CREATOR = new Creator<Kafic>() {
        @Override
        public Kafic createFromParcel(Parcel source) {
            return new Kafic(source);
        }

        @Override
        public Kafic[] newArray(int size) {
            return new Kafic[size];
        }
    };

    private Kafic(Parcel in){
        naziv = in.readString();
        adresa = in.readString();
        opstina = Opstina.CREATOR.createFromParcel(in);
        url = in.readString();
        logoUrl = in.readString();
        logoID = in.readInt();
        lat = in.readDouble();
        lng = in.readDouble();
    }
}
