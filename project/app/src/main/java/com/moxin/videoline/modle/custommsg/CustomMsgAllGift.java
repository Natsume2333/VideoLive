package com.moxin.videoline.modle.custommsg;

public class CustomMsgAllGift extends CustomMsg{


    /**
     * channel : all
     * sender : {"id":100190,"user_nickname":"王小狗","avatar":""}
     * send_gift_info : {"send_user_nickname":"王小狗","send_user_id":100190,"send_to_user_id":100190,"send_to_user_nickname":"王小狗","send_msg":"王小狗送给王二狗一个xxx"}
     */

    private String channel;
    private SendGiftInfoBean send_gift_info;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public SendGiftInfoBean getSend_gift_info() {
        return send_gift_info;
    }

    public void setSend_gift_info(SendGiftInfoBean send_gift_info) {
        this.send_gift_info = send_gift_info;
    }


    public static class SendGiftInfoBean {
        /**
         * send_user_nickname : 王小狗
         * send_user_id : 100190
         * send_to_user_id : 100190
         * send_to_user_nickname : 王小狗
         * send_msg : 王小狗送给王二狗一个xxx
         */

        private String send_user_nickname;
        private String send_user_id;
        private String send_to_user_id;
        private String send_to_user_nickname;
        private String send_msg;
        private String gift_icon;

        public String getGift_icon() {
            return gift_icon;
        }

        public void setGift_icon(String gift_icon) {
            this.gift_icon = gift_icon;
        }

        public String getSend_user_nickname() {
            return send_user_nickname;
        }

        public void setSend_user_nickname(String send_user_nickname) {
            this.send_user_nickname = send_user_nickname;
        }

        public String getSend_user_id() {
            return send_user_id;
        }

        public void setSend_user_id(String send_user_id) {
            this.send_user_id = send_user_id;
        }

        public String getSend_to_user_id() {
            return send_to_user_id;
        }

        public void setSend_to_user_id(String send_to_user_id) {
            this.send_to_user_id = send_to_user_id;
        }

        public String getSend_to_user_nickname() {
            return send_to_user_nickname;
        }

        public void setSend_to_user_nickname(String send_to_user_nickname) {
            this.send_to_user_nickname = send_to_user_nickname;
        }

        public String getSend_msg() {
            return send_msg;
        }

        public void setSend_msg(String send_msg) {
            this.send_msg = send_msg;
        }
    }
}
