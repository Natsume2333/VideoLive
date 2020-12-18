package com.moxin.videoline.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.moxin.videoline.R;

import butterknife.ButterKnife;

public abstract class BaseDialog {

    public Context context;
    public AlertDialog dialog;
    public View view;

    public BaseDialog(Context context) {
        this.context = context;
        view=LayoutInflater.from(context).inflate(setRes(),null,false);
        dialog=new AlertDialog.Builder(context).setView(view).create();
        ButterKnife.bind(this,view);
        init();
    }

    public void init(){

    }





    public void setTrans(){
       dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }



    public void show(){
        dialog.show();
        Log.i("显示Dialog", this.getClass().getName()+" from "+context.getClass().getName());
    }

    public void hide(){
        dialog.dismiss();
    }

    public void setCancel(boolean b){
        dialog.setCancelable(b);
    }

    public abstract int setRes();

    /**
     * @param f 在show之后使用
     */
    public BaseDialog setWith(float f){
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m =dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * f); // 宽度设置为屏幕的0.95
        dialogWindow.setAttributes(p);
        return this;
    }

    public BaseDialog setBottomPop(){
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogBtPop);
        window.setGravity(Gravity.BOTTOM);
        return this;
    }



}
