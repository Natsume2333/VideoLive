package com.moxin.videoline.modle;

import java.io.Serializable;

public class CustomMsgModel implements Serializable {
    private String msg;
    private int id;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
