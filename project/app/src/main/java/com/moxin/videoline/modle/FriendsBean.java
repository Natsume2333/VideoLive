package com.moxin.videoline.modle;

import java.util.List;

public class FriendsBean {

    /**
     * code : 1
     * msg :
     * data : [{"id":"用户id","sex":"性别","user_nickname":"昵称","avatar":"头像","level":"等级","is_online":"在线状态1在0否","signature":"签名","vip_end_time":"vip到期时间","province":"省","city":"市","birthday":"生日","user_img":[{"id":"图片id","img":"图片","uid":"用户id"}],"is_focus":"是否关注1是2否","charging_coin":"通话消费的金币","is_vip":"1vip0否","sign":""}]
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
         * id : 用户id
         * sex : 性别
         * user_nickname : 昵称
         * avatar : 头像
         * level : 等级
         * is_online : 在线状态1在0否
         * signature : 签名
         * vip_end_time : vip到期时间
         * province : 省
         * city : 市
         * birthday : 生日
         * user_img : [{"id":"图片id","img":"图片","uid":"用户id"}]
         * is_focus : 是否关注1是2否
         * charging_coin : 通话消费的金币
         * is_vip : 1vip0否
         * sign :
         */

        private String id;
        private String sex;
        private String user_nickname;
        private String avatar;
        private String level;
        private String is_online;
        private String signature;
        private String vip_end_time;
        private String province;
        private String city;
        private String birthday;
        private String is_focus;
        private String charging_coin;
        private String is_vip;
        private String sign;
        private List<UserImgBean> user_img;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
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

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getIs_online() {
            return is_online;
        }

        public void setIs_online(String is_online) {
            this.is_online = is_online;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getVip_end_time() {
            return vip_end_time;
        }

        public void setVip_end_time(String vip_end_time) {
            this.vip_end_time = vip_end_time;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getIs_focus() {
            return is_focus;
        }

        public void setIs_focus(String is_focus) {
            this.is_focus = is_focus;
        }

        public String getCharging_coin() {
            return charging_coin;
        }

        public void setCharging_coin(String charging_coin) {
            this.charging_coin = charging_coin;
        }

        public String getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(String is_vip) {
            this.is_vip = is_vip;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public List<UserImgBean> getUser_img() {
            return user_img;
        }

        public void setUser_img(List<UserImgBean> user_img) {
            this.user_img = user_img;
        }

        public static class UserImgBean {
            /**
             * id : 图片id
             * img : 图片
             * uid : 用户id
             */

            private String id;
            private String img;
            private String uid;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }
        }
    }
}
