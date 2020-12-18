package com.moxin.videoline.modle;

import java.util.ArrayList;
import java.util.List;

public class RewardDetailBean {

    /**
     * code : 1
     * msg :
     * money : 总收益
     * date : 时间
     * data : [{"user_nickname":"昵称","create_time":"时间","money":"奖励金额","type":"0注册 1主播收益 2充值奖励"}]
     */

    private int code;
    private String msg;
    private String money;
    private String date;
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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<DataBean> getData() {
        if(data==null)data=new ArrayList<>();
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * user_nickname : 昵称
         * create_time : 时间
         * money : 奖励金额
         * type : 0注册 1主播收益 2充值奖励
         */

        private String user_nickname;
        private String create_time;
        private String money;
        private String type;

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getType() {
            //:'0注册 1主播收益 2充值奖励'
            switch (type){
                case "0":
                    return "注册";
                case "1":
                    return "主播收益";
                case "2":
                    return "充值奖励";
            }
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
