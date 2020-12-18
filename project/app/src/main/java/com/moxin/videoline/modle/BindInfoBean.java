package com.moxin.videoline.modle;

public class BindInfoBean {

    /**
     * code : 1
     * msg :
     * data : {"id":2,"uid":100193,"pay":"啦啦操","wx":"回家咯","name":"看","addtime":1562151034}
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
         * id : 2
         * uid : 100193
         * pay : 啦啦操
         * wx : 回家咯
         * name : 看
         * addtime : 1562151034
         */

        private int id;
        private int uid;
        private String pay;
        private String wx;
        private String name;
        private int addtime;
        private String type;
        private String bank_account;
        private String bank_cardno;
        private String bank_name;
        private String bank_addr;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }

        public String getWx() {
            return wx;
        }

        public void setWx(String wx) {
            this.wx = wx;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAddtime() {
            return addtime;
        }

        public void setAddtime(int addtime) {
            this.addtime = addtime;
        }

        public String getBank_account() {
            return bank_account;
        }

        public void setBank_account(String bank_account) {
            this.bank_account = bank_account;
        }

        public String getBank_cardno() {
            return bank_cardno;
        }

        public void setBank_cardno(String bank_cardno) {
            this.bank_cardno = bank_cardno;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getBank_addr() {
            return bank_addr;
        }

        public void setBank_addr(String bank_addr) {
            this.bank_addr = bank_addr;
        }
    }
}
