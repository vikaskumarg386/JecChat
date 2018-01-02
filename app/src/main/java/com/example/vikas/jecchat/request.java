package com.example.vikas.jecchat;

/**
 * Created by vikas on 1/1/18.
 */

public class request {
    String request_type;

    public request(){}

    public request(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
