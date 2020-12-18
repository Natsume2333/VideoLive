package com.moxin.videoline.modle;

import java.util.List;

public class PairBean {

    /**
     * num : 2
     * list : [{"id":"16","uid":"1251","touid":"1286","type":"1","addtime":"1550202418","edittime":"1550202418","backstatus":"0","status":"1","user_nicename":"顾美丽","avatar":"/data/upload/avatar/2018-02-08/5a7bf32051a6c.png"},{"id":"17","uid":"1243","touid":"1286","type":"1","addtime":"1550202419","edittime":"1550202419","backstatus":"0","status":"1","user_nicename":"武汉小静","avatar":"/data/upload/avatar/2018-02-08/5a7bd42188155.jpg"}]
     */
    private int is_who_like;
    private int num;
    private List<ListBean> list;

    public int getIs_who_like() {
        return is_who_like;
    }

    public void setIs_who_like(int is_who_like) {
        this.is_who_like = is_who_like;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 16
         * uid : 1251
         * touid : 1286
         * type : 1
         * addtime : 1550202418
         * edittime : 1550202418
         * backstatus : 0
         * status : 1
         * user_nicename : 顾美丽
         * avatar : /data/upload/avatar/2018-02-08/5a7bf32051a6c.png
         */

        private String id;
        private String uid;
        private String touid;
        private String type;
        private String addtime;
        private String edittime;
        private String backstatus;
        private String status;
        private String user_nicename;
        private String avatar;

        private String lastMessage;
        private boolean unreadMessage;

        public String getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(String lastMessage) {
            this.lastMessage = lastMessage;
        }

        public boolean isUnreadMessage() {
            return unreadMessage;
        }

        public void setUnreadMessage(boolean unreadMessage) {
            this.unreadMessage = unreadMessage;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTouid() {
            return touid;
        }

        public void setTouid(String touid) {
            this.touid = touid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getEdittime() {
            return edittime;
        }

        public void setEdittime(String edittime) {
            this.edittime = edittime;
        }

        public String getBackstatus() {
            return backstatus;
        }

        public void setBackstatus(String backstatus) {
            this.backstatus = backstatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
