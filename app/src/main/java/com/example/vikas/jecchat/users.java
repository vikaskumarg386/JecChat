package com.example.vikas.jecchat;

/**
 * Created by vikas on 19/12/17.
 */

public class users {

    public String image;
    public String name;
    public String status;
    public String thumbImage;

    public users() {
    }

    public users(String image, String name, String status,String thumbImage) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.thumbImage=thumbImage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbImage(){ return thumbImage; }

    public void setThumbImage(){ this.thumbImage = thumbImage; }
}
