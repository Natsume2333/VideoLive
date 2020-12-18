package com.moxin.videoline.modle.custommsg;

public class CustomMsgOpenVip extends CustomMsg {

    private VipInfoBean vip_info;

    public VipInfoBean getVip_info() {
        return vip_info;
    }

    public void setVip_info(VipInfoBean vip_info) {
        this.vip_info = vip_info;
    }

    public static class VipInfoBean{
        private String send_msg;

        public String getSend_msg() {
            return send_msg;
        }

        public void setSend_msg(String send_msg) {
            this.send_msg = send_msg;
        }
    }
}
