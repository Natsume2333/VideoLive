package com.moxin.videoline.json;

import com.moxin.videoline.modle.UserRatioModel;

public class JsonGetUserRatio extends JsonRequestBase {

    private UserRatioModel data;

    public UserRatioModel getData() {
        return data;
    }

    public void setData(UserRatioModel data) {
        this.data = data;
    }
}
