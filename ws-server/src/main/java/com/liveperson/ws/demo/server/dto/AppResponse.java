package com.liveperson.ws.demo.server.dto;

/**
 * Created by eladw on 11/10/16.
 */
public class AppResponse {

    String msg;

    public AppResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
