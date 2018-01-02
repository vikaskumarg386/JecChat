package com.example.vikas.jecchat;

/**
 * Created by vikas on 30/12/17.
 */

public class Message  {

    public String message,type,from;
    public String timestamp;
    public boolean seen;

    public Message(){}

    public Message(String message, String type, String from, String timestamp, boolean seen) {
        this.message = message;
        this.type = type;
        this.from=from;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void message(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {return from;}

    public void setFrom(String from) {this.from = from;}


}
