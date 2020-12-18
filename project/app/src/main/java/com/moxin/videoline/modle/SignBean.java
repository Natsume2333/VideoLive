package com.moxin.videoline.modle;

public class SignBean {


    /**
     * code : 1
     * msg :
     * data : []
     * income : 0
     * list : [{"id":4,"coin":3000,"sort":1,"addtime":1547448656,"status":1,"money":"300"},{"id":3,"coin":550,"sort":55,"addtime":1547434544,"status":1,"money":"55"},{"id":1,"coin":230,"sort":100,"addtime":1547434489,"status":1,"money":"23"}]
     * is_bind_pay : 0
     */

    private int code;
    private String msg;
    private int is_sign_in;

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

    public int getIs_sign_in() {
        return is_sign_in;
    }

    public void setIs_sign_in(int is_sign_in) {
        this.is_sign_in = is_sign_in;
    }
}
