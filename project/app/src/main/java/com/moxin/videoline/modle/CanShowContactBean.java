package com.moxin.videoline.modle;

public class CanShowContactBean {

    /**
     * code : 1
     * msg :
     * price : 购买价格如果有值就请求购买接口
     * number : 显示的号码
     */

    private int code;
    private String msg;
    private String price;
    private String number;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
