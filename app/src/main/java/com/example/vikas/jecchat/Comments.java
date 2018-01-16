package com.example.vikas.jecchat;

/**
 * Created by vikas on 8/1/18.
 */

public class Comments {

    private String comment_Text;
    private String time;
    private String from;
    private String likes;
    private String type;
    private String cPush_key;
    private String push_key;

    public String getPush_key() {
        return push_key;
    }

    public void setPush_key(String push_key) {
        this.push_key = push_key;
    }

    public Comments(){}

    public Comments(String comment_Text, String time, String from, String likes, String type, String cPush_key, String push_key) {
        this.comment_Text = comment_Text;
        this.time = time;
        this.from = from;
        this.likes = likes;
        this.type = type;
        this.cPush_key = cPush_key;
        this.push_key=push_key;
    }

    public String getComment_Text() {
        return comment_Text;
    }

    public void setComment_Text(String comment_Text) {
        this.comment_Text = comment_Text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getcPush_key() {
        return cPush_key;
    }

    public void setcPush_key(String cPush_key) {
        this.cPush_key = cPush_key;
    }
}
