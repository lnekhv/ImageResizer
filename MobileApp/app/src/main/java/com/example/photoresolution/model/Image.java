package com.example.photoresolution.model;

public class Image {
    private String name;
    private String timestamp;
    private String resolution;
    private String img;

    public Image() {

    }

    public Image(String name, String timestamp, String resolution, String img) {
        this.name = name;
        this.timestamp = timestamp;
        this.resolution = resolution;
        this.img = img;
    }

    public Image(String name, String timestamp, String resolution) {
        this.name = name;
        this.timestamp = timestamp;
        this.resolution = resolution;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
