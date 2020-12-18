package com.moxin.videoline.utils.im;

import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.moxin.chat.model.Message;
import com.moxin.chat.model.TextMessage;
import com.moxin.videoline.modle.custommsg.CustomMsgText;
import com.moxin.videoline.modle.custommsg.CustomMsgVideoCall;
import com.moxin.videoline.modle.custommsg.CustomMsgVideoCallEnd;
import com.moxin.videoline.modle.custommsg.CustomMsgVideoCallReply;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendResult;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 魏鹏 on 2018/5/9.
 * email:1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 */

public class IMHelp {

    public static void addBlackUser(String userId, @NonNull TIMValueCallBack<List<TIMFriendResult>> callback) {

        List<String> list = new ArrayList<>();
        list.add(userId);
        TIMFriendshipManagerExt.getInstance().addBlackList(list, callback);
    }

    public static void deleteBlackUser(String userId, @NonNull TIMValueCallBack<List<TIMFriendResult>> callback) {

        List<String> list = new ArrayList<>();
        list.add(userId);
        TIMFriendshipManagerExt.getInstance().delBlackList(list, callback);
    }

    //发送视频通话请求
    public static void sendVideoCallMsg(String channel, String toUserId, int type, TIMValueCallBack<TIMMessage> callBack) {

        //自定义消息
        CustomMsgVideoCall customMsgVideoCall = new CustomMsgVideoCall();
        customMsgVideoCall.setChannel(channel);
        customMsgVideoCall.setCall_type(type);
        TIMMessage timMessage = new TIMMessage();

        TIMCustomElem customElem = new TIMCustomElem();
        customElem.setData(JSON.toJSONBytes(customMsgVideoCall));
        timMessage.addElement(customElem);
        TIMManager.getInstance().getConversation(TIMConversationType.C2C, toUserId)
                .sendMessage(timMessage, callBack);

    }

    //回复视频通话
    public static void sendReplyVideoCallMsg(String channel, String type, String toUserId, TIMValueCallBack<TIMMessage> callBack) {
        //自定义消息
        CustomMsgVideoCallReply customMsgVideoCallReply = new CustomMsgVideoCallReply();
        customMsgVideoCallReply.setChannel(channel);
        customMsgVideoCallReply.setReply_type(type);

        TIMMessage timMessage = new TIMMessage();

        TIMCustomElem customElem = new TIMCustomElem();
        customElem.setData(JSON.toJSONBytes(customMsgVideoCallReply));
        timMessage.addElement(customElem);
        TIMManager.getInstance().getConversation(TIMConversationType.C2C, toUserId)
                .sendMessage(timMessage, callBack);
    }

    //挂断视频通话
    public static void sendEndVideoCallMsg(String toUserId, TIMValueCallBack<TIMMessage> callBack) {
        //自定义消息
        CustomMsgVideoCallEnd customMsgVideoCallEnd = new CustomMsgVideoCallEnd();

        TIMMessage timMessage = new TIMMessage();

        TIMCustomElem customElem = new TIMCustomElem();
        customElem.setData(JSON.toJSONBytes(customMsgVideoCallEnd));
        timMessage.addElement(customElem);
        TIMManager.getInstance().getConversation(TIMConversationType.C2C, toUserId)
                .sendMessage(timMessage, callBack);
    }


    //发送文字消息
    public static void sendTextCallMsg(String trim, String toUserId, TIMConversationType type, TIMValueCallBack<TIMMessage> callBack) {
        Message message = new TextMessage(trim);
        TIMMessage timMessage = message.getMessage();

        CustomMsgText customMsgText = new CustomMsgText();
        TIMMessage senderMessage = customMsgText.parseToTIMMessage(timMessage);

        Log.e("onSendMsgListenerCount","count");
        TIMConversation conversation = TIMManager.getInstance().getConversation(type, toUserId);
        conversation.sendMessage(senderMessage, callBack);

    }

}
