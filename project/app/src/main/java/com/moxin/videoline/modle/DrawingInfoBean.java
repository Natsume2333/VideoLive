package com.moxin.videoline.modle;

import java.util.List;

public class DrawingInfoBean {

    /**
     * code : 1
     * msg :
     * data : [{"addtime":"提现时间","coin":"提现金额","status":"状态：0审核中1以提现2拒绝提现","type":"1支付宝2微信"}]
     */

    private int code;
    private String msg;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * addtime : 提现时间
         * coin : 提现金额
         * status : 状态：0审核中1以提现2拒绝提现
         * type : 1支付宝2微信
         */

        private String addtime;
        private String coin;
        private String status;
        private String type;

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
