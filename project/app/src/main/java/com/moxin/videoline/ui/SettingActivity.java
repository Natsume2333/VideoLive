package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.manage.RequestConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.ui.common.LoginUtils;
import com.moxin.videoline.utils.CuckooSharedPreUtil;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.UIHelp;
import com.lzy.okgo.callback.StringCallback;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 魏鹏
 * email 1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 * @dw 设置
 */
public class SettingActivity extends BaseActivity {

    private QMUITopBar qmuiTopBar;
    private QMUIGroupListView groupListView;
    private String state;
    private int sex;
    private Intent intent;


    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        //QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        StatusBarUtil.setColor(this, getResources().getColor(R.color.admin_color), 0);
        StatusBarUtil.setLightMode(this);
        groupListView = findViewById(R.id.groupListView);
        qmuiTopBar = findViewById(R.id.qmui_top_bar);

        state = getIntent().getStringExtra("state");
        sex = getIntent().getIntExtra("sex", -1);
    }

    @Override
    protected void initSet() {

        initTopBar();
        initGroupList();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initPlayerDisplayData() {

    }

    private void initTopBar() {

        qmuiTopBar.addLeftImageButton(R.drawable.icon_back_black, R.id.all_backbtn).setOnClickListener(this);
        qmuiTopBar.setTitle(getString(R.string.setting));

    }

    private void initGroupList() {

        QMUICommonListItemView itemCustomQQView = groupListView.createItemView(getString(R.string.contact_custom_service));
        itemCustomQQView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemCustomQQView.setId(R.id.custom_qq);

        //获取当前APP版本号
        String version = String.valueOf(AppUtils.getAppVersionCode());

        QMUICommonListItemView itemVersionView = groupListView.createItemView(getString(R.string.version_to_update));
        itemVersionView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemVersionView.setDetailText(version);


        QMUICommonListItemView itemLoginOutView = groupListView.createItemView(getString(R.string.login_out));
        itemLoginOutView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemLoginOutView.setId(R.id.sett_login_out);


        QMUICommonListItemView itemBlackView = groupListView.createItemView(getString(R.string.black_list));
        itemBlackView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemBlackView.setId(R.id.sett_black);


        QMUICommonListItemView itemSettingVideoMoney = groupListView.createItemView(getString(R.string.set_call_mony));
        itemSettingVideoMoney.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemSettingVideoMoney.setId(R.id.sett_custom_video_coin);


        QMUICommonListItemView itemAboutView = groupListView.createItemView(getString(R.string.about_me));
        itemAboutView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemAboutView.setId(R.id.sett_about_me);


        QMUICommonListItemView itemSwitchLanguage = groupListView.createItemView(getString(R.string.switch_language));
        itemSwitchLanguage.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemSwitchLanguage.setId(R.id.sett_switch_language);


        QMUICommonListItemView itemUploadFile = groupListView.createItemView("上传日志");
        itemUploadFile.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemUploadFile.setId(R.id.upload_log);


        QMUICommonListItemView itemFeedback = groupListView.createItemView("意见反馈");
        itemFeedback.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemFeedback.setId(R.id.feed_back);

        QMUICommonListItemView itemSetCustomSayHiMsg = groupListView.createItemView("自定义打招呼话术");
        itemSetCustomSayHiMsg.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemSetCustomSayHiMsg.setId(R.id.set_custom_say_hi_msg);

        QMUICommonListItemView itemContact = groupListView.createItemView("设置联系方式");
        itemContact.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemContact.setId(R.id.setting_contact);

//        QMUICommonListItemView itemDoNotView = groupListView.createItemView("免打扰");
//        itemDoNotView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
//        itemDoNotView.setId(R.id.sett_do_not);

//        if (StringUtils.toInt(SaveData.getInstance().getUserInfo().getIs_open_do_not_disturb()) == 1) {
//            itemDoNotView.getSwitch().setChecked(true);
//        }
//        itemDoNotView.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                clickChangeDoNotDisturbStatus(b);
//            }
//        });

        QMUIGroupListView.Section section = QMUIGroupListView.newSection(this)
                .addItemView(itemCustomQQView, this)
                .addItemView(itemBlackView, this)
                .addItemView(itemVersionView, this)
                .addItemView(itemSwitchLanguage, this)
                .addItemView(itemAboutView, this)
                //.addItemView(itemUploadFile, this)
                .addItemView(itemFeedback, this);

        if (sex == 2 && "1".equals(state)) {
            section.addItemView(itemSettingVideoMoney, this);
        }

        if (sex == 2) {
            section.addItemView(itemContact, this);
        }

        if (ConfigModel.getInitData().getOpen_auto_see_hi_plugs() == 1
                && SaveData.getInstance().getUserInfo().getSex() == 2) {
            section.addItemView(itemSetCustomSayHiMsg, this);
        }


        section.addItemView(itemLoginOutView, this);
        section.addTo(groupListView);

    }

    //退出登录
    private void loginOut() {
        LoginUtils.doLoginOut(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.custom_qq:
                openCustomServiceQQ();
                break;
            case R.id.sett_login_out:
                //退出登录
                new MaterialDialog.Builder(this)
                        .content("是否退出登录？")
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                loginOut();
                            }
                        })
                        .show();
                break;
            case R.id.sett_about_me:
                WebViewActivity.openH5Activity(this, false, getString(R.string.about_me), RequestConfig.getConfigObj().getAboutUrl());
                //WealthDetailedActivity.start(this,WealthDetailedActivity.TYPE_ABOUT_US);
                //关于我们
                break;
            case R.id.all_backbtn:
                finish();
                break;
            case R.id.sett_black:
                intent = new Intent(this, BlackListActivity.class);
                startActivity(intent);
                break;
            case R.id.sett_custom_video_coin:
                clickCustomVideoCoin();
                break;
            case R.id.sett_switch_language:
                clickSwitchLanguage();
                break;
            case R.id.upload_log:
                clickUploadLog();
                break;
            case R.id.feed_back:
                clickFeedback();
                break;

            case R.id.set_custom_say_hi_msg:
                clickSetCustomSayHiMsg();
                break;

            case R.id.setting_contact:
                intent = new Intent(this, ContactSettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void clickSetCustomSayHiMsg() {
        UIHelp.showCustomMsg(this);
    }


    private void clickFeedback() {
        //WebViewActivity.openH5Activity(this, true, getString(R.string.about_me), ConfigModel.getInitData().getApp_h5().getUser_feedback());
        WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_FEED_BACK);
    }

    //上传声网日志
    private void clickUploadLog() {

        String ts = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String filepath = "/sdcard/" + ts + ".log";
        File logFile = new File(filepath);
        if (!logFile.exists()) {
            ToastUtils.showLong("日志文件不存在！");
            return;
        }
//        Api.doReuqestUploadAgoraLog(SaveData.getInstance().getId(),SaveData.getInstance().getToken(),logFile,new StringCallback(){
//
//            @Override
//            public void onSuccess(String s, Call call, Response response) {
//
//            }
//        });

//        Api.doRequestGetOSSInfo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {
//            @Override
//            public void onSuccess(String s, Call call, Response response) {
//
//                JsonDoRequestGetOssInfo data = (JsonDoRequestGetOssInfo) JsonRequestBase.getJsonObj(s, JsonDoRequestGetOssInfo.class);
//                if (StringUtils.toInt(data.getCode()) == 1) {
//                    uploadVideoThumb(data);
//                } else {
//                    ToastUtils.showLong(data.getMsg());
//                }
//            }
//
//            @Override
//            public void onError(Call call, Response response, Exception e) {
//                super.onError(call, response, e);
//                hideLoadingDialog();
//            }
//        });
    }

    //切换语言
    private void clickSwitchLanguage() {
        DialogHelp.getSelectDialog(this, new String[]{"中文简体", "中文繁体"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    switchLanguage("zh_simple");
                } else {
                    switchLanguage("zh_traditional");
                }
            }
        }).show();
    }

    private void openCustomServiceQQ() {
        Common.openCustomServiceQQ(this);
    }

    private void clickCustomVideoCoin() {
        WealthDetailedActivity.start(this, WealthDetailedActivity.TYPE_VIDEO_SET);
        //WebViewActivity.openH5Activity(this, true, getString(R.string.about_me), ConfigModel.getInitData().getApp_h5().getUser_fee());
//        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getNowContext());
//        builder.setTitle(getString(R.string.change_mony))
//                .setPlaceholder("0代表系统默认价格")
//                .setInputType(InputType.TYPE_CLASS_NUMBER)
//                .addAction(getString(R.string.not_change), new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//
//                        dialog.dismiss();
//                    }
//                })
//                .addAction(getString(R.string.determine_change), new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        CharSequence text = builder.getEditText().getText();
//
//                        changeVideoCoin(text.toString());
//                        dialog.dismiss();
//
//                    }
//                }).show();

    }

    private void changeVideoCoin(String coin) {

        showLoadingDialog(getString(R.string.loading_now_change));
        Api.doChangeVideoChargeCoin(uId, uToken, coin, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase jsonObj = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (jsonObj.getCode() == 1) {
                    showToastMsg(getString(R.string.change_success));
                } else {
                    showToastMsg(jsonObj.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
                showToastMsg(getString(R.string.change_fail));
            }
        });
    }

    private void clickChangeDoNotDisturbStatus(final boolean b) {

        showLoadingDialog(getString(R.string.loading_now_change));
        int type = b ? 1 : 2;
        Api.doRequestSetDoNotDisturb(type, SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase jsonObj = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (jsonObj.getCode() == 1) {
                    showToastMsg(getString(R.string.change_success));

                    UserModel userModel = SaveData.getInstance().getUserInfo();
                    userModel.setIs_open_do_not_disturb(b ? "1" : "0");
                    SaveData.getInstance().saveData(userModel);
                } else {
                    showToastMsg(jsonObj.getMsg());
                }
            }
        });
    }


    /**
     * 切换语言
     *
     * @param language
     */
    private void switchLanguage(String language) {

        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        if (language.equals("zh_simple")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            CuckooSharedPreUtil.put(this, "LANGUAGE", "zh_simple");
        } else if (language.equals("zh_traditional")) {
            config.locale = Locale.TRADITIONAL_CHINESE;
            CuckooSharedPreUtil.put(this, "LANGUAGE", "zh_traditional");
        } else {
            config.locale = Locale.getDefault();
        }

        resources.updateConfiguration(config, dm);

        //更新语言后，destroy当前页面，重新绘制
        finish();
        Intent it = new Intent(this, MainActivity.class);
        //清空任务栈确保当前打开activit为前台任务栈栈顶
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(it);

    }

}
