package com.example.photoresolution.model;

public class ImageDatabase {
    private String ImageId;
    private String name;
    private String timestamp;
    private String resolution;
    private String img;
    private String imgUrl;

    public ImageDatabase(String name, String timestamp, String resolution, String img, String imgUrl) {
        this.name = name;
        this.timestamp = timestamp;
        this.resolution = resolution;
        this.img = img;
        this.imgUrl = imgUrl;
    }

    public ImageDatabase() {

    }

    public ImageDatabase(String name, String timestamp, String resolution, String img) {
        this.name = name;
        this.timestamp = timestamp;
        this.resolution = resolution;
        this.img = img;
    }

    public ImageDatabase(String name, String timestamp, String resolution) {
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImageId() {
        return ImageId;
    }

    public void setImageId(String imageId) {
        this.ImageId = imageId;
    }
}
