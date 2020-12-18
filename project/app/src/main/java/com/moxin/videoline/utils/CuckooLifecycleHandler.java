package com.moxin.videoline.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class CuckooLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;
    String  TAG = "ACT管理";

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated: "+activity.getPackageName()+" : "+activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
        Log.i(TAG, "onActivityStarted: "+activity.getPackageName()+" : "+activity.getLocalClassName());

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        Log.i(TAG, "onActivityResumed: "+activity.getPackageName()+" : "+activity.getLocalClassName());

    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        android.util.Log.w("test", "application is in foreground: " + (resumed > paused));
        Log.i(TAG, "onActivityPaused: "+activity.getPackageName()+" : "+activity.getLocalClassName());

    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.w("test", "application is visible: " + (started > stopped));
        Log.i(TAG, "onActivityStopped: "+activity.getPackageName()+" : "+activity.getLocalClassName());

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, "onActivityDestroyed: "+activity.getPackageName()+" : "+activity.getLocalClassName());

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i(TAG, "onActivitySaveInstanceState: "+activity.getPackageName()+" : "+activity.getLocalClassName());

    }

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
    // 当所有 Activity 的状态中处于 resumed 的大于 paused 状态的，即可认为有Activity处于前台状态中 
        return resumed > paused;
    }
}