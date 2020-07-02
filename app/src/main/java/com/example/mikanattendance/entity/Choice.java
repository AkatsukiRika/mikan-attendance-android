package com.example.mikanattendance.entity;

import android.graphics.Bitmap;

public class Choice {
    private String name;
    private Bitmap thumbnail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "name='" + name + '\'' +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
