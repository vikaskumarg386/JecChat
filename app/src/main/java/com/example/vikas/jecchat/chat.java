package com.example.vikas.jecchat;

/**
 * Created by vikas on 31/12/17.
 */

public class chat {

    public String timeStamp;
    public boolean seen;

    public chat(){

    }

    public chat(String timeStamp, boolean seen) {
        this.timeStamp = timeStamp;
        this.seen = seen;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
