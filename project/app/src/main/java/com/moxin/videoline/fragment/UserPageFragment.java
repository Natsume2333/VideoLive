package com.moxin.videoline.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.ApiConstantDefine;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.helper.SelectResHelper;
import com.moxin.videoline.json.JsonGetIsAuth;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestUserCenterInfo;
import com.moxin.videoline.json.jsonmodle.UserCenterData;
import com.moxin.videoline.manage.RequestConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.msg.ui.AboutFansActivity;
import com.moxin.videoline.ui.CuckooAuthFormActivity;
import com.moxin.videoline.ui.EditActivity;
import com.moxin.videoline.ui.InviteActivityNew;
import com.moxin.videoline.ui.PrivatePhotoActivity;
import com.moxin.videoline.ui.RechargeVipActivity;
import com.moxin.videoline.ui.SettingActivity;
import com.moxin.videoline.ui.ShortVideoActivity;
import com.moxin.videoline.ui.ToJoinActivity;
import com.moxin.videoline.ui.VideoAuthActivity;
import com.moxin.videoline.ui.WealthActivity;
import com.moxin.videoline.ui.WealthDetailedActivity;
import com.moxin.videoline.ui.WebViewActivity;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.ui.common.LoginUtils;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.SharedPreferencesUtils;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.UIHelp;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.widget.BGLevelTextView;
import com.lzy.okgo.callback.StringCallback;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 我的
 */
public class UserPageFragment extends BaseFragment {
    private RelativeLayout user_page_my_user_page;
    private BGLevelTextView tv_level;

    private Dialog radioDialog;//分成比例dialog

    //显示数据
    private CircleImageView userImg;//用户头像
    private TextView userName;//用户名
    private ImageView userIsVerify;//用户是否验证图标

    private TextView aboutNumber;//关注人数
    private TextView fansNumber;//粉丝数

    private RelativeLayout ll_video_auth;
    private RelativeLayout ll_short_video;
    private RelativeLayout ll_private_photo;
    private RelativeLayout ll_invite;
    private RelativeLayout ll_new_guide;
    private RelativeLayout ll_cooperation;
    private RelativeLayout ll_setting;
    private RelativeLayout ll_level;
    private RelativeLayout ll_buyVip;
    private RelativeLayout ll_switch_disturb;

    @BindView(R.id.tv_user_page_id)
    TextView tv_user_page_id;

    @BindView(R.id.iv_switch_disturb)
    ImageView iv_switch_disturb;

    @BindView(R.id.ll_emcee_menu)
    LinearLayout ll_emcee_menu;

    @BindView(R.id.ll_buy_vip)
    RelativeLayout ll_buy_vip;

    @BindView(R.id.ll_guild)
    RelativeLayout llGuide;

    @BindView(R.id.ll_beauty_setting)
    RelativeLayout ll_beauty_setting;

    private UserCenterData userCenterData;//个人中心接口返回信息

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_userpage, container, false);
    }

    @Override
    protected void initView(View view) {
        userImg = view.findViewById(R.id.userpage_img);
        userName = view.findViewById(R.id.userpage_nickname);
        userIsVerify = view.findViewById(R.id.userpage_is_auth);
        aboutNumber = view.findViewById(R.id.love_number);
        fansNumber = view.findViewById(R.id.fans_number);

        ll_video_auth = view.findViewById(R.id.ll_video_auth);
        ll_short_video = view.findViewById(R.id.ll_short_video);
        ll_private_photo = view.findViewById(R.id.ll_private_photo);
        ll_invite = view.findViewById(R.id.ll_invite);
        ll_new_guide = view.findViewById(R.id.ll_new_guide);
        ll_cooperation = view.findViewById(R.id.ll_cooperation);
        ll_setting = view.findViewById(R.id.ll_setting);
        ll_level = view.findViewById(R.id.ll_level);
        ll_switch_disturb = view.findViewById(R.id.ll_switch_disturb);
        ll_buyVip = view.findViewById(R.id.ll_buy_vip);
        ll_buyVip.setOnClickListener(this);

        user_page_my_user_page = view.findViewById(R.id.userpage_myuserpage);
        tv_level = view.findViewById(R.id.tv_level);

    }

    @Override
    protected void initDate(View view) {

    }

    @Override
    protected void initSet(View view) {

        tv_user_page_id.setText(String.format(Locale.CHINA, "ID: %s", SaveData.getInstance().getId()));

        setOnclickListener(view, R.id.count_love_layout, R.id.count_fans_layout, R.id.count_divide_layout,
                R.id.ll_video_auth, R.id.ll_short_video, R.id.ll_private_photo, R.id.ll_invite, R.id.ll_new_guide,
                R.id.ll_cooperation, R.id.ll_setting, R.id.ll_level, R.id.ll_guild);

        user_page_my_user_page.setOnClickListener(this);

        if (StringUtils.toInt(ConfigModel.getInitData().getOpen_invite()) == 1) {
            ll_invite.setVisibility(View.VISIBLE);
        }

        ll_switch_disturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickChangeDoNotDisturbStatus(!userCenterData.isOpenDoNotDisturb());
            }
        });

        //显示美颜
        if (!TextUtils.isEmpty(ConfigModel.getInitData().getBogokj_beauty_sdk_key())
                && SaveData.getInstance().getUserInfo().getSex() == 2) {
            ll_beauty_setting.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initDisplayData(View view) {
    }


    @OnClick({R.id.ll_wallet, R.id.iv_user_center_sign, R.id.ll_beauty_setting})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //财富
            case R.id.ll_wallet:
                goMoneyPage();
                break;
            case R.id.ll_video_auth:

                clickVideoAuth();
                break;
            case R.id.ll_short_video:
                //小视频
                ShortVideoActivity.startShortVideoActivity(getContext());
                break;
            case R.id.ll_private_photo:
                //私照
                PrivatePhotoActivity.startPrivatePhotoActivity(getContext(), uId, "", 0);
                break;
            case R.id.ll_level:
                //我的等级
                //WebViewActivity.openH5Activity(getContext(), true, getString(R.string.my_level), RequestConfig.getConfigObj().getMyLevelUrl());
                WealthDetailedActivity.start(getContext(), WealthDetailedActivity.TYPE_MY_LEVEL);
                break;
            case R.id.ll_new_guide:
                //新手引导
                WebViewActivity.openH5Activity(getContext(), false, getString(R.string.novice_guide), RequestConfig.getConfigObj().getNewBitGuideUrl());
                break;

            case R.id.ll_setting:{
                //设置
                Intent intent = new Intent(getContext(), SettingActivity.class);
                if (userCenterData != null) {
                    intent.putExtra("state", userCenterData.getUser_auth_status());
                    intent.putExtra("sex", userCenterData.getSex());
                }
                getContext().startActivity(intent);
            }
                break;
            case R.id.ll_cooperation: {
                Intent intent = new Intent(getContext(), ToJoinActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.ll_invite:
                //邀请好友
//                InviteActivity.startInviteAcitivty(getContext());
//                WebViewActivity.openH5Activity(getContext(), true, getString(R.string.inviting_friends), ConfigModel.getInitData().getApp_h5().getInvite_share_menu());
                InviteActivityNew.start(getContext());
                break;

            case R.id.mine_ed:
                goActivity(getContext(), EditActivity.class);
                break;
            //个人主页
            case R.id.userpage_myuserpage:
                goMyUserPage();
                break;
            //关注
            case R.id.count_love_layout:
                goMsgListPage(0);
                break;
            //粉丝
            case R.id.count_fans_layout:
                goMsgListPage(1);
                break;
            case R.id.count_divide_layout:
                //showDialogRatio();
                break;
            case R.id.dialog_close:
                radioDialog.dismiss();
                break;
            case R.id.ll_buy_vip:
                Intent intentVip = new Intent(getContext(), RechargeVipActivity.class);
                startActivity(intentVip);
//                WebViewActivity.openH5Activity(getContext(), true, getString(R.string.vip), ConfigModel.getInitData().getApp_h5().getVip_url());
                break;
            case R.id.ll_guild:
                clickGuild();
                break;

            case R.id.iv_user_center_sign:
                WebViewActivity.openH5Activity(getContext(), true, getString(R.string.sign), ConfigModel.getInitData().getApp_h5().getSign_in());
//                intent = new Intent(getContext(), FriendsActivity.class);
//                startActivity(intent);
                break;

            //美颜设置
            case R.id.ll_beauty_setting:
                UIHelp.showBeautySettingPage(getContext());
                break;
            default:
                break;
        }
    }

    //工会
    private void clickGuild() {
        DialogHelp.getSelectDialog(getContext(), new String[]{"申请公会"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    UIHelp.showGuildList(getContext());
                }

//                else if (i == 1 && userCenterData != null && userCenterData.getIs_president() != 1) {
//                    UIHelp.showGuildManageActivity(getContext());
//                }

//                else if (i == 2 && userCenterData != null && userCenterData.getIs_president() == 1) {
//                    UIHelp.showGuildCreateActivity(getContext());
//                }

                if (i == 2 && userCenterData != null && userCenterData.getIs_president() != 1) {
                    ToastUtils.showLong("没有创建公会！");
                }
            }
        }).show();
    }


    //视频认证
    private void clickVideoAuth() {

//        if(StringUtils.toInt(userCenterData.getSex()) == 1){
//            ToastUtils.showLong("男性用户无法认证!");
//            return;
//        }

        if (userCenterData == null) {
            return;
        }

        if (ConfigModel.getInitData().getAuth_type() == 1) {
            if (StringUtils.toInt(userCenterData.getUser_auth_status()) == 0) {
                ToastUtils.showLong("正在等待审核！");
                return;
            }
            Intent intent = new Intent(getContext(), VideoAuthActivity.class);
            intent.putExtra(CuckooAuthFormActivity.STATUS, StringUtils.toInt(userCenterData.getUser_auth_status()));
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), CuckooAuthFormActivity.class);
            intent.putExtra(CuckooAuthFormActivity.STATUS, StringUtils.toInt(userCenterData.getUser_auth_status()));
            startActivity(intent);
        }

//        switch (StringUtils.toInt(userCenterData.getUser_auth_status())) {
//
//            case -1: {
//                Intent intent = new Intent(getContext(), CuckooAuthFormActivity.class);
//                startActivity(intent);
//            }
//            break;
//            case 1: {
//                ToastUtils.showLong("视频认证已通过");
//                break;
//            }
//            case 2: {
//                Intent intent = new Intent(getContext(), CuckooAuthFormActivity.class);
//                startActivity(intent);
//            }
//            break;
//            case 0:
//
//                ToastUtils.showLong("正在审核中...");
//                break;
//            default:
//
//                break;
//        }
    }

    /**
     * 显示分成比例对话框
     */
    private void showDialogRatio() {
        radioDialog = showViewDialog(getContext(), R.layout.dialog_ratio_view, new int[]{R.id.dialog_close, R.id.dialog_left_btn, R.id.dialog_right_btn});
        TextView text = radioDialog.findViewById(R.id.radio_radio_text);
        text.setText(userCenterData.getSplit());
    }


    //修改免打扰状态
    private void clickChangeDoNotDisturbStatus(final boolean b) {

        int type = b ? 1 : 2;
        Api.doRequestSetDoNotDisturb(type, SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestBase jsonObj = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (jsonObj.getCode() == 1) {

                    UserModel userModel = SaveData.getInstance().getUserInfo();
                    userModel.setIs_open_do_not_disturb(b ? "1" : "0");
                    SaveData.getInstance().saveData(userModel);

                    userCenterData.setIs_open_do_not_disturb(userModel.getIs_open_do_not_disturb());
                    if (StringUtils.toInt(userModel.getIs_open_do_not_disturb()) == 1) {
                        iv_switch_disturb.setImageResource(R.mipmap.me_icon_disturb_off);
                    } else {
                        iv_switch_disturb.setImageResource(R.mipmap.me_icon_disturb_on);
                    }

                } else {
                    showToastMsg(getContext(), jsonObj.getMsg());
                }
            }
        });
    }

    /**
     * 刷新用户资料页面显示
     */
    private void refreshUserData() {

        Utils.loadHeadHttpImg(getContext(), Utils.getCompleteImgUrl(userCenterData.getAvatar()), userImg);

        userName.setText(userCenterData.getUser_nickname());
        tv_level.setLevelInfo(userCenterData.getSex(), userCenterData.getLevel());
        //是否认证标识
        userIsVerify.setImageResource(SelectResHelper.getAttestationRes(StringUtils.toInt(userCenterData.getUser_auth_status())));
        aboutNumber.setText(userCenterData.getAttention_all());
        fansNumber.setText(userCenterData.getAttention_fans());

        if (StringUtils.toInt(userCenterData.getIs_open_do_not_disturb()) == 1) {
            iv_switch_disturb.setImageResource(R.mipmap.me_icon_disturb_off);
        } else {
            iv_switch_disturb.setImageResource(R.mipmap.me_icon_disturb_on);
        }

        if (userCenterData.getSex() == 2) {
            ll_emcee_menu.setVisibility(View.VISIBLE);
            llGuide.setVisibility(View.VISIBLE);
        }

        UserModel userModel = SaveData.getInstance().getUserInfo();
        userModel.setSex(userCenterData.getSex());
        userModel.setLevel(userCenterData.getLevel());
        userModel.setUser_nickname(userCenterData.getUser_nickname());
        SaveData.getInstance().saveData(userModel);

        if (!userCenterData.isMan()) {
            userIsVerify.setVisibility(View.VISIBLE);
            ll_video_auth.setVisibility(View.VISIBLE);
            ll_buy_vip.setVisibility(View.GONE);
        } else {
            userIsVerify.setVisibility(View.GONE);
            ll_video_auth.setVisibility(View.GONE);
            ll_buy_vip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 服务端请求用户数据
     */
    private void requestUserData() {
        Api.getUserDataByMe(
                uId,
                uToken,
                new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JsonRequestUserCenterInfo jsonRequestUserCenterInfo = JsonRequestUserCenterInfo.getJsonObj(s);
                        if (jsonRequestUserCenterInfo.getCode() == 1) {

                            userCenterData = jsonRequestUserCenterInfo.getData();
                            UserModel userModel = SaveData.getInstance().getUserInfo();
                            userModel.setIs_open_do_not_disturb(userCenterData.getIs_open_do_not_disturb());
                            userModel.setAvatar(userCenterData.getAvatar());
                            userModel.setUser_nickname(userCenterData.getUser_nickname());
                            SaveData.getInstance().saveData(userModel);

                            //这里只有实名了的女性 预约我的  其他 我的预约
                            if (userCenterData.getSex() == 2 && StringUtils.toInt(userCenterData.getUser_auth_status()) == 1) {
                                SharedPreferencesUtils.setParam(getContext(), "AccountNature", "anchor");
                            } else {
                                SharedPreferencesUtils.setParam(getContext(), "AccountNature", "boss");
                            }

                            refreshUserData();

                        } else if (jsonRequestUserCenterInfo.getCode() == ApiConstantDefine.ApiCode.LOGIN_INFO_ERROR) {

                            new MaterialDialog.Builder(getContext())
                                    .content("您的账号已经在其他手机登陆，即将退出此账号")
                                    .cancelable(false)
                                    .positiveText(R.string.agree_ok)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            doLogout();

                                        }
                                    })
                                    .show();


                        } else {
                            showToastMsg(getContext(), jsonRequestUserCenterInfo.getMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastUtils.showLong("刷新用户数据失败!");
                    }
                }
        );
    }


    /**
     * 前往消息列表页面##0关注页--1粉丝页
     *
     * @param i 标识
     */
    private void goMsgListPage(int i) {
        if (i == 0) {
            //关注
            goActivity(getContext(), AboutFansActivity.class, getString(R.string.follow));
        } else {
            //粉丝
            goActivity(getContext(), AboutFansActivity.class, getString(R.string.fans));
        }
    }

    /**
     * 执行购买聊币操作
     *
     * @param money 购买的数量(单位/元[人民币])
     */
    private void goBuyMoney(int money) {
        showToastMsg(getContext(), getString(R.string.buy) + money * 100 + currency);
    }

    /**
     * 跳转到个人主页
     */
    private void goMyUserPage() {
        Common.jumpUserPage(getContext(), uId);
    }

    /**
     * 跳转到个人财富主页
     */
    private void goMoneyPage() {
        Api.doRequestGetIsAuth(uId, uToken, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetIsAuth data = (JsonGetIsAuth) JsonRequestBase.getJsonObj(s, JsonGetIsAuth.class);
                if (data.getIs_auth() == 1) {
                    //我的钱包
                    //UIHelp.showIncomePage(getContext());
                    WealthActivity.startWealthActivity(getContext());
                } else {
                    WealthActivity.startWealthActivity(getContext());
                }
            }
        });
        // UIHelp.showIncomePage(getContext());
    }

    /**
     * 退出/注销方法
     */
    private void doLogout() {
        LoginUtils.doLoginOut(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        requestUserData();//服务端请求用户数据并设置到页面
    }
}
