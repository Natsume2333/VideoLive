package com.moxin.videoline.json;

import com.moxin.videoline.modle.UserModel;

public class JsonRequestPerfectRegisterInfo extends JsonRequestBase{

    private UserModel data;

    public UserModel getData() {
        return data;
    }

    public void setData(UserModel data) {
        this.data = data;
    }
}
