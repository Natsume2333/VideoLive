package com.moxin.videoline.manage;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.modle.JsonData;
import com.moxin.videoline.modle.UserModel;

/**
 * 保存数据
 */
public class SaveData {

    //用户登录信息存储KEY
    public static final String USER_SAVE_LOGIN_INFO = "USER_SAVE_LOGIN_INFO";

    public String id;
    public String token;
    public String userSig;
    public boolean isLogin = false;
    public UserModel userModel;

    public static SaveData instance;

    /**
     * 获取当前单例
     *
     * @return SaveData
     */
    public static SaveData getInstance() {
        if (instance == null) {
            instance = new SaveData();
        }
        return instance;
    }

    /**
     * 初始化方法
     *
     * @param context NimApplication对象
     */
    public static void init(CuckooApplication context) {

        LogUtils.d("登录信息初始化");
        getInstance().initData(context);
    }

    /**
     * 初始化数据
     */
    private void initData(CuckooApplication context) {

        JsonData infoJson = JsonDataManage.getInstance().getData(USER_SAVE_LOGIN_INFO);
        if (infoJson != null) {
            LogUtils.d("登录信息不为空进行初始化状态" + infoJson.getVal());
            //存在登录信息
            refreshNow(JSON.parseObject(infoJson.getVal(), UserModel.class));
        } else {
            LogUtils.d("登录信息为空");
            //不存在登录信息
            isLogin = false;
        }
    }


    /**
     * 登录成功
     *
     * @param userConfig 用户信息配置对象
     */
    public static void loginSuccess(UserModel userConfig) {
        getInstance().saveData(userConfig);
    }

    /**
     * 刷新用户操作
     *
     * @param userConfig 用户信息配置对象
     */
    public static void refreshUserConfig(UserModel userConfig) {
        getInstance().setUserConfig(userConfig);
    }

    /**
     * 刷新页面数据
     *
     * @param userConfig
     */
    private void refreshNow(UserModel userConfig) {
        id = userConfig.getId();
        token = userConfig.getToken();
        userSig = userConfig.getUser_sign();
        userModel = userConfig;
        isLogin = true;
    }

    /**
     * 保存信息
     *
     * @param userModel 用户配置对象--重置id为0
     */
    public void saveData(UserModel userModel) {
        //userConfigDao = CuckooApplication.getInstances().getDaoSession().getUserConfigDao();
        if (isLogin) {
            JsonDataManage.getInstance().delete(USER_SAVE_LOGIN_INFO);
        }
        JsonData jsonData = new JsonData();
        jsonData.setId(null);
        jsonData.setKey(USER_SAVE_LOGIN_INFO);
        jsonData.setVal(JSON.toJSONString(userModel));
        JsonDataManage.getInstance().saveData(jsonData);
        refreshNow(userModel);
    }


    /**
     * 设置新的userModel--从数据库中做修改操作
     *
     * @param userModel userModel
     */
    private void setUserConfig(UserModel userModel) {
        if (isLogin) {
            JsonDataManage.getInstance().delete(USER_SAVE_LOGIN_INFO);
        }
        JsonData jsonData = new JsonData();
        jsonData.setId(null);
        jsonData.setKey(USER_SAVE_LOGIN_INFO);
        jsonData.setVal(JSON.toJSONString(userModel));
        JsonDataManage.getInstance().saveData(jsonData);
        refreshNow(userModel);
    }


    /*
     * 清除用户信息
     * */
    public void clearData() {
        id = "0";
        token = "";
        isLogin = false;
        JsonDataManage.getInstance().delete(USER_SAVE_LOGIN_INFO);
        LogUtils.i("清除登录用户信息");
    }

    //获取用户登录信息
    public UserModel getUserInfo() {

//        if (userModel != null) {
//            return userModel;
//        }
        JsonData jsonData = JsonDataManage.getInstance().getData(USER_SAVE_LOGIN_INFO);
        userModel = JSON.parseObject(jsonData.getVal(), UserModel.class);
        Log.i("用户信息", "getUserInfo: "+userModel.toString());
        return userModel;
    }

    //获取数据
    public String getId() {
        return id;
    }

    public String getUserSig() {
        return userSig;
    }

    public String getToken() {
        return token;
    }

    public boolean isIsLogin() {
        return isLogin;
    }

}
