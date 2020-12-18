package com.moxin.videoline.msg.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;

import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.dao.MsgDao;
import com.moxin.videoline.inter.AdapterOnItemClick;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequestTarget;
import com.moxin.videoline.json.JsonRequestUser;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.json.jsonmodle.UserData;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.msg.MsgTim;
import com.moxin.videoline.msg.adapter.ChatAdapter;
import com.moxin.videoline.msg.modle.Msg;
import com.moxin.videoline.msg.modle.MsgModle;
import com.moxin.videoline.widget.MsgTypeListLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 消息页面
 * Created by jiahengfei on 2018/1/13 0013.
 */
public class MsgActivity extends BaseActivity implements TextWatcher,TIMValueCallBack{
    private QMUITopBar topBar;
    private RecyclerView msgRecycler;//列表
    private SwipeRefreshLayout msgRefresh;//下拉刷新
    private MaterialEditText msgText;//文本输入

    //常量
    private final int TYPE_TEXT = 0;//文本类型
    private final int TYPE_PHOTO = 1;//图片类型
    private final int TYPE_RECORD = 2;//语音类型
    private final int TYPE_VIDEO = 3;//视频类型
    private final int TYPE_FUNCTION = 3;//功能类型

    //数据库操作
    private MsgDao msgDao = CuckooApplication.getInstances().getDaoSession().getMsgDao();

    //聊天适配器
    private ChatAdapter chatListAdapter;
    //布局管理器
    private GridLayoutManager gridLayoutManager;

    //聊天列表
    private List<MsgModle> msgModles = new ArrayList<>();

    private TargetUserData targetUserData;//目标对象信息
    private UserData userData;//自身账号信息
    private Bitmap toBitmap;//目标对象信息
    private Bitmap fromBitmap;//自身账号信息

    //声明一个消息对象
    private int msgType;//当前消息对象的类型
    private String mesg;//当前消息对象

    private Dialog dialogList;//底部对话框list
    private Dialog dialogEmmm;//底部对话框emmm
    private View view;//填充view

    //底部功能栏
    private FrameLayout emmm;

    //标记
    private boolean isRetry = false;//是否为重发消息

    public MsgActivity() {
    }

    //展开底部目录的类型
    private enum AddAction{
        //emmm表情类型--listimage图片列表
        EMMM,
        LIST_IMAGE
    }

    @Override
    protected Context getNowContext() {
        return MsgActivity.this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_msg_chat;
    }

    @Override
    protected void initView() {
        topBar = findViewById(R.id.chat_topbar);
        msgRecycler = findViewById(R.id.msg_recycler);
        msgRefresh = findViewById(R.id.msg_refresh);
        emmm = findViewById(R.id.emmm);
        msgText = findViewById(R.id.msg_text);
        view = new View(getNowContext());
        view.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void initSet() {
        topBar.addLeftImageButton(R.drawable.icon_back_black,R.id.msg_left_btn).setOnClickListener(this);
        topBar.addRightImageButton(R.drawable.icon_chat_menu,R.id.msg_right_btn).setOnClickListener(this);
        setOnclickListener(R.id.msg_grid,R.id.msg_voice,R.id.msg_emmm,R.id.msg_add,R.id.msg_send);

        gridLayoutManager = new GridLayoutManager(getNowContext(),1);
        msgRecycler.setLayoutManager(gridLayoutManager);

        //下拉刷新监听
        msgRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                msgRefresh.setRefreshing(false);
            }
        });
        //文本变化监听
        msgText.addTextChangedListener(this);
    }

    @Override
    protected void initData() {
        requestData();
        //消息观察者监听对象
        initSetByThen();
    }

    @Override
    protected void initPlayerDisplayData() {
    }

    /**
     * 初始化第三方监听
     * //log("getPeer:::::::"+message.getConversation().getPeer());//发送方id
     * //log("getIdentifer:::::::"+message.getConversation().getIdentifer());//接收方id
     */
    private void initSetByThen() {
        //腾讯云
        //设置消息监听
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                log("TIMM腾讯单聊消息接收--------------------------->"+list.toString());
                //监听大群组会话信息,不对消息做拦截操作
                for (TIMMessage message:list) {
                    for(int i = 0; i < message.getElementCount(); ++i) {
                        TIMElem elem = message.getElement(i);
                        TIMElemType elemType = elem.getType();
                        if (elemType == TIMElemType.Text) {
                            msgType = 0;//当前消息类型
                            //处理文本消息
                            TIMTextElem e = (TIMTextElem) elem;
                            mesg = e.getText();
                        } /*else if (elemType == TIMElemType.Image) {
                            msgType = 1;//当前消息类型
                            //图片元素
                            TIMImageElem e = (TIMImageElem) elem;
                            for(TIMImage image : e.getImageList()) {
                                image.getImage(new TIMValueCallBack<byte[]>() {
                                    @Override
                                    public void onError(int code, String desc) {
                                        log("获取图片失败::code="+code);
                                    }
                                    @Override
                                    public void onSuccess(byte[] data) {//成功，参数为图片数据
                                        mesg = ImageUtil.getStringByByte(data);
                                    }
                                });
                            }
                        }*/
                        saveMsg(true,1,mesg);
                        refreshMsgList(20);
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void doLogout() {
        //注销消息观察者
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.msg_left_btn:
                doGoBack();
                break;
            case R.id.msg_right_btn:
                doShowMore();
                break;
            case R.id.msg_emmm:
                doAddAction(AddAction.EMMM);
                break;
            case R.id.msg_add:
                doAddAction(AddAction.LIST_IMAGE);
                break;
            case R.id.msg_send:
                sendMsg(new Msg(msgText.getText().toString(),TYPE_TEXT));
                msgText.setText("");
                break;
            case R.id.msg_recycler:
                doRecycleClick();
                break;
            case R.id.dialog_img_photo:
                doSelectPhoto();
                break;
            case R.id.dialog_img_video:
                doSelectVideo();
                break;
            case R.id.dialog_img_grid:
                doGoveGrid();
                break;
        }
    }

    /**
     * 消息发送监听
     */
    @Override
    public void onError(int i, String s) {
        //消息发送失败监听        }
        saveMsg(false, 0,mesg);
        refreshMsgList(20);
        showToastMsg("消息发送失败:code="+i);
        log("消息发送失败:==============================?code="+i);
    }

    @Override
    public void onSuccess(Object o) {
        //消息发送成功监听
        saveMsg(true,0,mesg);
        refreshMsgList(20);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //文本输入之后监听
        if (s.length() == 0){
            showView(R.id.msg_add);
            concealView(R.id.msg_send);
        }else if(s.length() >= 1){
            showView(R.id.msg_send);
            concealView(R.id.msg_add);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //文本输入之前监听
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //文本输入中监听
    }
    /////////////////////////////////////////////业务逻辑处理/////////////////////////////////////////
    /**
     * 请求用户数据信息
     */
    private void requestData() {
        //请求目标用户数据
        Api.getUserData(
                getIntent().getStringExtra("str"),
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
                            targetUserData = requestObj.getData();
                            targetUserData.setId(getIntent().getStringExtra("str"));
                            topBar.setTitle(targetUserData.getUser_nickname());
                            if (ApiUtils.isTrueUrl(targetUserData.getAvatar())){
                                ApiUtils.getUrlBitmap(targetUserData.getAvatar(), new ApiUtils.GetUrlBitMap() {
                                    @Override
                                    public void doThenByBitmap(Bitmap bitmap) {
                                        toBitmap = bitmap;
                                        refreshMsgList(20);
                                    }
                                });
                            }
                        }else{
                            showToastMsg(requestObj.getMsg());
                        }
                    }
                }
        );
        //请求用户自身数据
        Api.getUserDataAtCompile(
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
                        JsonRequestUser requestObj = JsonRequestUser.getJsonObj(s);
                        if (requestObj.getCode() == 1){
                            userData = requestObj.getData();
                            if (ApiUtils.isTrueUrl(userData.getAvatar())){
                                ApiUtils.getUrlBitmap(userData.getAvatar(), new ApiUtils.GetUrlBitMap() {
                                    @Override
                                    public void doThenByBitmap(Bitmap bitmap) {
                                        fromBitmap = bitmap;
                                        refreshMsgList(20);
                                    }
                                });
                            }
                        }else{
                            showToastMsg(requestObj.getMsg());
                        }
                    }
                }
        );
    }

    /**
     * 执行返回操作
     */
    private void doGoBack() {
        finish();
    }

    /**
     * 打开更多操作
     */
    private void doShowMore() {
    }

    /**
     * 执行送礼物操作
     */
    private void doGoveGrid() {
        dialogList.dismiss();
    }

    /**
     * 选择照片
     */
    private void doSelectPhoto() {
        dialogList.dismiss();
    }

    /**
     * 选择视频
     */
    private void doSelectVideo() {
    }

    /**
     * 执行底部选择栏动态显示操作
     * @param addAction 判断类型-确认打开什么
     */
    private void doAddAction(AddAction addAction) {
        if (addAction != null){
            clearActionLayout();
            switch (addAction){
                case EMMM:
                    addActionEmmm();
                    break;
                case LIST_IMAGE:
                    //addActionList();
                    showDialogList();
                    break;
            }
        }
    }

    /**
     * 移除Emmm中的布局对象
     */
    private void clearActionLayout() {
        emmm.removeAllViews();
    }


    /**
     * 执行列表的点击事件
     */
    private void doRecycleClick() {

    }

    /**
     * 添加一个emmm布局
     */
    private void addActionEmmm() {
    }

    /**
     * 添加一个list布局
     */
    private void addActionList() {
        MsgTypeListLayout msgTypeListLayout = new MsgTypeListLayout(getNowContext(), new MsgTypeListLayout.OnItemclick() {
            @Override
            public void onItemClick(View view, AdapterOnItemClick.ViewName viewName, int position) {
                concealView(emmm);
                showToastMsg("点击了::"+position);
            }
        });
        emmm.addView(msgTypeListLayout);
    }

    /**
     * 显示一个listDialog
     */
    private void showDialogList(){
        emmm.addView(view,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,320));
        dialogList = showButtomDialogWhite(
                R.layout.dialog_msg_list,
                new int[] {R.id.dialog_img_photo,R.id.dialog_img_video,R.id.dialog_img_grid},
                0);
        dialogList.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clearActionLayout();
            }
        });
        dialogList.show();
    }

    /**
     * 执行发送信息操作(单聊)
     * @param msg 消息对象
     */
    private void sendMsg(Msg msg) {
        msgType = msg.getType();
        switch (msg.getType()){
            case 0:
                //发送一个文本消息
                MsgTim.getInstaner(targetUserData.getId()).sendMsg(msg.getMsg(),this);
                break;
            case 1:
                // 创建一个图片消息
                MsgTim.getInstaner(targetUserData.getId()).sendImgMsg(msg.getMsg(),this);
            case 2:
                //创建一个语音信息
                break;
        }
        mesg = msg.getMsg();
    }

    /**
     * 刷新消息列表
     */
    private void refreshMsgList(int limit) {
        msgModles = selectMsg(limit);
        //log("msgModles::当前查询到的数据库消息对象解析之后"+msgModles.toString());
        //刷新适配器
        chatListAdapter = new ChatAdapter(toBitmap,fromBitmap,msgModles);
        //chatListAdapter.notifyItemRangeInserted(chatListAdapter.getItemCount(),msgModles.size());
        msgRecycler.setAdapter(chatListAdapter);
        //移动光标到最下
        msgRecycler.smoothScrollToPosition(chatListAdapter.getItemCount());
        msgRecycler.setOnClickListener(this);
        //列表注册
        chatListAdapter.setOnItemClickListener(new AdapterOnItemClick() {
            @Override
            public void onItemClick(View view, ViewName viewName, int position) {
                if (viewName == ViewName.RETRY){
                    anewSend(chatListAdapter.getModle(position).getId());
                }else {
                    //点击Le消息
                    showToastMsg("点击Le消息");
                }
            }
        });
    }

    /**
     * 保存聊天信息
     * @param isSend 是否发送成功
     * @param msgModleType 消息状态--0发出消息(right_chat),1接受消息(left)
     * @param msg 消息
     */
    private void saveMsg(boolean isSend, int msgModleType,String msg){
        Msg msgSave = new Msg(null, SaveData.getInstance().getId(),getIntent().getStringExtra("str"),msg,msgType,msgModleType,new Date(System.currentTimeMillis()),"",isSend?0:1);
        msgDao.insert(msgSave);
    }

    /**
     * 查看聊天信息
     */
    private List<MsgModle> selectMsg(int litim){
        List<Msg> msgList = msgDao.queryBuilder()
                .where(MsgDao.Properties.Account.eq(getIntent().getStringExtra("str")),MsgDao.Properties
                .MyAccount.eq(SaveData.getInstance().getId()))
                .limit(litim)
                .orderAsc(MsgDao.Properties.Date)
                .list();
        log("msgModles::当前查询到的数据库消息对象"+msgList.toString());
        List<MsgModle> selectMsgMoudle = new ArrayList<>();
        for (Msg msgFor:msgList) {
            selectMsgMoudle.add(analysisMsg(msgFor));
        }
        return selectMsgMoudle;
    }

    /**
     * 查看未发送的聊天信息
     */
    private List<MsgModle> selectMsgNotSend(){
        List<Msg> msgList = msgDao.queryBuilder()
                .where(MsgDao.Properties.I.eq(1))
                .orderAsc(MsgDao.Properties.Date)
                .list();
        List<MsgModle> selectMsgMoudle = new ArrayList<>();
        for (Msg msgFor:msgList) {
            selectMsgMoudle.add(analysisMsg(msgFor));
        }
        return selectMsgMoudle;
    }

    /**
     * 解析Msg对象为MsgModle
     * @param msgFor Msg对象
     * @return MsgModle
     */
    private MsgModle analysisMsg(Msg msgFor) {
        return new MsgModle(msgFor.getId(),msgFor.getStatus() == 0 ? MsgModle.Type.Right : MsgModle.Type.Left,msgFor);
    }

    /**
     * 消息重发
     * @param id 重发消息id
     */
    private void anewSend(Long id){
        Msg msg = msgDao.queryBuilder().where(MsgDao.Properties.Id.eq(id)).unique();
        if (msg != null){
            msgDao.deleteByKey(msg.getId());//清除旧记录
            sendMsg(msg);//重新发送消息
        }
    }


    /**
     * 执行发送一条tip消息(提示消息)
     */
    private void doShowTip() {

    }

    /**
     * 初始化File对象
     */
    private void initFiles(){
        // 在这里我们创建一个文件，用于保存录制内容
        File fpath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/recondMsg/");
        fpath.mkdirs();// 创建文件夹
//        try {
//            // 创建临时文件,注意这里的格式为.pcm
//            sendRecordFile = File.createTempFile("recording", ".mp3", fpath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
