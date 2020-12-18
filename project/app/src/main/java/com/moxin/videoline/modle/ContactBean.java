package com.moxin.videoline.modle;

public class ContactBean {


    /**
     * code : 0
     * msg :
     * data : []
     * user : {"user_nickname":"真由理","id":100202,"coin":190,"sex":2,"avatar":"http://videoline.qiniu.bugukj.com/'user/20190815/f66f7a635a05f039f55b81e68fe9a724.jpeg'","user_status":2,"level":4,"address":"济南市","signature":"王者荣耀","is_auth":1,"wx_number":"","qq_number":"","phone_number":"","wx_price":0,"qq_price":0,"phone_price":0}
     */

    private int code;
    private String msg;
    //    private UserBean user;
//    private List<?> data;
    private UserBean data;

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

    public UserBean getUser() {
        return data;
    }

    public void setUser(UserBean user) {
        this.data = user;
    }

//    public List<?> getData() {
//        return data;
//    }
//
//    public void setData(List<?> data) {
//        this.data = data;
//    }

    public static class UserBean {
        /**
         * user_nickname : 真由理
         * id : 100202
         * coin : 190
         * sex : 2
         * avatar : http://videoline.qiniu.bugukj.com/'user/20190815/f66f7a635a05f039f55b81e68fe9a724.jpeg'
         * user_status : 2
         * level : 4
         * address : 济南市
         * signature : 王者荣耀
         * is_auth : 1
         * wx_number :
         * qq_number :
         * phone_number :
         * wx_price : 0
         * qq_price : 0
         * phone_price : 0
         */

        private String user_nickname;
        private int id;
        private int coin;
        private int sex;
        private String avatar;
        private int user_status;
        private int level;
        private String address;
        private String signature;
        private int is_auth;
        private String wx_number;
        private String qq_number;
        private String phone_number;
        private String wx_price;
        private String qq_price;
        private String phone_price;

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

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

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getUser_status() {
            return user_status;
        }

        public void setUser_status(int user_status) {
            this.user_status = user_status;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public int getIs_auth() {
            return is_auth;
        }

        public void setIs_auth(int is_auth) {
            this.is_auth = is_auth;
        }

        public String getWx_number() {
            return wx_number;
        }

        public void setWx_number(String wx_number) {
            this.wx_number = wx_number;
        }

        public String getQq_number() {
            return qq_number;
        }

        public void setQq_number(String qq_number) {
            this.qq_number = qq_number;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getWx_price() {
            return wx_price;
        }

        public void setWx_price(String wx_price) {
            this.wx_price = wx_price;
        }

        public String getQq_price() {
            return qq_price;
        }

        public void setQq_price(String qq_price) {
            this.qq_price = qq_price;
        }

        public String getPhone_price() {
            return phone_price;
        }

        public void setPhone_price(String phone_price) {
            this.phone_price = phone_price;
        }
    }
}
