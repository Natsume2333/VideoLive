package com.moxin.videoline.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moxin.videoline.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteBoyHeaderView extends FrameLayout {


    @BindView(R.id.center)
    TextView center;

    @BindView(R.id.left_top)
    TextView left_top;

    @BindView(R.id.left_bottom)
    TextView left_bottom;

    @BindView(R.id.right_top)
    TextView right_top;

    @BindView(R.id.right_bottom)
    TextView right_bottom;

    @BindView(R.id.title_center)
    TextView title_canter;

    public InviteBoyHeaderView(@NonNull Context context) {
        super(context);
        init();
    }

    public InviteBoyHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InviteBoyHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void boy(String c, String lb, String rb) {
        center.setText(c);
        left_top.setText("充值奖励");
        left_bottom.setText(lb);
        right_top.setText("注册奖励");
        right_bottom.setText(rb);
        title_canter.setText("充值奖励");
    }

    public void girl(String c, String lb, String rb) {
        center.setText(c);
        left_top.setText("收益奖励");
        left_bottom.setText(lb);
        right_top.setText("注册奖励");
        right_bottom.setText(rb);
        title_canter.setText("收益奖励");

    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.invite_boy_header, null, false);
        addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ButterKnife.bind(this, view);
    }
}
