package edu.ktu.myfirstapplication;

import java.io.Serializable;

public class ListItem implements Serializable{
    private String title;
    private int ImageId;
    private String description;

    public ListItem() {}

    public ListItem(String title, int imageId, String description) {
        this.title = title;
        ImageId = imageId;
        this.description = description;
    }

    public ListItem(String title, String description) {
        this.title = title;
        ImageId = R.drawable.ic_android_black_48dp;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
