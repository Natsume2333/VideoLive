package com.moxin.videoline.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.RankModel;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingHeaderView extends FrameLayout {
    public RankingHeaderView(@NonNull Context context) {
        super(context);
        init();
    }


    public RankingHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RankingHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @BindView(R.id.rk_icon_1)
    ImageView icon1;
    @BindView(R.id.rk_icon_2)
    ImageView icon2;
    @BindView(R.id.rk_icon_3)
    ImageView icon3;


    @BindView(R.id.rk_name1)
    TextView name1;
    @BindView(R.id.rk_name2)
    TextView name2;
    @BindView(R.id.rk_name3)
    TextView name3;


    @BindView(R.id.rk_vip_man1)
    TextView man1;
    @BindView(R.id.rk_vip_man2)
    TextView man2;
    @BindView(R.id.rk_vip_man3)
    TextView man3;

    @BindView(R.id.rk_vip_woman1)
    TextView woman1;
    @BindView(R.id.rk_vip_woman2)
    TextView woman2;
    @BindView(R.id.rk_vip_woman3)
    TextView woman3;


    @BindView(R.id.rk_address1)
    TextView address1;
    @BindView(R.id.rk_address2)
    TextView address2;
    @BindView(R.id.rk_address3)
    TextView address3;

    @BindView(R.id.rk_coin1)
    TextView coin1;
    @BindView(R.id.rk_coin2)
    TextView coin2;
    @BindView(R.id.rk_coin3)
    TextView coin3;


    public List<RankModel> setData(List<RankModel> data) {
        int size = data.size();
        if (size > 0) {
            RankModel bean = data.remove(0);
            show1(bean);
            if (size > 1) {
                RankModel bean2 = data.remove(0);
                show2(bean2);
                if (size > 2) {
                    RankModel bean3 = data.remove(0);
                    show3(bean3);
                }
            }
        }
        return data;
    }

    @BindView(R.id.left)
    View left;
    @BindView(R.id.center)
    View center;
    @BindView(R.id.right)
    View right;

    private void show1(final RankModel bean) {
        name1.setText(bean.getUser_nickname());
        Utils.loadUserIcon(bean.getAvatar(),icon1);
        if("2".equals(bean.getSex())){
            man1.setVisibility(GONE);
            woman1.setVisibility(VISIBLE);
            woman1.setText(bean.getLevel());
        }else {
            man1.setVisibility(VISIBLE);
            woman1.setVisibility(GONE);
            woman1.setText(bean.getLevel());
        }
        address1.setText(bean.getAddress());
        coin1.setText(bean.getTotal());
        center.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.jumpUserPage(getContext(), bean.getId());
            }
        });
    }

    private void show2(final RankModel bean) {
        name2.setText(bean.getUser_nickname());
        Utils.loadUserIcon(bean.getAvatar(),icon2);
        if("2".equals(bean.getSex())){
            man2.setVisibility(GONE);
            woman2.setVisibility(VISIBLE);
            woman2.setText(bean.getLevel());
        }else {
            man2.setVisibility(VISIBLE);
            woman2.setVisibility(GONE);
            woman2.setText(bean.getLevel());
        }
        address2.setText(bean.getAddress());
        coin2.setText(bean.getTotal());
        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.jumpUserPage(getContext(), bean.getId());
            }
        });
    }

    private void show3(final RankModel bean) {
        name3.setText(bean.getUser_nickname());
        Utils.loadUserIcon(bean.getAvatar(),icon3);
        if("2".equals(bean.getSex())){
            man3.setVisibility(GONE);
            woman3.setVisibility(VISIBLE);
            woman3.setText(bean.getLevel());
        }else {
            man3.setVisibility(VISIBLE);
            woman3.setVisibility(GONE);
            woman3.setText(bean.getLevel());
        }
        address3.setText(bean.getAddress());
        coin3.setText(bean.getTotal());
        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.jumpUserPage(getContext(), bean.getId());
            }
        });
    }

    private void init() {
        View rank = LayoutInflater.from(getContext()).inflate(R.layout.main_rank_header, null, false);
        addView(rank, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ButterKnife.bind(this, rank);
    }
}
