package com.moxin.videoline.json;

import com.moxin.videoline.modle.UserModel;

/**
 * Created by weipeng on 2018/2/27.
 */

public class JsonRequestGetUserBaseInfo extends JsonRequestBase {
    private UserModel user_info;

    public UserModel getUser_info() {
        return user_info;
    }

    public void setUser_info(UserModel user_info) {
        this.user_info = user_info;
    }
}
