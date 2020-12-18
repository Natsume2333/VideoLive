package com.moxin.videoline.modle;

import android.text.TextUtils;

import java.util.List;

public class GuardRankBean {

    /**
     * code : 1
     * msg :
     * data : {"user":{"uid":"守护主播的本用户id","gift_count":"贡献数"},"list":[{"uid":"守护人id","user_nickname":"昵称","avatar":"头像","gift_count":"贡献数","level":"等级"}]}
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
         * user : {"uid":"守护主播的本用户id","gift_count":"贡献数"}
         * list : [{"uid":"守护人id","user_nickname":"昵称","avatar":"头像","gift_count":"贡献数","level":"等级"}]
         */

        private UserBean user;
        private List<ListBean> list;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class UserBean {
            /**
             * uid : 守护主播的本用户id
             * gift_count : 贡献数
             */

            private String uid;
            private String gift_count;
            private int is_open;

            public int getIs_open() {
                return is_open;
            }

            public void setIs_open(int is_open) {
                this.is_open = is_open;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getGift_count() {
                return gift_count;
            }

            public void setGift_count(String gift_count) {
                this.gift_count = gift_count;
            }
        }

        public static class ListBean {
            /**
             * uid : 守护人id
             * user_nickname : 昵称
             * avatar : 头像
             * gift_count : 贡献数
             * level : 等级
             */

            private String uid;
            private String user_nickname;
            private String avatar;
            private String gift_count;
            private String level;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getUser_nickname() {
                return user_nickname;
            }

            public void setUser_nickname(String user_nickname) {
                this.user_nickname = user_nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getGift_count() {
                if(TextUtils.isEmpty(gift_count))gift_count="0";
                return gift_count;
            }

            public void setGift_count(String gift_count) {
                this.gift_count = gift_count;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }
        }
    }
}
