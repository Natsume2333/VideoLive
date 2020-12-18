package com.moxin.tisdk;

import android.util.Log;

import java.nio.ByteBuffer;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;

public class AGRender {
    //share mByteBuffer with native code
    private static ByteBuffer mByteBuffer;
    private static TiSDKManager tiSDKManager;

    public static void init(TiSDKManager manager) {
        tiSDKManager = manager;
    }

    /**
     * init buffer，call by native code
     *
     * @param len len
     * @return buffer
     */
    public static ByteBuffer createBuffer(int len) {
        mByteBuffer = ByteBuffer.allocateDirect(len);
        return mByteBuffer;
    }

    /**
     * render yuv frame,modify mByteBuffer,call by native code
     *
     * @param w        width
     * @param h        height
     * @param rotation rotation
     * @return -1,use the original yuv data
     */
    public static int renderYuvFrame(int w, int h, int rotation) {

//        Log.e("AGRender", w + " " + h + " " + rotation);

        if (tiSDKManager == null || mByteBuffer == null) {
            Log.e("tracker", "tiSDKManager == null");
            return -1;
        }

        tiSDKManager.renderPixels(mByteBuffer.array(), TiSDKManager.FORMAT_NV21, w, h, TiRotation.fromValue(rotation), true);

        return 0;
    }

}