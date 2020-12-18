package com.moxin.videoline.modle;

import java.util.List;

public class IncomeBean {

    /**
     * code : 1
     * msg :
     * count : 接单总人数
     * data : [{"coin":"消费的金币","profit":"收益","content":"通话计时消费","create_time":"时间","table_id":"消费类型id","type":"分类","user_id":"用户id","to_user_id":"用户id","user_nickname":"昵称"}]
     */

    private int code;
    private String msg;
    private String count;
    private String statistical="";
    private List<DataBean> data;

    public String getStatistical() {
        return statistical;
    }

    public void setStatistical(String statistical) {
        this.statistical = statistical;
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
         * coin : 消费的金币
         * profit : 收益
         * content : 通话计时消费
         * create_time : 时间
         * table_id : 消费类型id
         * type : 分类
         * user_id : 用户id
         * to_user_id : 用户id
         * user_nickname : 昵称
         */

        private String coin;
        private String profit;
        private String content;
        private String create_time;
        private String table_id;
        private String type;
        private String user_id;
        private String to_user_id;
        private String user_nickname;


        private String income;
        private String money;
        private String status;

        private String addtime;



        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getTable_id() {
            return table_id;
        }

        public void setTable_id(String table_id) {
            this.table_id = table_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(String to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }
    }
}
