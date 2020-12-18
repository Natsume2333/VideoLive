package com.moxin.videoline.modle;

import java.util.List;

public class InviteBean {


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
    private int income;
    private int is_bind_pay;
    private List<?> data;
    private List<ListBean> list;

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

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getIs_bind_pay() {
        return is_bind_pay;
    }

    public void setIs_bind_pay(int is_bind_pay) {
        this.is_bind_pay = is_bind_pay;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 4
         * coin : 3000
         * sort : 1
         * addtime : 1547448656
         * status : 1
         * money : 300
         */

        private int id;
        private int coin;
        private int sort;
        private int addtime;
        private int status;
        private String money;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getAddtime() {
            return addtime;
        }

        public void setAddtime(int addtime) {
            this.addtime = addtime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
