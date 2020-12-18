package com.moxin.videoline.modle;

import java.util.List;

public class RechargeVipBean {


    /**
     * code : 1
     * msg :
     * vip_time : 未开通
     * pay_list : [{"id":1,"pay_name":"支付宝","icon":"http://videoline.qiniu.bugukj.com/'admin/20181211/6c34693891f367c00c18b3ba96158670.png'"},{"id":10,"pay_name":"微信支付","icon":"http://videoline.qiniu.bugukj.com/'admin/20181211/ab5adc6adb52471cfd309f05a98f3b46.png'"}]
     * vip_rule : [{"id":1,"name":"一个月","money":"30","create_time":1548665928,"day_count":30,"day_money":"¥1"},{"id":2,"name":"半年","money":"100","create_time":1534441825,"day_count":180,"day_money":"¥0.56"},{"id":3,"name":"一年","money":"300","create_time":1534441836,"day_count":365,"day_money":"¥0.82"}]
     */

    private int code;
    private String msg;
    private String vip_time;
    private List<PayListBean> pay_list;
    private List<VipRuleBean> vip_rule;
    private List<VipDetailsModel> detail_list;

    public List<VipDetailsModel> getDetail_list() {
        return detail_list;
    }

    public void setDetail_list(List<VipDetailsModel> detail_list) {
        this.detail_list = detail_list;
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

    public String getVip_time() {
        return vip_time;
    }

    public void setVip_time(String vip_time) {
        this.vip_time = vip_time;
    }

    public List<PayListBean> getPay_list() {
        return pay_list;
    }

    public void setPay_list(List<PayListBean> pay_list) {
        this.pay_list = pay_list;
    }

    public List<VipRuleBean> getVip_rule() {
        return vip_rule;
    }

    public void setVip_rule(List<VipRuleBean> vip_rule) {
        this.vip_rule = vip_rule;
    }

    public static class PayListBean {
        /**
         * id : 1
         * pay_name : 支付宝
         * icon : http://videoline.qiniu.bugukj.com/'admin/20181211/6c34693891f367c00c18b3ba96158670.png'
         */

        private int id;
        private String pay_name;
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPay_name() {
            return pay_name;
        }

        public void setPay_name(String pay_name) {
            this.pay_name = pay_name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class VipRuleBean {
        /**
         * id : 1
         * name : 一个月
         * money : 30
         * create_time : 1548665928
         * day_count : 30
         * day_money : ¥1
         */

        private int id;
        private String name;
        private String money;
        private int create_time;
        private int day_count;
        private String day_money;
        private String icon;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public int getDay_count() {
            return day_count;
        }

        public void setDay_count(int day_count) {
            this.day_count = day_count;
        }

        public String getDay_money() {
            return day_money;
        }

        public void setDay_money(String day_money) {
            this.day_money = day_money;
        }
    }
}
