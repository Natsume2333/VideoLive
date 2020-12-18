package com.moxin.videoline.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.recycler.RecyclerRecommendAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseListFragment;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequestOneKeyCall;
import com.moxin.videoline.json.JsonRequestsImage;
import com.moxin.videoline.json.JsonRequestsTarget;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.BannerImgBean;
import com.moxin.videoline.modle.OnKeyCallBean;
import com.moxin.videoline.ui.CuckooOrderActivity;
import com.moxin.videoline.ui.WebViewActivity;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;
import com.stx.xhb.xbanner.XBanner;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 推荐
 */

public class RecommendFragment extends BaseListFragment<TargetUserData> {

    //轮播图
    private XBanner recommendRoll;
    //头部布局
    private View rollView;


    private ImageView call_icon1;
    private ImageView call_icon2;
    private TextView call_num;

    private ArrayList<BannerImgBean> rollImg = new ArrayList<>();
    private RelativeLayout emptyLayout;
    //    private RecyclerView mBannerRv;

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new RecyclerRecommendAdapter(getContext(), dataList);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManage() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    protected void initDate(View view) {

    }

    @Override
    public boolean canHasEmptyLayout() {
        return false;
    }

    @Override
    protected void initSet(View view) {

        //引入头部信息
        rollView = LayoutInflater.from(getContext()).inflate(R.layout.view_roll_page, null);
        call_icon1 = rollView.findViewById(R.id.icon_l);
        call_icon2 = rollView.findViewById(R.id.icon_r);
        call_num = rollView.findViewById(R.id.call_num);

        emptyLayout = rollView.findViewById(R.id.recommend_empty_layout);

        if (SaveData.getInstance().getUserInfo().getSex() == 1) {
            //男号
            rollView.findViewById(R.id.boy_header).setVisibility(View.VISIBLE);
            rollView.findViewById(R.id.call_one).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickOneKeyCall();
                }
            });
            Log.i("性别", "initSet: 男");

        } else {
            rollView.findViewById(R.id.boy_header).setVisibility(View.GONE);
            Log.i("性别", "initSet: 女");

        }

        recommendRoll = rollView.findViewById(R.id.mRollPagerView);


        initXbanner();

        //添加头部
        baseQuickAdapter.addHeaderView(rollView);
        baseQuickAdapter.notifyDataSetChanged();


    }

    private void initXbanner() {
        //设置图片加载器
        recommendRoll.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ImageView tvContent = (ImageView) view.findViewById(R.id.custom_imageview_layout);
                BannerImgBean bannerImgBean = (BannerImgBean) model;
                Utils.loadHttpImg(Utils.getCompleteImgUrl(bannerImgBean.getImage()), tvContent);

            }
        });
        recommendRoll.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                BannerImgBean imageData = rollImg.get(position);
                if (imageData.getUrl() != null && imageData.getTitle() != null) {

                    if (imageData.getUrl().equals("#order_page")) {
                        Intent intent = new Intent(getContext(), CuckooOrderActivity.class);
                        startActivity(intent);
                    } else {
                        WebViewActivity.openH5Activity(getContext(), true, imageData.getTitle(), imageData.getUrl());
                    }
                }
            }
        });
    }

    @Override
    protected void initDisplayData(View view) {

    }


    @Override
    public void fetchData() {
        //加载数据源
        requestGetData(false);

        //加载推荐页轮播图
        requestRecommendRoll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    //一键约爱
    private void clickOneKeyCall() {

        showLoadingDialog("正在接通...");
        Api.doRequestOneKeyCall(
                SaveData.getInstance().getId(),
                SaveData.getInstance().getToken(),
                new StringCallback() {

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        hideLoadingDialog();

                        JsonRequestOneKeyCall requestObj = (JsonRequestOneKeyCall) JsonRequestOneKeyCall.getJsonObj(s, JsonRequestOneKeyCall.class);
                        if (requestObj.getCode() == 1) {
                            Common.callVideo(getContext(), requestObj.getEmcee_id(), 0);
                        } else {
                            LogUtils.i("拨打电话error:" + requestObj.getMsg());
                            ToastUtils.showLong(requestObj.getMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        hideLoadingDialog();
                    }
                }

        );
    }


    /**
     * 加载轮播图
     */
    private void requestRecommendRoll() {
        Api.getRollImage(
                uId,
                uToken,
                "1",
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JsonRequestsImage requestObj = JsonRequestsImage.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            rollImg.clear();
                            rollImg.addAll(requestObj.getData());
                            recommendRoll.setBannerData(R.layout.banner_custom_layout, rollImg);
                            recommendRoll.startAutoPlay();
                        }
                    }
                }
        );

        Api.getOneKeyCallInfo(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("一键约爱", "onSuccess: " + s);
                OnKeyCallBean bean = new Gson().fromJson(s, OnKeyCallBean.class);
                if (bean == null || bean.getData() == null || bean.getData().size() == 0) return;
                Utils.loadUserIcon(bean.getData().get(0).getAvatar(), call_icon1);
                if (bean.getData().size() > 1)
                    Utils.loadUserIcon(bean.getData().get(1).getAvatar(), call_icon2);
                call_num.setText("10");
            }
        });
    }


    @Override
    protected void requestGetData(boolean isCache) {
        final int userHeadWidth = ConvertUtils.dp2px(30);
        Api.getRecommendUserList(
                uId,
                uToken,
                page,
                new StringCallback() {

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("getRecommendUserList", s);
                        JsonRequestsTarget requestObj = JsonRequestsTarget.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            call_num.setText(requestObj.getOnline_emcee_count());

                            if (page == 1 && requestObj.getData() != null && requestObj.getData().size() == 0) {
                                emptyLayout.setVisibility(View.VISIBLE);
                            } else {
                                emptyLayout.setVisibility(View.GONE);
                            }
                            onLoadDataResult(requestObj.getData());
                        } else {
                            onLoadDataError();
                            showToastMsg(getContext(), requestObj.getMsg());
                        }
                    }
                }
        );
    }


    @Override
    public void onResume() {
        super.onResume();
        requestRecommendRoll();

    }


    @Override
    public void onStop() {
        super.onStop();
        recommendRoll.stopAutoPlay();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        Common.jumpUserPage(getContext(), dataList.get(position).getId());
    }
}