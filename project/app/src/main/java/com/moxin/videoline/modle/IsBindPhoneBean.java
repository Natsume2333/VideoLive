package com.moxin.videoline.modle;

public class IsBindPhoneBean {


    /**
     * code : 1
     * msg :
     * status : 0未绑定手机号1已绑定2不强制绑定
     */

    private int code;
    private String msg;
    private String is_force_binding_mobile;
    private String is_binding_mobile;

    public String getIs_binding_mobile() {
        return is_binding_mobile;
    }

    public void setIs_binding_mobile(String is_binding_mobile) {
        this.is_binding_mobile = is_binding_mobile;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIs_force_binding_mobile() {
        return is_force_binding_mobile;
    }

    public void setIs_force_binding_mobile(String is_force_binding_mobile) {
        this.is_force_binding_mobile = is_force_binding_mobile;
    }
}
