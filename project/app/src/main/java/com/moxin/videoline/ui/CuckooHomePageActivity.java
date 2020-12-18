package com.moxin.videoline.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.CuckooGuardianOrderListAdapter;
import com.moxin.videoline.adapter.FragAdapter;
import com.moxin.videoline.adapter.recycler.RecycleUserHomeGiftAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.fragment.CuckooHomePageImageFragment;
import com.moxin.videoline.fragment.CuckooHomePageUserInfoFragment;
import com.moxin.videoline.fragment.CuckooHomePageVideoFragment;
import com.moxin.videoline.fragment.DynamicMyFragment;
import com.moxin.videoline.helper.SelectResHelper;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequest;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestTarget;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.manage.RequestConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.CanShowContactBean;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.HomePageImgBean;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.ui.dialog.QuickDialog;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.utils.im.IMHelp;
import com.moxin.videoline.widget.NoScrollViewPager;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.stx.xhb.xbanner.XBanner;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

/***
 * 新的个人主页
 */
public class CuckooHomePageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;

    @BindView(R.id.tv_user_id)
    TextView tv_user_id;

    @BindView(R.id.rv_guardian_order)
    RecyclerView rv_guardian_order;

    @BindView(R.id.rl_guardian)
    RelativeLayout rl_guardian;

    @BindView(R.id.tv_text_money_minute)
    TextView textChatMoney;

    @BindView(R.id.tv_voice_money_minute)
    TextView voiceChatMoney;

    @BindView(R.id.tv_video_money_minute)
    TextView videoChatMoney;

    @BindView(R.id.iv_auth_status_tv)
    TextView iv_auth_statusTv;

    @BindView(R.id.iv_level_sex)
    ImageView iv_level_sex;

    @BindView(R.id.topbar)
    View top;

    @BindView(R.id.home_page_auth_ll)
    LinearLayout home_page_auth_ll;

    @BindView(R.id.home_page_level_ll)
    LinearLayout home_page_level_ll;

    @BindView(R.id.home_page_local_ll)
    LinearLayout home_page_local_ll;

    @BindView(R.id.hoem_page_rank_iv)
    ImageView rankIv;

    @BindView(R.id.chat_attribute_card_view)
    LinearLayout chatCardView;

    @BindView(R.id.userpage_img)
    CircleImageView cardCircleView;

    @BindView(R.id.top_title_tv)
    TextView cardNameTv;

    @BindView(R.id.tv_btn_info_view)
    View infoView;

    @BindView(R.id.tv_btn_video_view)
    View videoView;

    @BindView(R.id.tv_btn_img_view)
    View imgView;

    @BindView(R.id.contact_rl)
    LinearLayout ll_contact_rl;

    private FragAdapter mInfoTabFragAdapter;
    private String targetUserId;//目标用户id
    private TargetUserData targetUserData;//当前目标用户信息

    //轮播图

    private ArrayList<HomePageImgBean> rollPath = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList();

    private QuickDialog rankDialog;

    private TextView maxLevelText;//当前最高等级
    private TextView userNickname; //当前player名称
    private TextView userTimeText; //通话x小时
    private TextView userGoodText;//好评
    private TextView userLocationText;//当前player位置
    //    private TextView userIsonLineText;//当前player是否在线
    private TextView fansNumber;//粉丝数量
    private TextView listBarGiftText;//礼物数量
    private TextView listBarVideoText;//小视频数量
    private TextView listBarPhotoText;//私照数量
    private TextView tvVideoMoney;
    private TextView tv_level;
    private ImageView userIsonLine;//是否在线图标
    private ImageView iv_auth_status;//是否认证
    private XBanner homePageWallpaper;//轮播组件
    private ImageView userLoveMe;//关注这个player

    private Dialog menuDialog;
    private TextView infoTv;
    private TextView videoTv, dynamicTv;
    private LinearLayout chatLl;
    private TextView myPrivateImg;

    private int[] actionMenuIds;

    private RecyclerView listBarGiftList;//礼物列表
    private RecycleUserHomeGiftAdapter recycleUserHomeGiftAdapter;

    private List<UserModel> guardianOrderList = new ArrayList<>();
    private CuckooGuardianOrderListAdapter cuckooGuardianOrderListAdapter;

    //收到的礼物列表
    private ArrayList<TargetUserData.GiftBean> giftList = new ArrayList<>();

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_home_page;
    }


    @Override
    protected void initView() {

        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        Utils.initTransP(top);
        userIsonLine = findViewById(R.id.userinfo_bar_isonLine);
        iv_auth_status = findViewById(R.id.iv_auth_status);
        userNickname = findViewById(R.id.userinfo_bar_userid);
        userTimeText = findViewById(R.id.userinfo_bar_time_text);
        userGoodText = findViewById(R.id.userinfo_bar_good_text);
        userLocationText = findViewById(R.id.userinfo_bar_location_text);
//        userIsonLineText = findViewById(R.id.userinfo_bar_isonLine_text);
        fansNumber = findViewById(R.id.fans_number);
        //listBarVideoList = findViewById(R.id.list_bar_video_list);
        //listBarPhotoList = findViewById(R.id.list_bar_photo_list);
        listBarGiftText = findViewById(R.id.list_bar_gift_text);
        listBarVideoText = findViewById(R.id.list_bar_video_text);
        listBarPhotoText = findViewById(R.id.list_bar_photo_text);
        userLoveMe = findViewById(R.id.userinfo_bar_loveme);
        userLoveMe.bringToFront();
        //homePageWallpaper = findViewById(R.id.home_page_wallpaper);
        tv_level = findViewById(R.id.tv_level);
        maxLevelText = findViewById(R.id.userinfo_bar_max_number);
        tvVideoMoney = findViewById(R.id.tv_video_money);
        homePageWallpaper = findViewById(R.id.home_page_wallpaper);
        infoTv = findViewById(R.id.tv_btn_info);
        videoTv = findViewById(R.id.tv_btn_video);
        chatLl = findViewById(R.id.chat_ll);
        dynamicTv = findViewById(R.id.tv_btn_dynamic);
        myPrivateImg = findViewById(R.id.tv_btn_img);

        listBarGiftList = findViewById(R.id.list_bar_gift_list);


        //设置收到的礼物列表
        LinearLayoutManager listGiftLayoutManage = new LinearLayoutManager(this);
        listGiftLayoutManage.setOrientation(LinearLayoutManager.HORIZONTAL);
        listBarGiftList.setLayoutManager(listGiftLayoutManage);

        recycleUserHomeGiftAdapter = new RecycleUserHomeGiftAdapter(this, giftList);
        listBarGiftList.setAdapter(recycleUserHomeGiftAdapter);

        LinearLayoutManager guardianOrderManager = new LinearLayoutManager(getNowContext());
        guardianOrderManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_guardian_order.setLayoutManager(guardianOrderManager);
        cuckooGuardianOrderListAdapter = new CuckooGuardianOrderListAdapter(guardianOrderList);
        rv_guardian_order.setAdapter(cuckooGuardianOrderListAdapter);


        initXbanner();

        viewPager.setNoScroll(true);

        selectItem(15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black);
    }


    private void initXbanner() {
        //设置图片加载器
        homePageWallpaper.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ImageView tvContent = (ImageView) view.findViewById(R.id.custom_imageview_layout);
                HomePageImgBean bannerImgBean = (HomePageImgBean) model;
                Utils.loadHttpImg(bannerImgBean.getUrl(), tvContent);

            }
        });

    }


    @Override
    protected void initSet() {

        fragmentList.add(CuckooHomePageUserInfoFragment.getInstance(targetUserId));
        fragmentList.add(CuckooHomePageVideoFragment.getInstance(targetUserId));
        fragmentList.add(CuckooHomePageImageFragment.getInstance(targetUserId));
        fragmentList.add(DynamicMyFragment.getInstance(targetUserId));

        viewPager.setOffscreenPageLimit(1);
        mInfoTabFragAdapter = new FragAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(mInfoTabFragAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    protected void initData() {

        targetUserId = getIntent().getStringExtra("str");
        tv_user_id.setText("ID: " + targetUserId);
        requestTargetUserData();
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.hoem_page_rank_iv, R.id.list_bar_gift_text, R.id.rl_guardian, R.id.rl_voice_call, R.id.tv_btn_info, R.id.tv_btn_video, R.id.float_back, R.id.float_meun,
            R.id.ll_chat, R.id.ll_gift, R.id.rl_call, R.id.userinfo_bar_loveme, R.id.tv_btn_img, R.id.tv_btn_dynamic, R.id.home_page_qq_contact_ll, R.id.home_page_wechat_contact_ll
            , R.id.home_page_phone_contact_ll})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.userinfo_bar_loveme:
                loveThisPlayer();
                break;
            case R.id.tv_btn_info:
                selectItem(15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_btn_video:
                selectItem(13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_btn_img:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black);
                viewPager.setCurrentItem(2);
                break;

            case R.id.tv_btn_dynamic:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color);
                viewPager.setCurrentItem(3);
                break;
            //贡献榜按钮
            case R.id.hoem_page_rank_iv:
                showContribution();
                break;
            case R.id.float_back:
                finish();
                break;
            //举报
            case R.id.float_meun:
                if (targetUserData == null) {
                    return;
                }
                showFloatMeun();
                break;

            //修改资料
            case R.id.edit_mine:
                Intent intent = new Intent(this, EditActivity.class);
                startActivity(intent);
                menuDialog.dismiss();
                break;

            //加入黑名单操作
            case R.id.join_black_list:
                clickBlack();
                menuDialog.dismiss();
                break;
            //举报该用户操作
            case R.id.report_this_user:
                clickReport();
                menuDialog.dismiss();
                break;
            //取消操作
            case R.id.dialog_dis:
                menuDialog.dismiss();
                break;
            case R.id.ll_chat:
                showChatPage(false);
                break;
            case R.id.ll_gift:
                showChatPage(true);
                break;
            //视频通话
            case R.id.rl_call:
                callThisPlayer();
                break;
            case R.id.rl_voice_call:
                callVoice();
                break;
            case R.id.rl_guardian:
                openGuardianPage();
                break;

            case R.id.list_bar_gift_text:
                Intent intentGift = new Intent(this, CuckooGiftCabinetGiftListActivity.class);
                intentGift.putExtra(CuckooGiftCabinetGiftListActivity.TO_USER_ID, targetUserId);
                startActivity(intentGift);
                break;

            //查看 qq WeChat 手机号
            case R.id.home_page_qq_contact_ll:

                isCanShowContact("qq");
                break;
            case R.id.home_page_phone_contact_ll:
                isCanShowContact("phone");
                break;
            case R.id.home_page_wechat_contact_ll:
                isCanShowContact("wx");
                break;

            default:
                break;
        }
    }

    private void isCanShowContact(final String type) {
        Api.getCanShowContactResult(targetUserId, type, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("getCanShowContactResult", s);
                CanShowContactBean bean = new Gson().fromJson(s, CanShowContactBean.class);
                //后台用的0
                if (bean.getCode() == 1) {
                    String price = bean.getPrice();
                    String number = bean.getNumber();


                    //购买价格如果有值就请求购买接口
                    if (!TextUtils.isEmpty(price)) {
                        new MaterialDialog.Builder(CuckooHomePageActivity.this)
                                .content("是否购买主播的联系方式?")
                                .positiveText(R.string.agree)
                                .negativeText(R.string.disagree)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        toBuyContact(type);
                                    }
                                })
                                .show();
                    } else {
                        new MaterialDialog.Builder(CuckooHomePageActivity.this)
                                .content(type + ":" + number)
                                .positiveText(R.string.agree)
                                .negativeText(R.string.disagree)
                                .show();
                    }

                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("getCanShowContactResult", e.toString());
            }
        });
    }

    /**
     * 购买联系方式
     *
     * @param type
     */
    private void toBuyContact(final String type) {
        Api.toBuyContact(targetUserId, type, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("toBuyContact", s);
                CanShowContactBean bean = new Gson().fromJson(s, CanShowContactBean.class);
                //后台用的0
                if (bean.getCode() == 1) {
                    String number = bean.getNumber();
                    new MaterialDialog.Builder(CuckooHomePageActivity.this)
                            .content(type + ":" + number)
                            .positiveText(R.string.agree)
                            .negativeText(R.string.disagree)
                            .show();
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("toBuyContact", e.toString());
            }
        });
    }

    //守护页面
    private void openGuardianPage() {
        WebViewActivity.openH5Activity(CuckooHomePageActivity.this, true, "守护", ConfigModel.getInitData().getApp_h5().getGuardian_list() + "?hostid=" + targetUserId);
    }

    private void selectItem(int infoTvSize, boolean infoTvStyle, int infoColor, int videoTvSize, boolean videoTvStyle, int videoColor, int myPrivateImgSize, boolean myPrivateImgStyle, int myPrivateImgColor, int myDynamicImgSize, boolean myDynamicImgStyle, int myDynamicImgColor) {
        infoTv.setTextSize(infoTvSize);
        TextPaint tp1 = infoTv.getPaint();
        tp1.setFakeBoldText(infoTvStyle);
        infoTv.setTextColor(getResources().getColor(infoColor));

        videoTv.setTextSize(videoTvSize);
        TextPaint tpv1 = videoTv.getPaint();
        tpv1.setFakeBoldText(videoTvStyle);
        videoTv.setTextColor(getResources().getColor(videoColor));

        myPrivateImg.setTextSize(myPrivateImgSize);
        TextPaint tpvImg = myPrivateImg.getPaint();
        tpvImg.setFakeBoldText(myPrivateImgStyle);
        myPrivateImg.setTextColor(getResources().getColor(myPrivateImgColor));


        dynamicTv.setTextSize(myDynamicImgSize);
        TextPaint tpvDy = dynamicTv.getPaint();
        tpvDy.setFakeBoldText(myDynamicImgStyle);
        dynamicTv.setTextColor(getResources().getColor(myDynamicImgColor));
    }

    //给这个player打电话
    private void callThisPlayer() {
        showToastMsg(getString(R.string.now_call));
        Common.callVideo(this, targetUserId, 0);
    }

    //拨打音频电话
    private void callVoice() {
        showToastMsg(getString(R.string.now_call));
        Common.callVideo(this, targetUserId, 1);
    }

    //显示聊天页面
    private void showChatPage(boolean isShowGift) {
        Common.startPrivatePage(this, targetUserId);
    }

    //显示菜单
    private void showFloatMeun() {

        //是自己的个人主页
        if (SaveData.getInstance().getId().equals(targetUserId)) {
            actionMenuIds = new int[]{R.id.edit_mine, R.id.join_black_list, R.id.report_this_user, R.id.dialog_dis};
            menuDialog = showButtomDialog(R.layout.dialog_float_meun, actionMenuIds, 20);
            menuDialog.findViewById(R.id.join_black_list).setVisibility(View.GONE);
            menuDialog.findViewById(R.id.report_this_user).setVisibility(View.GONE);
        } else {
            actionMenuIds = new int[]{R.id.join_black_list, R.id.report_this_user, R.id.dialog_dis};
            menuDialog = showButtomDialog(R.layout.dialog_float_meun_no_edit, actionMenuIds, 20);
        }


        TextView tv = menuDialog.findViewById(R.id.join_black_list);

        if (targetUserData.getIs_black() == 1) {
            tv.setText(R.string.relieve_black);
        }
        menuDialog.show();

    }

    //显示贡献榜
    private void showContribution() {
        //  Intent intent = new Intent(this, UserContribuionRankActivity.class);
        if (rankDialog == null) rankDialog = new QuickDialog(this).add("贡献榜").add("守护榜")
                .addCallBack(new QuickDialog.CallBack() {
                    @Override
                    public void onClick(String item, int pos) {
                        Intent intent = null;
                        switch (pos) {
                            case 0:
                                intent = new Intent(CuckooHomePageActivity.this, UserContribuionRankActivity.class);
                                break;
                            case 1:
                                intent = new Intent(CuckooHomePageActivity.this, UserGuardRankActivity.class);
                                break;
                        }

                        intent.putExtra(UserContribuionRankActivity.TO_USER_ID, targetUserId);
                        startActivity(intent);
                    }
                });

        rankDialog.show();
    }

    //举报
    private void clickReport() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(ReportActivity.REPORT_USER_ID, targetUserId);
        startActivity(intent);
    }


    /**
     * 初始化显示页面数据
     */
    private void initDisplayData() {

        if (StringUtils.toInt(targetUserData.getIs_visible_bottom_btn()) == 0) {
            chatLl.setVisibility(View.GONE);
        } else {
            chatLl.setVisibility(View.VISIBLE);

            if (targetUserData.getSex() == 2) {
                //显示打电话弹窗
                TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);
                showAnim.setDuration(500);
                chatCardView.startAnimation(showAnim);
                chatCardView.setVisibility(View.VISIBLE);
            }

        }

        //排行榜
        if (targetUserData.getSex() == 2) {
            ll_contact_rl.setVisibility(View.VISIBLE);

            rankIv.setVisibility(View.VISIBLE);
            voiceChatMoney.setVisibility(View.VISIBLE);
            videoChatMoney.setVisibility(View.VISIBLE);

            //显示视频私照动态
            hideBtn(View.VISIBLE);

        } else {
            rankIv.setVisibility(View.GONE);
            voiceChatMoney.setVisibility(View.GONE);
            videoChatMoney.setVisibility(View.GONE);

            //隐藏视频私照动态
            hideBtn(View.GONE);
        }


        if (!TextUtils.isEmpty(targetUserData.getLevel())) {
            home_page_level_ll.setVisibility(View.VISIBLE);
            //初始化显示页面数据
            if (targetUserData.getSex() == 1) {
                tv_level.setText("V " + targetUserData.getLevel());
            } else {
                tv_level.setText("M " + targetUserData.getLevel());
            }
        } else {
            home_page_level_ll.setVisibility(View.GONE);
        }

        //卡片文案
        cardNameTv.setText(targetUserData.getUser_nickname() + "");
        Utils.loadHttpImg(this, targetUserData.getAvatar(), cardCircleView);


        iv_level_sex.setBackgroundResource(targetUserData.getSex() == 2 ? R.mipmap.woman_vip_bac : R.mipmap.man_vip_bac);
        //设置是否显示关注
        userLoveMe.setBackgroundResource(StringUtils.toInt(targetUserData.getAttention()) == 1 ? R.mipmap.followed_bat : R.mipmap.follow_bat);

        userNickname.setText(targetUserData.getUser_nickname());
        userTimeText.setText(getString(R.string.call) + targetUserData.getCall());
        userGoodText.setVisibility(View.GONE);
//        userGoodText.setText(getString(R.string.praise) + targetUserData.getEvaluation());

        if (!TextUtils.isEmpty(targetUserData.getAddress())) {
            home_page_local_ll.setVisibility(View.VISIBLE);
            userLocationText.setText(targetUserData.getAddress());
        } else {
            home_page_local_ll.setVisibility(View.GONE);
        }


        if (StringUtils.toInt(targetUserData.getUser_status()) == 1) {
            videoChatMoney.setText(targetUserData.getVideo_deduction() + RequestConfig.getConfigObj().getCurrency() + getString(R.string.minute));
            voiceChatMoney.setText(targetUserData.getVoice_deduction() + RequestConfig.getConfigObj().getCurrency() + getString(R.string.minute));

        }

        int attestationResForSex = SelectResHelper.getAttestationResForSex(targetUserData.getSex(), StringUtils.toInt(targetUserData.getUser_status()));
        if (attestationResForSex == 0) {
            home_page_auth_ll.setVisibility(View.GONE);
        } else {
            home_page_auth_ll.setVisibility(View.VISIBLE);
            iv_auth_status.setImageResource(attestationResForSex);
        }

        if (targetUserData.getSex() == 2) {
            iv_auth_statusTv.setText(StringUtils.toInt(targetUserData.getUser_status()) == 1 ? "已认证" : "未认证");
        } else {
            iv_auth_statusTv.setText(StringUtils.toInt(targetUserData.getUser_status()) == 1 ? "已认证" : "");

        }

        fansNumber.setText(getString(R.string.fans) + ":" + targetUserData.getAttention_fans());

        //是否在线
        userIsonLine.setImageResource(StringUtils.toInt(targetUserData.getIs_online()) == 1 ? R.mipmap.on_line : R.mipmap.not_online);

        if (targetUserData.getImg() != null) {
            for (TargetUserData.ImgBean img : targetUserData.getImg()) {
                HomePageImgBean homePageImgBean = new HomePageImgBean();
                homePageImgBean.setUrl(Utils.getCompleteImgUrl(img.getImg()));
                rollPath.add(homePageImgBean);
            }
            if (targetUserData.getImg().size() == 0) {
                HomePageImgBean homePageImgBean = new HomePageImgBean();
                homePageImgBean.setUrl(Utils.getCompleteImgUrl(Utils.getCompleteImgUrl(targetUserData.getAvatar())));
                rollPath.add(homePageImgBean);
            }
        }

        if (targetUserData.getSex() == 2) {
            listBarGiftText.setText(String.format(Locale.CHINA, "收到的礼物(%d)", targetUserData.getGift_count()));
        } else {
            listBarGiftText.setText(String.format(Locale.CHINA, "送出的礼物(%d)", targetUserData.getGift_count()));
        }


        //填充收到的礼物列表
        if (targetUserData.getGift() != null) {
            giftList.addAll(targetUserData.getGift());
            recycleUserHomeGiftAdapter.notifyDataSetChanged();
        }

        //填充收到的礼物列表
//        if(targetUserData.getGift() != null){
//            giftList.addAll(targetUserData.getGift());
//            recycleUserHomeGiftAdapter.notifyDataSetChanged();
//        }
//
//        //填充发布的视频列表
//        if(targetUserData.getVideo() != null){
//            videoList.addAll(targetUserData.getVideo());
//            recycleUserHomePhotoAdapter.notifyDataSetChanged();
//        }
//        //填充发布的私照列表
//        if(targetUserData.getPictures() != null){
//            photoList.addAll(targetUserData.getPictures());
//            recycleUserHomeVideoAdapter.notifyDataSetChanged();
//        }

        homePageWallpaper.setBannerData(R.layout.banner_custom_layout, rollPath);
        homePageWallpaper.startAutoPlay();


        guardianOrderList.clear();
        guardianOrderList.addAll(targetUserData.getGuardian_user_list());
        cuckooGuardianOrderListAdapter.notifyDataSetChanged();

    }

    private void hideBtn(int visible) {
        videoTv.setVisibility(visible);
        myPrivateImg.setVisibility(visible);
        dynamicTv.setVisibility(visible);

        infoView.setVisibility(visible);
        videoView.setVisibility(visible);
        imgView.setVisibility(visible);
    }

    //关注这个player
    private void loveThisPlayer() {
        Api.doLoveTheUser(
                targetUserId,
                uId,
                uToken,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        super.onSuccess(s, call, response);
                        JsonRequest requestObj = JsonRequest.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            if (StringUtils.toInt(targetUserData.getAttention()) == 1) {
                                targetUserData.setAttention("0");
                            } else {
                                targetUserData.setAttention("1");
                            }
                            userLoveMe.setBackgroundResource(StringUtils.toInt(targetUserData.getAttention()) == 1 ? R.mipmap.followed_bat : R.mipmap.follow_bat);
                        }
                    }
                }
        );
    }


    /**
     * 获取目标用户基础信息
     */
    private void requestTargetUserData() {

        Api.getUserHomePageInfo(
                targetUserId,
                uId,
                uToken,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JsonRequestTarget jsonTargetUser = JsonRequestTarget.getJsonObj(s);
                        if (jsonTargetUser.getCode() == 1) {
                            targetUserData = jsonTargetUser.getData();
                            initDisplayData();
                        } else {
                            //请求失败
                            showToastMsg(jsonTargetUser.getMsg());
                        }
                    }
                }
        );
    }


    //拉黑
    private void clickBlack() {

        showLoadingDialog(getString(R.string.loading_action));
        Api.doRequestBlackUser(uId, uToken, targetUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase jsonObj = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (jsonObj.getCode() == 1) {
                    showToastMsg(getResources().getString(R.string.action_success));
                    if (targetUserData.getIs_black() == 1) {
                        targetUserData.setIs_black(0);
                        IMHelp.deleteBlackUser(targetUserId, new TIMValueCallBack<List<TIMFriendResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                LogUtils.i("解除拉黑用户失败:" + s);
                            }

                            @Override
                            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                                LogUtils.i("解除拉黑用户成功");
                            }
                        });
                    } else {
                        targetUserData.setIs_black(1);
                        IMHelp.addBlackUser(targetUserId, new TIMValueCallBack<List<TIMFriendResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                LogUtils.i("拉黑用户失败:" + s);
                            }

                            @Override
                            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                                LogUtils.i("拉黑用户成功");
                            }
                        });
                    }
                } else {
                    showToastMsg(jsonObj.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                hideLoadingDialog();
            }
        });
    }

    /**
     * viewpager滑动监听
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                selectItem(15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(0);
                break;
            case 1:
                selectItem(13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black, 13, false, R.color.black);
                viewPager.setCurrentItem(1);
                break;
            case 2:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color, 13, false, R.color.black);
                viewPager.setCurrentItem(2);
                break;

            case 3:
                selectItem(13, false, R.color.black, 13, false, R.color.black, 13, false, R.color.black, 15, true, R.color.admin_color);
                viewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onStop() {
        super.onStop();
        homePageWallpaper.stopAutoPlay();
    }
}
