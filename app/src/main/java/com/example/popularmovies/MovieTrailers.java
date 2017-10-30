package com.example.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by karthik on 30/10/17.
 */

public class MovieTrailers implements Parcelable {

    private String key;
    private String name;
    private String site;
    private Integer size;
    private String type;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeValue(this.size);
        dest.writeString(this.type);
    }

    public MovieTrailers() {
    }

    protected MovieTrailers(Parcel in) {
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = (Integer) in.readValue(Integer.class.getClassLoader());
        this.type = in.readString();
    }

    public static final Parcelable.Creator<MovieTrailers> CREATOR = new Parcelable.Creator<MovieTrailers>() {
        @Override
        public MovieTrailers createFromParcel(Parcel source) {
            return new MovieTrailers(source);
        }

        @Override
        public MovieTrailers[] newArray(int size) {
            return new MovieTrailers[size];
        }
    };
}
