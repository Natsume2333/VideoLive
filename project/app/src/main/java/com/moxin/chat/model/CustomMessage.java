package com.moxin.chat.model;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.moxin.chat.adapter.ChatAdapter;
import com.moxin.chat.utils.TimeUtil;
import com.moxin.videoline.BuildConfig;
import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.ICustomMsg;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.event.EventChatClickPrivateImgMessage;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.modle.custommsg.CustomMsg;
import com.moxin.videoline.modle.custommsg.CustomMsgPrivateGift;
import com.moxin.videoline.modle.custommsg.CustomMsgPrivatePhoto;
import com.moxin.videoline.utils.Utils;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * 自定义消息
 */
public class CustomMessage extends Message {


    private String TAG = getClass().getSimpleName();

    private int type;

    private CustomMsg customMsg;

    public CustomMessage(TIMMessage message) {
        this.message = message;
    }

    public CustomMessage(CustomMsg customMsg, int type) {
        message = new TIMMessage();

        switch (type) {
            case LiveConstant.CustomMsgType.MSG_PRIVATE_GIFT:

                message = customMsg.parseToTIMMessage(message);
                break;
            default:
                break;
        }
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {

        CustomMsg customMsg = parseToModel(CustomMsg.class);

        if (customMsg != null) {
            type = customMsg.getType();

            Class realCustomMsgClass = LiveConstant.mapCustomMsgClass.get(customMsg.getType());
            if (realCustomMsgClass == null) {
                return;
            }
            CustomMsg realCustomMsg = parseToModel(realCustomMsgClass);
            setCustomMsg(realCustomMsg);

            switch (customMsg.getType()) {

                case LiveConstant.CustomMsgType.MSG_PRIVATE_GIFT:

                    CustomMsgPrivateGift customMsgPrivateGift = getCustomMsgReal();
                    if (customMsgPrivateGift != null) {
                        // 私聊消息类型
                        setPrivateMsgType(viewHolder, context, customMsgPrivateGift);
                    }
                    break;

                case LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL:

                    setVideoLinMsgType(viewHolder, context, customMsg.getSender(), "拨打视频通话");
                    break;

                case LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL_END:

                    setVideoLinMsgType(viewHolder, context, customMsg.getSender(), "结束视频通话");
                    break;

                case LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL_REPLY:

                    LogUtils.i("MSG_VIDEO_LINE_CALL_REPLY");
                    setVideoLinMsgType(viewHolder, context, customMsg.getSender(), "视频通话回复");
                    break;
                case LiveConstant.CustomMsgType.MSG_PRIVATE_IMG:
                    CustomMsgPrivatePhoto customMsgPrivatePhoto = getCustomMsgReal();
                    if (customMsgPrivatePhoto != null) {
                        // 私聊消息类型
                        setPrivateImgMsgType(viewHolder, context, customMsgPrivatePhoto);
                    }
                    LogUtils.i("MSG_PRIVATE_IMG");
                    break;
                default:
                    LogUtils.i("default");
                    //viewHolder.llContent.setVisibility(View.GONE);
                    break;
            }
        }

    }

    //私照信息
    private void setPrivateImgMsgType(ChatAdapter.ViewHolder viewHolder, Context context, final CustomMsgPrivatePhoto customMsgPrivatePhoto) {

        clearView(viewHolder);
        if (checkRevoke(viewHolder)) {
            return;
        }

        viewHolder.systemMessage.setVisibility(hasTime ? View.VISIBLE : View.GONE);
        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));

        ImageView iv = new ImageView(CuckooApplication.getInstances());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setLayoutParams(new ViewGroup.LayoutParams(ConvertUtils.dp2px(100), ConvertUtils.dp2px(100)));
        Utils.loadHttpImg(customMsgPrivatePhoto.getImg(), iv);
        if (message.isSelf()) {
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            viewHolder.rightMessage.addView(iv);

        } else {
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            viewHolder.leftMessage.addView(iv);
        }

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventChatClickPrivateImgMessage event = new EventChatClickPrivateImgMessage();
                event.setId(customMsgPrivatePhoto.getId());
                event.setImg(customMsgPrivatePhoto.getImg());
                EventBus.getDefault().post(event);
            }
        });
        setSenderUserInfo(viewHolder, context, customMsgPrivatePhoto.getSender());
        showStatus(viewHolder);
    }


    //一对一视频请求消息UI填充
    private void setVideoLinMsgType(ChatAdapter.ViewHolder viewHolder, Context context, UserModel userModel, String msg) {

        clearView(viewHolder);
        if (checkRevoke(viewHolder)) {
            return;
        }

        viewHolder.systemMessage.setVisibility(hasTime ? View.VISIBLE : View.GONE);
        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));

        TextView tv = new TextView(CuckooApplication.getInstances());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tv.setPadding(10,8,10,8);
        tv.setTextColor(CuckooApplication.getInstances().getResources().getColor(isSelf() ? R.color.admin_color : R.color.admin_color));
        if (message.isSelf()) {
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            tv.setText(msg);
            viewHolder.rightMessage.addView(tv);
            viewHolder.rightMessage.setBackgroundResource(R.drawable.bg_bubble_blue);
        } else {
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            tv.setText(msg);
            viewHolder.leftMessage.addView(tv);
            viewHolder.leftMessage.setBackgroundResource(R.drawable.bg_bubble_gray);
        }
        setSenderUserInfo(viewHolder, context, userModel);
        showStatus(viewHolder);
    }


    //私信送礼物UI填充数据
    private void setPrivateMsgType(ChatAdapter.ViewHolder viewHolder, Context context, CustomMsgPrivateGift customMsgPrivateGift) {
        clearView(viewHolder);
        if (checkRevoke(viewHolder)) {
            return;
        }

        viewHolder.systemMessage.setVisibility(hasTime ? View.VISIBLE : View.GONE);
        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));


        if (message.isSelf()) {
            View view_private_msg_view = getView(R.layout.view_private_recived_gift_msg);
            ImageView iv_gift = view_private_msg_view.findViewById(R.id.iv_gift);
            TextView tv = view_private_msg_view.findViewById(R.id.tv_msg);
            initLayout(customMsgPrivateGift, iv_gift, tv);

            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            tv.setText(customMsgPrivateGift.getFrom_msg());
            tv.setBackgroundResource(R.drawable.chat_send_gift_right_yellow_bac);

            viewHolder.rightMessage.setBackgroundResource(0);
            viewHolder.rightMessage.setPadding(0,0,0,0);
            viewHolder.rightMessage.addView(view_private_msg_view);

        } else {
            View view_private_msg_view = getView(R.layout.view_private_send_gift_msg);
            ImageView iv_gift = view_private_msg_view.findViewById(R.id.iv_gift);
            TextView tv = view_private_msg_view.findViewById(R.id.tv_msg);
            initLayout(customMsgPrivateGift, iv_gift, tv);

            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            tv.setText(customMsgPrivateGift.getTo_msg());
            tv.setBackgroundResource(R.drawable.chat_send_gift_left_white_bac);

            viewHolder.leftMessage.setBackgroundResource(0);
            viewHolder.leftMessage.setPadding(0,0,0,0);
            viewHolder.leftMessage.addView(view_private_msg_view);
        }
        setSenderUserInfo(viewHolder, context, customMsgPrivateGift.getSender());
        showStatus(viewHolder);
    }

    private void initLayout(CustomMsgPrivateGift customMsgPrivateGift, ImageView iv_gift, TextView tv) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tv.setTextColor(CuckooApplication.getInstances().getResources().getColor(isSelf() ? R.color.gray_black : R.color.gift_msg_color));
        Utils.loadHttpImg(customMsgPrivateGift.getProp_icon(), iv_gift);
    }

    private View getView(int res) {
        return LayoutInflater.from(CuckooApplication.getInstances()).inflate(res, null);
    }


    public void setCustomMsg(CustomMsg customMsg) {
        this.customMsg = customMsg;
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;

        CustomMsg customMsg = parseToModel(CustomMsg.class);

        String result = "";
        if (customMsg != null) {
            type = customMsg.getType();

            Class realCustomMsgClass = LiveConstant.mapCustomMsgClass.get(customMsg.getType());
            if (realCustomMsgClass == null) {
                return result;
            }
            CustomMsg realCustomMsg = parseToModel(realCustomMsgClass);
            setCustomMsg(realCustomMsg);

            switch (customMsg.getType()) {

                case LiveConstant.CustomMsgType.MSG_PRIVATE_GIFT:

                    CustomMsgPrivateGift customMsgPrivateGift = getCustomMsgReal();
                    if (customMsgPrivateGift != null) {
                        // 私聊消息类型
                        result = "赠送礼物";
                    }
                    break;

                case LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL:

                    result = "拨打视频通话";
                    break;

                case LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL_END:

                    result = "结束视频通话";
                    break;

                case LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL_REPLY:

                    result = "视频通话回复";
                    break;
                case LiveConstant.CustomMsgType.MSG_PRIVATE_IMG:
                    CustomMsgPrivatePhoto customMsgPrivatePhoto = getCustomMsgReal();
                    if (customMsgPrivatePhoto != null) {
                        // 私聊消息类型
                        result = "私照";
                    }
                    LogUtils.i("MSG_PRIVATE_IMG");
                    break;
                default:
                    LogUtils.i("default");
                    //viewHolder.llContent.setVisibility(View.GONE);
                    break;
            }
        }
        return result;
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }


    public <T extends ICustomMsg> T getCustomMsgReal() {
        T t = null;
        try {
            return (T) getCustomMsg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CustomMsg getCustomMsg() {
        return customMsg;
    }

    public <T extends CustomMsg> T parseToModel(Class<T> clazz) {
        T model = null;
        String json = null;
        try {
            byte[] data = null;

            data = ((TIMCustomElem) message.getElement(0)).getData();

            json = new String(data, LiveConstant.DEFAULT_CHARSET);
            model = JSON.parseObject(json, clazz);

            if (BuildConfig.DEBUG) {
                LogUtils.i("parseToModel " + model.getType() + ":" + json);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                //LogUtils.e("(" + getConversationPeer() + ")parse msg error:" + e.toString() + ",json:" + json);
            }
        } finally {

        }
        return model;
    }
}
