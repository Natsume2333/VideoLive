package com.moxin.videoline.modle.custommsg;


import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.manage.RequestConfig;
import com.tencent.imsdk.TIMMessage;

public class CustomMsgText extends CustomMsg {

    private String text;

    public CustomMsgText() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_TEXT);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public TIMMessage parseToTIMMessage(TIMMessage msg) {
        TIMMessage timMessage = super.parseToTIMMessage(msg);
        if (RequestConfig.getConfigObj().getHas_dirty_words() == 1) {
            if (timMessage != null) {

            }
        }
        return timMessage;
    }
}
