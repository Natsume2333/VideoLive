package com.moxin.videoline.msg.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.view.View;

import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.dao.MsgDao;
import com.moxin.videoline.inter.AdapterOnItemClick;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequestTarget;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.msg.adapter.ChatListAdapter;
import com.moxin.videoline.msg.modle.ChatData;
import com.moxin.videoline.msg.modle.Msg;
import com.moxin.videoline.ui.common.Common;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 消息列表
 * Created by jiahengfei on 2018/2/2 0002.
 */

public class MsgListActivity extends BaseActivity{
    private QMUITopBar topBar;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ChatListAdapter chatListAdapter;

    private MsgDao msgDao = CuckooApplication.getInstances().getDaoSession().getMsgDao();

    private List<ChatData> chatDatas = new ArrayList<>();

    @Override
    protected Context getNowContext() {
        return MsgListActivity.this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_msg_list;
    }

    @Override
    protected void initView() {
        topBar = findViewById(R.id.topbar);
        recyclerView = findViewById(R.id.recycler);

        gridLayoutManager = new GridLayoutManager(getNowContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void initSet() {
        topBar.setTitle("私信");
        topBar.addRightTextButton("忽略未读",R.id.msg_right_btn).setOnClickListener(this);
        topBar.addLeftImageButton(R.drawable.icon_back_black,R.id.all_backbtn).setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        requestUserList();
    }

    @Override
    protected void initPlayerDisplayData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.all_backbtn:
                finishNow();
                break;
            case R.id.msg_right_btn:
                doOverLook();//忽略未读
                break;
        }
    }

    /**
     * 执行忽略未读操作
     */
    private void doOverLook() {
        showToastMsg("忽略未读");
    }

    /**
     * 获取最近联系人
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestUserList() {
        List<Msg> msgList = msgDao.queryBuilder()
                .where(MsgDao.Properties.MyAccount.eq(SaveData.getInstance().getId()))
                .orderAsc(MsgDao.Properties.Date)
                .list();
        Set<String> msgAcount = new ArraySet<>();
        for (Msg msg:msgList) {
            if (!msg.equals(SaveData.getInstance().getId())){
                msgAcount.add(msg.getAccount());
            }
        }
        if (msgAcount.size() >0){
            for (String str:msgAcount) {
                requestUserData(str,getLastMsg(str));
            }
        }
    }

    /**
     * 根据id获取这个用户的最后一条msg
     * @param id 用户id
     * @return Msg
     */
    private Msg getLastMsg(String id){
        List<Msg> msgList = msgDao.queryBuilder()
                .where(MsgDao.Properties.Account.eq(id))
                .orderAsc(MsgDao.Properties.Date)
                .list();
        return msgList.get(msgList.size()-1);
    }

    /**
     * 刷新适配器
     */
    private void refreshAdapter(){
        //刷新加载适配器
        chatListAdapter = new ChatListAdapter(this,chatDatas);
        recyclerView.setAdapter(chatListAdapter);
        chatListAdapter.setOnItemClickListener(new AdapterOnItemClick() {
            @Override
            public void onItemClick(View view, ViewName viewName, int position) {
                if (viewName == ViewName.HEAD_PORTRAIT){
                    //点击头像
                    Common.jumpUserPage(MsgListActivity.this,chatDatas.get(position).getAccount());
                }else{
                    //点击布局
                    goActivity(MsgActivity.class,chatDatas.get(position).getAccount());
                }

            }
        });
    }

    /**
     * 获取用户信息
     */
    private void requestUserData(final String id, final Msg msg) {
        Api.getUserData(
                id,
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
                        JsonRequestTarget requestObj = JsonRequestTarget.getJsonObj(s);
                        if (requestObj.getCode() == 1){
                            TargetUserData targetUserData = requestObj.getData();
                            chatDatas.add(new ChatData(id,targetUserData.getUser_nickname(),targetUserData.getAvatar(),msg,0));
                            log("chatDatas::"+chatDatas.toString());
                            refreshAdapter();
                        }else {
                            log("获取用户信息::"+requestObj.getMsg());
                        }
                    }
                }
        );
    }
}
