package com.moxin.videoline.modle;

import java.util.List;

public class InviteMenBean {

    /**
     * code : 1
     * msg :
     * data : [{"user_nickname":"用户昵称","id":"用户id","registered":"注册奖励","money":"充值或主播收益总数"}]
     * money_sum : 充值或主播收益总数
     * registered_sum : 注册奖励总数
     * count : 总收益
     */

    private int code;
    private String msg;
    private String money_sum;
    private String registered_sum;
    private String count;
    private List<DataBean> data;

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

    public String getMoney_sum() {
        return money_sum;
    }

    public void setMoney_sum(String money_sum) {
        this.money_sum = money_sum;
    }

    public String getRegistered_sum() {
        return registered_sum;
    }

    public void setRegistered_sum(String registered_sum) {
        this.registered_sum = registered_sum;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * user_nickname : 用户昵称
         * id : 用户id
         * registered : 注册奖励
         * money : 充值或主播收益总数
         */

        private String user_nickname;
        private String id;
        private String registered;
        private String money;

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRegistered() {
            return registered;
        }

        public void setRegistered(String registered) {
            this.registered = registered;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
