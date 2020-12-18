package com.moxin.videoline.business;

import com.moxin.videoline.ui.VideoLineActivity;
import com.moxin.videoline.utils.BGTimedTaskManage;

public class CuckooCheckRateBusiness implements BGTimedTaskManage.BGTimeTaskRunnable {

    private int time = 1000 * 10;
    private BGTimedTaskManage bgTimedTaskManage;
    private VideoLineActivity activity;

    public CuckooCheckRateBusiness(VideoLineActivity activity) {
        this.activity = activity;
    }

    public void setTime(int time) {
        this.time = time * 1000;
    }

    public void startCheck() {
        if (bgTimedTaskManage == null) {
            bgTimedTaskManage = new BGTimedTaskManage();
            bgTimedTaskManage.setTime(time);
        }
        bgTimedTaskManage.setTimeTaskRunnable(this);
        bgTimedTaskManage.startRunnable(false);
    }

    public void stopCheck() {
        if (bgTimedTaskManage != null) {
            bgTimedTaskManage.stopRunnable();
        }

        activity = null;
    }

    @Override
    public void onRunTask() {
//        root_view.setDrawingCacheEnabled(true);
//        root_view.buildDrawingCache();       //启用DrawingCache并创建位图
//
//        // 创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
//        Bitmap bitmap = Bitmap.createBitmap(root_view.getDrawingCache());
//        root_view.setDrawingCacheEnabled(false);     //禁用DrawingCahce否则会影响性能

        //不行
//        View view = activity.getWindow().getDecorView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.setDrawingCacheEnabled(false);
//        view.destroyDrawingCache();



//        if (bitmap != null) {
//            try {
//                // 获取内置SD卡路径
//                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
//                // 图片文件路径
//                String filePath = sdCardPath + File.separator + "screenshot.png";
//                File file = new File(filePath);
//                FileOutputStream os = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//                os.flush();
//                os.close();
//
//                Api.doRequestCheckImg(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), file, new StringCallback() {
//
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
//                        if (data.getCode() == 10099) {
//                            ToastUtils.showLong(data.getMsg());
//                        }
//                    }
//                });
//            } catch (Exception e) {
//
//            }
//        }


    }
}
