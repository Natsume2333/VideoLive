package com.moxin.videoline.json;

public class JsonGetMsgPage extends JsonRequestBase{
    private int sum;
    private int un_handle_subscribe_num;

    public int getUn_handle_subscribe_num() {
        return un_handle_subscribe_num;
    }

    public void setUn_handle_subscribe_num(int un_handle_subscribe_num) {
        this.un_handle_subscribe_num = un_handle_subscribe_num;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
