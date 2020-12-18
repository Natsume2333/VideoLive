package com.moxin.videoline.helper;

import com.moxin.videoline.R;

/**
 * 资源选择器
 * Created by jiahengfei on 2017/12/25 0025.
 */

public class SelectResHelper {

    //获取是否认证资源
    public static int getAttestationRes(int attestation){

        return attestation == 1 ? R.drawable.video_verified : R.drawable.video_not_verify;
    }

    //获取性别资源
    public static int getSexRes(int sex){
        return sex == 1 ? R.drawable.im_emoji_icon : R.drawable.im_emoji_icon_inactive;
    }

    //获取是否在线资源
    public static int getOnLineRes(int onLine){
        return onLine == 1 ? R.drawable.bg_green_num : R.drawable.bg_beckoning_num;
    }


    //获取是否认证资源
    public static int getAttestationResForSex(int sex ,int attestation){
        if (sex==2){
            return attestation == 1 ? R.mipmap.certified : R.mipmap.uncertified;
        }else {
            return attestation == 1 ? R.mipmap.certified : 0;
        }

    }

}
