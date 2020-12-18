package com.moxin.videoline.modle;

public class DrawalBean {


    /**
     * code : 1
     * msg :
     * data : {"less":"最低提现金额","more":"最高提现金额","cash_day_limit":"每日最大提现次数","invitation_coin":"余额","rules":"提现规则"}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * less : 最低提现金额
         * more : 最高提现金额
         * cash_day_limit : 每日最大提现次数
         * invitation_coin : 余额
         * rules : 提现规则
         */

        private String less;
        private String more;
        private String cash_day_limit;
        private String invitation_coin;
        private String rules;

        public String getLess() {
            return less;
        }

        public void setLess(String less) {
            this.less = less;
        }

        public String getMore() {
            return more;
        }

        public void setMore(String more) {
            this.more = more;
        }

        public String getCash_day_limit() {
            return cash_day_limit;
        }

        public void setCash_day_limit(String cash_day_limit) {
            this.cash_day_limit = cash_day_limit;
        }

        public String getInvitation_coin() {
            return invitation_coin;
        }

        public void setInvitation_coin(String invitation_coin) {
            this.invitation_coin = invitation_coin;
        }

        public String getRules() {
            return rules;
        }

        public void setRules(String rules) {
            this.rules = rules;
        }
    }
}
