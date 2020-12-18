package com.moxin.videoline.ui.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.chat.ui.ChatActivity;
import com.moxin.videoline.ApiConstantDefine;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.dialog.ShowPayPhotoDialog;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestDoPrivateChat;
import com.moxin.videoline.json.JsonRequestSelectPic;
import com.moxin.videoline.json.JsonRequestsDoCall;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.ui.CuckooHomePageActivity;
import com.moxin.videoline.ui.PerViewImgActivity;
import com.moxin.videoline.ui.PlayerCallActivity;
import com.moxin.videoline.ui.WealthDetailedActivity;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.im.IMHelp;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by weipeng on 2018/2/28.
 */

public class Common {

    //充值弹窗
    public static void showRechargeDialog(final Context context, String msg) {
        new QMUIDialog.MessageDialogBuilder(context)
                .setTitle("温馨提示")
                .setMessage(msg)
                .addAction(0, "马上充值", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        WealthDetailedActivity.start(context, WealthDetailedActivity.TYPE_RECHARGE);
                    }
                })
                .show();
    }

    //跳转用户主页
    public static void jumpUserPage(Context context, String uid) {
        Intent intent = new Intent(context, CuckooHomePageActivity.class);
        intent.putExtra("str", uid);
        context.startActivity(intent);
    }

    //拨打视频通话
    public static void callVideo(final Context context, final String toUserId, final int type) {
        try {

            final UserModel userModel = SaveData.getInstance().getUserInfo();

            final QMUITipDialog tipD = new QMUITipDialog.Builder(context)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("正在接通...")
                    .create();
            tipD.show();

            Api.doCallToUser(
                    userModel.getId(),
                    userModel.getToken(),
                    toUserId,
                    type,
                    new StringCallback() {

                        @Override
                        public void onSuccess(String s, Call call, Response response) {

                            Log.e("doCallToUser", s);
                            final JsonRequestsDoCall requestObj = JsonRequestsDoCall.getJsonObj(s);
                            if (requestObj.getCode() == 1) {

                                IMHelp.sendVideoCallMsg(requestObj.getData().getChannel_id(), toUserId, type, new TIMValueCallBack<TIMMessage>() {
                                    @Override
                                    public void onError(int i, String s) {
                                        tipD.dismiss();
                                        LogUtils.i("IM 一对一消息推送失败！");
                                        ToastUtils.showLong("拨打通话失败！" + i);
                                        Api.doDeleteVideoCallRecord(userModel.getId(),userModel.getToken(),requestObj.getData().getChannel_id(),null);
                                    }

                                    @Override
                                    public void onSuccess(TIMMessage timMessage) {
                                        tipD.dismiss();
                                        LogUtils.i("IM 一对一消息推送成功！");
                                        UserModel callUserInfo = new UserModel();
                                        callUserInfo.setId(requestObj.getData().getTo_user_base_info().getId());
                                        callUserInfo.setUser_nickname(requestObj.getData().getTo_user_base_info().getUser_nickname());
                                        callUserInfo.setAvatar(requestObj.getData().getTo_user_base_info().getAvatar());
                                        callUserInfo.setSex(requestObj.getData().getTo_user_base_info().getSex());

                                        Intent intent = new Intent(context, PlayerCallActivity.class);
                                        intent.putExtra(PlayerCallActivity.CALL_USER_INFO, callUserInfo);
                                        intent.putExtra(PlayerCallActivity.CALL_CHANNEL_ID, requestObj.getData().getChannel_id());
                                        intent.putExtra(PlayerCallActivity.CALL_TYPE, type);
                                        context.startActivity(intent);
                                    }
                                });

                                //goActivity(PlayerCallActivity.class,new UserChatData(targetUserId,requestObj.getData().getChannel_id(),""));
                            } else if (requestObj.getCode() == ApiConstantDefine.ApiCode.BALANCE_NOT_ENOUGH) {
                                Common.showRechargeDialog(context, requestObj.getMsg());
                                tipD.dismiss();
                            } else if (requestObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_USER_NOT_ONLINE
                                    || requestObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_USER_BUSY
                                    || requestObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_DO_NOT_DISTURB) {

                                tipD.dismiss();

                                String msg = "";

                                if (requestObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_DO_NOT_DISTURB) {
                                    msg = "对方开启勿扰";
                                } else if (requestObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_USER_BUSY) {
                                    msg = "对方忙碌中";
                                } else if (requestObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_USER_NOT_ONLINE) {
                                    msg = "对方不在线";
                                }

                                //自己是否是主播
                                if (StringUtils.toInt(SaveData.getInstance().getId()) != StringUtils.toInt(requestObj.getData().getAnchor_id())) {
                                    new QMUIDialog.MessageDialogBuilder(context)
                                            .setTitle(msg + "，是否预约？")
                                            .setMessage("预约需要支付一分钟的视频消费，若超过12小时主播没有回拨，消费将退回。")
                                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                                @Override
                                                public void onClick(QMUIDialog dialog, int index) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .addAction("确定", new QMUIDialogAction.ActionListener() {
                                                @Override
                                                public void onClick(QMUIDialog dialog, int index) {
                                                    Common.subscribeVideoCall(context, toUserId);
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                } else {
                                    ToastUtils.showLong("对方手机不在身边，请稍后再拨！");
                                }

                            } else {
                                tipD.dismiss();
                                LogUtils.i("拨打电话error:" + requestObj.getMsg());
                                ToastUtils.showLong(requestObj.getMsg());
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            tipD.dismiss();
                        }
                    }

            );
        } catch (Exception e) {
            LogUtils.i("exception:" + e.toString());
        }

    }

    //跳转私信页面
    public static void startPrivatePage(final Context context, String toUserId) {

        String uid = SaveData.getInstance().getId();
        String token = SaveData.getInstance().getToken();
        if (uid.equals(toUserId)) {
            ToastUtils.showLong("不能给自己发送私信消息!");
            return;
        }
        final QMUITipDialog tipD = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在获取信息...")
                .create();
        tipD.show();


        Api.doPrivateChat(uid, token, toUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("doPrivateChat", s);
                tipD.dismiss();
                JsonRequestDoPrivateChat jsonObj =
                        (JsonRequestDoPrivateChat) JsonRequestBase.getJsonObj(s, JsonRequestDoPrivateChat.class);
                if (jsonObj.getCode() == 1) {

                    if (jsonObj.getUser_info() == null) {
                        return;
                    }
                    ChatActivity.navToChat(context, jsonObj.getUser_info().getId(), jsonObj.getUser_info().getUser_nickname()
                            , jsonObj.getUser_info().getAvatar(), jsonObj.getIs_pay(), jsonObj.getPay_coin()
                            , jsonObj.getSex(), jsonObj.getIs_auth(), jsonObj.getFollow()
                            , TIMConversationType.C2C);
                } else {
                    ToastUtils.showLong(jsonObj.getMsg());
                }

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                tipD.dismiss();
                Log.e("doPrivateChat", e.toString());
            }
        });

    }

    //查看私照
    public static void requestSelectPic(final Context context, final String id) {

        Api.doRequestSelectPic(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), id, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestSelectPic jsonObj = (JsonRequestSelectPic) JsonRequestSelectPic.getJsonObj(s, JsonRequestSelectPic.class);
                if (jsonObj.getCode() == 1) {
                    PerViewImgActivity.startPerViewImg(context, jsonObj.getImg());

                } else if (jsonObj.getCode() == ApiConstantDefine.ApiCode.PHOTO_NOT_PAY) {

                    ShowPayPhotoDialog showPayPhotoDialog = new ShowPayPhotoDialog(context, id);
                    showPayPhotoDialog.show();
                } else {
                    ToastUtils.showShort(jsonObj.getMsg());
                }
            }
        });

    }

    //QQ客服
    public static void openCustomServiceQQ(Context context) {
        if (ConfigModel.getInitData().getCustom_service_qq() == null) {
            return;
        }
//        String url1 ="mqqwpa://im/chatme?chat_type=wpa&uin=" + ConfigModel.getInitData().getCustom_service_qq();
//
//        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
//
//        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        i1.setAction(Intent.ACTION_VIEW);
//
//        context.startActivity(i1);

        DialogHelp.getConfirmDialog(context, "客服QQ：" + ConfigModel.getInitData().getCustom_service_qq() + context.getString(R.string.click_copy), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ClipboardUtils.copyText(ConfigModel.getInitData().getCustom_service_qq());
                ToastUtils.showLong(R.string.copy_success);
            }
        }).show();
    }

    //预约主播
    public static void subscribeVideoCall(Context context, String toUserId) {

        final QMUITipDialog loadingDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在预约...")
                .create();
        loadingDialog.show();

        Api.doSubscribe(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), toUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                loadingDialog.dismiss();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    ToastUtils.showLong("预约成功，扣除一分钟的通话费用！");
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                loadingDialog.dismiss();
            }
        });
    }


}
