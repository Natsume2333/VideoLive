package com.moxin.videoline.modle;

import java.util.List;

public class GuardItemBean {

    /**
     * code : 1
     * msg :
     * data : {"time":"守护到期时间；空不是本主播的守护","list":[{"id":"购买守护规则id","title":"购买名称","day":"时间（天）","coin":"消费的金币"}]}
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
         * time : 守护到期时间；空不是本主播的守护
         * list : [{"id":"购买守护规则id","title":"购买名称","day":"时间（天）","coin":"消费的金币"}]
         */

        private String time;
        private String user_nickname;
        private List<ListBean> list;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public static class ListBean {
            /**
             * id : 购买守护规则id
             * title : 购买名称
             * day : 时间（天）
             * coin : 消费的金币
             */

            private String id;
            private String title;
            private String day;
            private String coin;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDay() {
                return day;
            }

            public void setDay(String day) {
                this.day = day;
            }

            public String getCoin() {
                return coin;
            }

            public void setCoin(String coin) {
                this.coin = coin;
            }
        }
    }
}
