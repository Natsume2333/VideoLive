package com.moxin.videoline.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.custommsg.CustomMsgAllGift;
import com.moxin.videoline.modle.custommsg.CustomMsgOpenVip;
import com.moxin.videoline.utils.BGTimedTaskManage;
import com.moxin.videoline.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CuckooAllGiftView extends RelativeLayout implements BGTimedTaskManage.BGTimeTaskRunnable {

    private TextView tv_content;
    private CircleImageView iv_avatar;
    private ImageView iv_gift_icon;

    private List<CustomMsgAllGift> msgAllList = new ArrayList<>();
    private List<CustomMsgOpenVip> vipAllList = new ArrayList<>();
    private BGTimedTaskManage timedTaskManage = new BGTimedTaskManage();

    private boolean isFinish = true;

    public CuckooAllGiftView(Context context) {
        super(context);
        init(context);
    }

    public CuckooAllGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CuckooAllGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        View view = inflate(context, R.layout.view_all_gift_danmu, null);
        addView(view);
        tv_content = findViewById(R.id.tv_content);
        iv_avatar = findViewById(R.id.iv_avatar);
        iv_gift_icon = findViewById(R.id.iv_gift_icon);

        timedTaskManage.setTimeTaskRunnable(this);
        timedTaskManage.setTime(3 * 1000);
    }

    public void addMsg(CustomMsgAllGift msg) {
        msgAllList.add(msg);
    }

    public void addMsg(CustomMsgOpenVip msg) {
        vipAllList.add(msg);
    }


    public void start() {
        timedTaskManage.startRunnable(true);
    }

    public void stop() {
        timedTaskManage.stopRunnable();
    }

    @Override
    public void onRunTask() {
        if (!Utils.isBackground() && isFinish && msgAllList.size() != 0) {
            setVisibility(VISIBLE);
            isFinish = false;
            CustomMsgAllGift msg = msgAllList.get(0);
            //填充数据
            tv_content.setText(msg.getSend_gift_info().getSend_msg());
            Utils.loadHttpImg(msg.getSender().getAvatar(), iv_avatar);
            Utils.loadHttpImg(msg.getSend_gift_info().getGift_icon(), iv_gift_icon);
            //开始倒计时
            new CountDownTimer(10000, 1000) {

                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    isFinish = true;
                    msgAllList.remove(0);
                    tv_content.setText("");
                    setVisibility(GONE);
                }
            }.start();
        } else if (!Utils.isBackground() && isFinish && vipAllList.size() != 0) {
            setVisibility(VISIBLE);
            isFinish = false;
            CustomMsgOpenVip msg = vipAllList.get(0);
            //填充数据
            tv_content.setText(msg.getVip_info().getSend_msg());
            Utils.loadHttpImg(msg.getSender().getAvatar(), iv_avatar);
            //开始倒计时
            new CountDownTimer(10000, 1000) {

                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    isFinish = true;
                    vipAllList.remove(0);
                    tv_content.setText("");
                    setVisibility(GONE);
                }
            }.start();
        }
    }
}
