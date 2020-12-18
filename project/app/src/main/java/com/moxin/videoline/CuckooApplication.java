package com.moxin.videoline;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.dao.DaoMaster;
import com.moxin.videoline.dao.DaoSession;
import com.moxin.videoline.event.EImOnNewMessages;
import com.moxin.videoline.event.LocalEvent;
import com.moxin.videoline.helper.ContentUtils;
import com.moxin.videoline.inter.MsgDialogClick;
import com.moxin.videoline.manage.AppConfig;
import com.moxin.videoline.manage.AppManager;
import com.moxin.videoline.manage.JsonDataManage;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.custommsg.MsgModel;
import com.moxin.videoline.modle.custommsg.TIMMsgModel;
import com.moxin.videoline.ui.MainActivity;
import com.moxin.videoline.ui.RegisterSelectActivity;
import com.moxin.videoline.utils.CuckooLifecycleHandler;
import com.moxin.videoline.utils.CuckooSharedPreUtil;
import com.moxin.videoline.utils.SDHandlerManager;
import com.fm.openinstall.OpenInstall;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import static com.tencent.imsdk.TIMElemType.Custom;
import static com.tencent.imsdk.TIMElemType.Text;
import static com.tencent.qalsdk.QALBroadcastReceiver.tag;

/**
 * 初始化全局配置
 * Created by wp on 2018/1/5 0005.
 */

public class CuckooApplication extends Application {
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static CuckooApplication instances;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private String lat;
    private String lng;
    private String city = "";

    private TIMMsgModel pushVideoCallMessage;
    private boolean isInVideoCallWait;
    private boolean isInPrivateChatPage;

    private boolean isRefreshLocal;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        //初始化库
        initOpenInstall();
        initUmeng();
        //初始化数据库框架greenDao
        initDatabase();
        //初始化腾讯云SDK
        initTim();
        //初始化网路请求框架
        initOkGo();
        //初始化高德定位
        initAmap();
        //初始化utils
        Utils.init(this);
        initLanguage();

        registerActivityLifecycleCallbacks(new CuckooLifecycleHandler());
        //registerActivityLifecycleCallbacks(new ActivityHelper());
        closeAndroidPDialog();
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置okgo
     */
    private void initOkGo() {
//        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpParams params = new HttpParams();
        params.put("os", "Android");     //param支持中文,直接传,不要自己编码
        params.put("sdk_version", Build.VERSION.SDK_INT);
        params.put("app_version", BuildConfig.VERSION_CODE);
        params.put("brand", android.os.Build.BRAND);//手机厂商
        params.put("model", android.os.Build.MODEL);//手机型号
        //初始化okgo
        OkGo.init(this);
        OkGo.getInstance()
                .debug("BOGO_HTTP", Level.INFO, true)
                .addCommonParams(params);   //设置全局公共参数;
    }

    //初始化多语言
    private void initLanguage() {
        String language = "zh_simple";
        String languageLocal = CuckooSharedPreUtil.getString(this, "LANGUAGE");
        if (!TextUtils.isEmpty(languageLocal)) {
            language = languageLocal;
        }

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
    }

    ////////////////////////////////////////配置OpenInstallSDK//////////////////////////////////////////////
    private void initOpenInstall() {
        if (com.moxin.videoline.utils.Utils.isMainProcess(this)) {
            OpenInstall.init(this);
        }
    }

    ////////////////////////////////////////配置友盟SDK//////////////////////////////////////////////
    private void initUmeng() {

        UMConfigure.init(this, getResources().getString(R.string.umeng_appkey), "buguniao", UMConfigure.DEVICE_TYPE_PHONE, getResources().getString(R.string.umeng_message_secret));
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        // 打开统计SDK调试模式
        UMConfigure.setLogEnabled(true);
        PushAgent mPushAgent = PushAgent.getInstance(this);

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                //Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                //Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();

                JSONObject pushObj = JSON.parseObject(msg.custom);

                switch (pushObj.getInteger("action")) {

                    case LiveConstant.PushType.VIDEO_CALL:
                        Intent intent = new Intent(getInstances(), MainActivity.class);
                        intent.putExtra("push_data", pushObj.getString("custom_data"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }

        };

        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setDisplayNotificationNumber(0);
        //通知冷却时间
        mPushAgent.setMuteDurationSeconds(0);
        //通知免打扰关闭
        mPushAgent.setNoDisturbMode(0, 0, 0, 0);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                LogUtils.i("推送注册成功");
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtils.i("推送注册失败:" + s);

            }
        });

        mPushAgent.setNotificaitonOnForeground(false);
    }

    ////////////////////////////////////////配置高德地图//////////////////////////////////////////////
    private void initAmap() {
        ///回调监听
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                lat = String.valueOf(aMapLocation.getLatitude());//获取纬度
                lng = String.valueOf(aMapLocation.getLongitude());//获取经度
                city = aMapLocation.getCity();

                LocalEvent event = new LocalEvent();
                EventBus.getDefault().post(event);

                if (SaveData.getInstance().isLogin && !isRefreshLocal) {
                    isRefreshLocal = true;
                    Log.e("doRefreshCity",city+"="+lat+"="+lng);
                    Api.doRefreshCity(SaveData.getInstance().getId(), city, lat, lng, null);
                }

                mLocationClient.stopLocation();
                //LogUtils.i(city);
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果：
        mLocationOption.setOnceLocation(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    ////////////////////////////////////////配置腾讯云SDK//////////////////////////////////////////////
    private void initTim() {

        ContentUtils.TxContent.SDK_APPID = getResources().getInteger(R.integer.tencent_sdk_app_id);
        if (MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                        LogUtils.i("离线消息接受");
                    }
                }
            });
            initTimConfig();//设置腾讯云基础用户配置
            //初始化SDK
            TIMSdkConfig config = new TIMSdkConfig(ContentUtils.TxContent.SDK_APPID);
            config.enableLogPrint(true).setLogLevel(TIMLogLevel.INFO);
            TIMManager.getInstance().init(getApplicationContext(), config);

        }
    }


    /**
     * 设置腾讯云基础用户配置
     */
    private void initTimConfig() {

        TIMUserConfig timUserConfig = new TIMUserConfig();
        //设置用户状态变更事件监听器
        timUserConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                //被其他终端踢下线
                doAgainLogin("下线通知", "当前账号在其它设备登录,请重新登录!");
            }

            @Override
            public void onUserSigExpired() {
                //用户签名过期了，需要刷新userSig重新登录SDK
                doAgainLogin("下线通知", "当前用户签名过期,为了账户安全考虑请重新登录!");
            }
        });
        //设置连接状态事件监听器
        timUserConfig.setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                //链接建立
                Log.i(tag, "onConnected");
            }

            @Override
            public void onDisconnected(int code, String desc) {
                //链接断开
                //接口返回了错误码code和错误描述desc，可用于定位连接断开原因
                //错误码code含义请参见错误码表
                Log.i(tag, "onDisconnected");
            }

            @Override
            public void onWifiNeedAuth(String name) {
                //链接需要验证
                Log.i(tag, "onWifiNeedAuth");
            }
        });

        //设置消息监听
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {

            private void postNewMessage(MsgModel msgModel) {
                EImOnNewMessages event = new EImOnNewMessages();
                event.msg = msgModel;
                EventBus.getDefault().post(event);

            }

            @Override
            public boolean onNewMessages(final List<TIMMessage> list) {
                LogUtils.i("msg发送方" + list.size());
                if (list != null) {
                    SDHandlerManager.getBackgroundHandler().post(new Runnable() {
                        @Override
                        public void run() {

                            //监听大群组会话信息,不对消息做拦截操作
                            //LogUtils.i("全局Application消息接受--------------------------->" + list.toString());
                            //监听大群组会话信息,不对消息做拦截操作
                            for (TIMMessage message : list) {

                                TIMElemType lastType = null;
                                for (int i = 0; i < message.getElementCount(); ++i) {

                                    TIMElem elem = message.getElement(i);
                                    LogUtils.i("msg发送方" + message.getSender());
//                                    if (message.getSender().equals("admin")) {
//                                        return;
//                                    }


                                    TIMElemType type = message.getElement(i).getType();
                                    //这一行兼容发送CustomMsgText 勿删
                                    if (lastType == Text && type == Custom) {
                                        continue;
                                    }
                                    lastType = type;



                                    if (AppConfig.DEBUG) {
                                        TIMConversation conversation = message.getConversation();

                                    }
                                    TIMConversation conversation = message.getConversation();
                                    if (conversation.getType().toString().equals("System")) {
                                        continue;
                                    }

                                    boolean post = true;
                                    final TIMMsgModel msgModel = new TIMMsgModel(message, true);

                                    if (msgModel.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL
                                            || msgModel.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL_END
                                            || msgModel.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_VIDEO_LINE_CALL_REPLY
                                            || msgModel.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_CLOSE_VIDEO_LINE) {

                                        post = false;
                                    }

                                    if (post) {
                                        postNewMessage(msgModel);
                                    }
                                }
                            }


                        }
                    });
                }

                return false;
            }


        });
    }

    ////////////////////////////////////////////配置greenDao/////////////////////////////////////////
    private void initDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

        //初始化json数据存储
        JsonDataManage.init(this);
        //初始化登录数据保存
        SaveData.init(this);
    }

    /**
     * 获取DaoSession
     *
     * @return DaoSession对象
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 获取到创建的数据库对象
     *
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getDb() {
        return db;
    }


    /**
     * 获取经纬度
     *
     * @return HashMap<String, String>
     */
    public HashMap<String, String> getLocation() {
        HashMap<String, String> location = new HashMap<>();
        //启动定位
        mLocationClient.startLocation();
        location.put("lat", lat);//维度
        location.put("lng", lng);//经度
        location.put("city", city);//经度
        return location;
    }

    /**
     * 执行重新登录操作
     *
     * @param title 提示信息头
     * @param msg   提示信息
     */
    private void doAgainLogin(String title, String msg) {
        com.moxin.videoline.utils.Utils.showMsgDialog(getApplicationContext(),
                title,
                msg,
                new MsgDialogClick() {
                    @Override
                    public void doYes(QMUIDialog dialog, int index) {
                        com.moxin.videoline.utils.Utils.goActivity(getApplicationContext(), RegisterSelectActivity.class).finish();//重新登录
                    }

                    @Override
                    public void doNo(QMUIDialog dialog, int index) {
                        AppManager.getAppManager().appExit(getApplicationContext());//退出
                    }
                });
    }

    public TIMMsgModel getPushVideoCallMessage() {
        return pushVideoCallMessage;
    }

    public void setPushVideoCallMessage(TIMMsgModel pushVideoCallMessage) {
        this.pushVideoCallMessage = pushVideoCallMessage;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }


    /**
     * 获取本类的实例.用于获取数据库对象
     *
     * @return RabbitApplication
     */
    public static CuckooApplication getInstances() {
        return instances;
    }

    public boolean isInVideoCallWait() {
        return isInVideoCallWait;
    }

    public void setInVideoCallWait(boolean inVideoCallWait) {
        isInVideoCallWait = inVideoCallWait;
    }

    public boolean isInPrivateChatPage() {
        return isInPrivateChatPage;
    }

    public void setInPrivateChatPage(boolean inPrivateChatPage) {
        isInPrivateChatPage = inPrivateChatPage;
    }


}
