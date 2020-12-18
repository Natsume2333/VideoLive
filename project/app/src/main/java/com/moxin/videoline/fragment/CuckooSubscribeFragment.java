package com.moxin.videoline.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.adapter.CuckooSubscribeAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseListFragment;
import com.moxin.videoline.json.JsonDoGetSubscribeModelList;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestsDoCall;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.CuckooSubscribeModel;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.ui.PlayerCallActivity;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.im.IMHelp;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 魏鹏
 * email 1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 */
public class CuckooSubscribeFragment extends BaseListFragment<CuckooSubscribeModel> implements CuckooSubscribeAdapter.CancelSubScribeListener {

    public static String ACTION = "ACTION";

    private int action = 1;
    private CuckooSubscribeAdapter cuckooSubscribeAdapter;

    public static CuckooSubscribeFragment getInstance(int action) {
        CuckooSubscribeFragment fragment = new CuckooSubscribeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ACTION, action);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initDate(View view) {
        super.initDate(view);
        action = getArguments().getInt(ACTION, 0);
    }

    @Override
    protected void requestGetData(boolean isCache) {

        Api.doRequestGetSubscribeList(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), action, page, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("SubscribeList", s);
                JsonDoGetSubscribeModelList data = (JsonDoGetSubscribeModelList) JsonRequestBase.getJsonObj(s, JsonDoGetSubscribeModelList.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    onLoadDataResult(data.getList());
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtils.showLong("网络请求错误！");
            }
        });
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        cuckooSubscribeAdapter = new CuckooSubscribeAdapter(dataList, action);
        cuckooSubscribeAdapter.setCancelSubScribeListener(this);
        return cuckooSubscribeAdapter;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {

        //主播
        if (action == 2 && StringUtils.toInt(dataList.get(position).getStatus()) == 0) {
            new QMUIDialog.MessageDialogBuilder(getContext())
                    .setTitle("提示")
                    .setMessage("是否回拨视频？")
                    .addAction(0, "回拨", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            clickBackVideoCall(dataList.get(position).getUser_id());
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, "取消", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            //用户
            if (action == 2) {
                Common.jumpUserPage(getActivity(), dataList.get(position).getUser_id());
            } else {
                Common.jumpUserPage(getActivity(), dataList.get(position).getTo_user_id());
            }

        }
    }

    private void clickBackVideoCall(final String toUserId) {

        showLoadingDialog("正在回拨...");
        Api.doRequestBackVideoCall(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), toUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                final JsonRequestsDoCall requestObj = JsonRequestsDoCall.getJsonObj(s);
                if (StringUtils.toInt(requestObj.getCode()) != 1) {
                    hideLoadingDialog();
                    ToastUtils.showLong(requestObj.getMsg());
                    return;
                }

                IMHelp.sendVideoCallMsg(requestObj.getData().getChannel_id(), toUserId, 0, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
                        hideLoadingDialog();
                        LogUtils.i("IM 一对一消息推送失败！");
                        ToastUtils.showLong("拨打通话失败！");
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        hideLoadingDialog();
                        LogUtils.i("IM 一对一消息推送成功！");
                        UserModel callUserInfo = new UserModel();
                        callUserInfo.setId(requestObj.getData().getTo_user_base_info().getId());
                        callUserInfo.setUser_nickname(requestObj.getData().getTo_user_base_info().getUser_nickname());
                        callUserInfo.setAvatar(requestObj.getData().getTo_user_base_info().getAvatar());
                        callUserInfo.setSex(requestObj.getData().getTo_user_base_info().getSex());

                        Intent intent = new Intent(getContext(), PlayerCallActivity.class);
                        intent.putExtra(PlayerCallActivity.CALL_USER_INFO, callUserInfo);
                        intent.putExtra(PlayerCallActivity.CALL_CHANNEL_ID, requestObj.getData().getChannel_id());
                        getContext().startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }


    /**
     * 取消预约
     *
     * @param id
     */
    @Override
    public void onCancelSubScribeListener(String id) {
        Api.cancelSubScribe(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), id, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("cancelSubScribe", s);
                final JsonRequestsDoCall requestObj = JsonRequestsDoCall.getJsonObj(s);
                if (StringUtils.toInt(requestObj.getCode()) == 1) {
                    requestGetData(false);
                }
                ToastUtils.showLong(requestObj.getMsg());
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }
}
