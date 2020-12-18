package com.moxin.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.chat.adapter.ChatAdapter;
import com.moxin.chat.model.CustomMessage;
import com.moxin.chat.model.FileMessage;
import com.moxin.chat.model.ImageMessage;
import com.moxin.chat.model.Message;
import com.moxin.chat.model.MessageFactory;
import com.moxin.chat.model.TextMessage;
import com.moxin.chat.model.UGCMessage;
import com.moxin.chat.model.VideoMessage;
import com.moxin.chat.model.VoiceMessage;
import com.moxin.chat.utils.FileUtil;
import com.moxin.chat.utils.MediaUtil;
import com.moxin.chat.utils.RecorderUtil;
import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.dialog.CuckooRewardCoinDialog;
import com.moxin.videoline.dialog.GiftBottomDialog;
import com.moxin.videoline.event.EventChatClickPrivateImgMessage;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequest;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestDoPrivateSendGif;
import com.moxin.videoline.json.JsonRequestPrivateChatPay;
import com.moxin.videoline.manage.RequestConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.custommsg.CustomMsgPrivateGift;
import com.moxin.videoline.modle.custommsg.CustomMsgPrivatePhoto;
import com.moxin.videoline.modle.custommsg.InputListenerMsgText;
import com.moxin.videoline.ui.PrivatePhotoActivity;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.SharedPreferencesUtils;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.lzy.okgo.callback.StringCallback;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.ext.message.TIMMessageDraft;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.qcloud.presentation.presenter.ChatPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ChatView;
import com.tencent.qcloud.ui.ChatInput;
import com.tencent.qcloud.ui.TemplateTitle;
import com.tencent.qcloud.ui.VoiceSendingView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class ChatActivity extends BaseActivity implements ChatView, View.OnClickListener, GiftBottomDialog.DoSendGiftListen, ChatAdapter.OnChatChildrenItemListen {
    @BindView(R.id.chat_attribute_card_view)
    CardView attributeCv;


    private static final String TAG = "ChatActivity";
    public static final int SEND_TEXT_MESSAGE = 1;
    public static final int SEND_VOICE_MESSAGE = 2;
    public static final int SEND_IMAGE_MESSAGE = 3;
    public static final int SEND_FILE_MESSAGE = 4;
    public static final int SEND_VIDEO_MESSAGE = 5;

    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private ChatPresenter presenter;
    private ChatInput input;
    private GiftBottomDialog giftBottomDialog;

    private ImageView mIvPrivateChat, mIvGift, mIvVideo;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private static final int VIDEO_RECORD = 500;

    public static final int RESULT_SELECT_PRIVATE_PHOTO = 0x11;
    private Uri fileUri;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private String userName;
    private String avatar;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private String titleStr;

    private int isPay;
    private String payCoin;
    private int sex;
    private int isAuth;
    private TemplateTitle title;
    private int follow;

    public static void navToChat(Context context, String identify, String userName, String avatar, int isPay, String payCoin, int sex, int is_auth, int follow, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("user_nickname", userName);
        intent.putExtra("avatar", avatar);
        intent.putExtra("type", type);
        intent.putExtra("is_pay", isPay);
        intent.putExtra("pay_coin", payCoin);
        intent.putExtra("sex", sex);
        intent.putExtra("is_auth", is_auth);
        intent.putExtra("follow", follow);
        context.startActivity(intent);
    }


    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.admin_color), 0);
        StatusBarUtil.setLightMode(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        identify = getIntent().getStringExtra("identify");
        userName = getIntent().getStringExtra("user_nickname");
        avatar = getIntent().getStringExtra("avatar");
        isPay = getIntent().getIntExtra("is_pay", 0);
        payCoin = getIntent().getStringExtra("pay_coin");
        follow = getIntent().getIntExtra("follow", -1);
        sex = getIntent().getIntExtra("sex", 1);
        isAuth = getIntent().getIntExtra("is_auth", isAuth);

        //TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C,identify);
        type = (TIMConversationType) getIntent().getSerializableExtra("type");


        mIvPrivateChat = findViewById(R.id.iv_private_img);
        mIvGift = findViewById(R.id.iv_gift);
        mIvVideo = findViewById(R.id.iv_video);
        findViewById(R.id.iv_reward).setOnClickListener(this);
        mIvPrivateChat.setOnClickListener(this);
        mIvGift.setOnClickListener(this);
        mIvVideo.setOnClickListener(this);

        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);
        input.setSex(SaveData.getInstance().getUserInfo().getSex());
//        input.setCoinData(StringUtils.toInt(payCoin), RequestConfig.getConfigObj().getCurrency());

        presenter = new ChatPresenter(this, identify, type);
        presenter.start();

        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        adapter.setChildrenListen(this);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView);
        title = (TemplateTitle) findViewById(R.id.chat_title);

        switch (type) {
            case C2C:
                title.setMoreImg(R.drawable.ic_chat_user_page);
                title.setMoreImgAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.jumpUserPage(ChatActivity.this, identify);
                    }
                });
                title.setTitleText(userName);
                break;

        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);


//        if (isAuth == 1) {
//            mIvGift.setVisibility(View.GONE);
//        }

        if (follow == 1) {
            attributeCv.setVisibility(View.GONE);
        } else {
            attributeCv.setVisibility(View.VISIBLE);
        }
        CuckooApplication.getInstances().setInPrivateChatPage(true);
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initPlayerDisplayData() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        //退出聊天界面时输入框有内容，保存草稿
        if (input.getText().length() > 0) {
            TextMessage message = new TextMessage(input.getText());
            presenter.saveDraft(message.getMessage());
        } else {
            presenter.saveDraft(null);
        }
//        RefreshEvent.getInstance().onRefresh();
        presenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
        CuckooApplication.getInstances().setInPrivateChatPage(false);
    }


    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        List<TIMElem> elems = new ArrayList<>();
        if (message != null) {
            for (int i = 0; i < message.getElementCount(); i++) {
                elems.add(message.getElement(i));
            }

        }

        //解析出来实体类不为空就是输入中那些消息
        SpannableStringBuilder string = getString(elems, this);
        InputListenerMsgText baseCommonBean = JSON.parseObject(string.toString(), InputListenerMsgText.class);

        //输入中，输入完
        if (baseCommonBean != null) {
            if ("EIMAMSG_InputStatus_Ing".equals(baseCommonBean.getActionParam())) {
                title.setTitleText(userName + " (正在输入中...)");
                return;
            } else if ("EIMAMSG_InputStatus_End".equals(baseCommonBean.getActionParam())) {
                title.setTitleText(userName);
                return;
            }
        } else {
            title.setTitleText(userName);
        }

        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (messageList.size() == 0) {
                    mMessage.setHasTime(null);
                } else {
                    mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
                }
                messageList.add(mMessage);
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount() - 1);

            }
        }

    }


    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < elems.size(); ++i) {
            switch (elems.get(i).getType()) {
                case Custom:
                    TIMCustomElem textElem = (TIMCustomElem) elems.get(i);
                    String str = new String(textElem.getData());
                    stringBuilder.append(str);
                    break;
            }

        }
        return stringBuilder;
    }


    /**
     * 显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted) {
                continue;
            }
            /*if (mMessage instanceof CustomMessage && LiveConstant.mapCustomMsgClass.get(((CustomMessage)mMessage).getType()) == null){
                continue;
            }*/
            ++newMsgNum;
            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                messageList.add(0, mMessage);
            } else {
                mMessage.setHasTime(null);
                messageList.add(0, mMessage);
            }
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(newMsgNum);
    }

    @Override
    public void showRevokeMessage(TIMMessageLocator timMessageLocator) {
        for (Message msg : messageList) {
            TIMMessageExt ext = new TIMMessageExt(msg.getMessage());
            if (ext.checkEquals(timMessageLocator)) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList) {
            if (msg.getMessage().getMsgUniqueId() == id) {
                switch (code) {
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }

        adapter.notifyDataSetChanged();

    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
        if (!checkSendMessage(SEND_IMAGE_MESSAGE)) {
            return;
        }
        doSwitchMessageSend(SEND_IMAGE_MESSAGE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    //发送私照
    private void sendPrivateImg(String imgId, String imgUrl) {

        CustomMsgPrivatePhoto img = new CustomMsgPrivatePhoto();
        img.setId(imgId);
        img.setImg(imgUrl);

        Message message = new CustomMessage(img, LiveConstant.CustomMsgType.MSG_PRIVATE_GIFT);
        presenter.sendMessage(message.getMessage());
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {

        if (TextUtils.isEmpty(input.getText())) {
            showToast("发送内容不能为空!");
            return;
        }

        if (!Utils.dirtyWordFilter(input.getText().toString())) {
            showToast("发送内容包含敏感词汇!");
            return;
        }

        if (!checkSendMessage(SEND_TEXT_MESSAGE)) {
            return;
        }

        doSwitchMessageSend(SEND_TEXT_MESSAGE);

    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        if (!checkSendMessage(SEND_FILE_MESSAGE)) {
            return;
        }
        doSwitchMessageSend(SEND_FILE_MESSAGE);
    }


    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {

        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (mCancelSend) {
            if (recorder.getTimeInterval() < 1) {
                Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
            } else if (recorder.getTimeInterval() > 60) {
                Toast.makeText(this, getResources().getString(R.string.chat_audio_too_long), Toast.LENGTH_SHORT).show();
            } else {

                if (!checkSendMessage(SEND_VOICE_MESSAGE)) {
                    return;
                }

                doSwitchMessageSend(SEND_VOICE_MESSAGE);
            }
        }

    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }

    private boolean mCancelSend = true;

    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice(boolean mCancelSend) {
        this.mCancelSend = mCancelSend;
    }

    //根据消息类型发送消息
    private void doSwitchMessageSend(int sendType) {

        switch (sendType) {
            case SEND_TEXT_MESSAGE: {
                Message message = new TextMessage(input.getText());
                presenter.sendMessage(message.getMessage());
                input.setText("");
                break;
            }
            case SEND_VOICE_MESSAGE: {
                Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
                presenter.sendMessage(message.getMessage());
                break;
            }
            case SEND_FILE_MESSAGE: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_CODE);
                break;
            }
            case SEND_IMAGE_MESSAGE: {
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;
            }

        }
    }

    //检测发送消息条件是否满足
    private boolean checkSendMessage(final int sendType) {

        if (isPay == 1 && StringUtils.toInt(payCoin) != 0) {

            //获取是否显示弹窗
            boolean canshow = (boolean) SharedPreferencesUtils.getParam(this, "canShowDialog", true);

            if (canshow) {

                new MaterialDialog.Builder(this)
                        .content("是否花费" + payCoin + RequestConfig.getConfigObj().getCurrency() + "发送付费消息？")
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //弹窗弹出后置为false
                                SharedPreferencesUtils.setParam(ChatActivity.this, "canShowDialog", false);

                                toChat(sendType);
                            }
                        })
                        .show();

                return false;
            } else {

                toChat(sendType);

                return false;
            }

        } else if (sex == 2 && isAuth != 1) {

            DialogHelp.getMessageDialog(ChatActivity.this, "女性需要认证才可以发送消息").show();
            return false;
        }

        return true;
    }

    private void toChat(final int sendType) {
        Api.doRequestChatPay(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), identify, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestPrivateChatPay data = (JsonRequestPrivateChatPay) JsonRequestBase.getJsonObj(s, JsonRequestPrivateChatPay.class);
                if (data.getCode() == 1) {
                    doSwitchMessageSend(sendType);
                } else if (data.getCode() == 10002) {
//                    Common.showRechargeDialog(ChatActivity.this, "余额不足，请先充值！");
                    Common.showRechargeDialog(ChatActivity.this, data.getMsg());
                } else {
                    ToastUtils.showShort(data.getMsg());
                }
            }
        });
    }


    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C) {
            //Message message = new CustomMessage(CustomMessage.Type.TYPING);
            //presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
    }

    @Override
    public void videoAction() {

        //  录制视频
        //Intent intent = new Intent(this, TCVideoRecordActivity.class);
        //startActivityForResult(intent, VIDEO_RECORD);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAction(int id) {
        if (id == R.id.btn_gift) {
            clickShowGift();
        } else if (id == R.id.btn_image) {

        } else if (id == R.id.btn_private_img) {

            //选择私照发送
            clickSelectPrivatePhoto();
        } else if (id == R.id.btn_video_call) {
            Common.callVideo(this, identify, 0);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()) {
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        } else if (message.getMessage().isSelf()) {
            menu.add(0, 4, Menu.NONE, getString(R.string.chat_pullback));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage) {
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                break;
            case 3:
                message.save();
                break;
            case 4:
                presenter.revokeMessage(message.getMessage());
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && fileUri != null) {
                showImagePreview(fileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists()) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    if (file.length() == 0 && options.outWidth == 0) {
                        Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                    } else {
                        if (file.length() > 1024 * 1024 * 10) {
                            Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                        } else {
                            Message message = new ImageMessage(path, isOri);
                            presenter.sendMessage(message.getMessage());
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == VIDEO_RECORD) {
            if (resultCode == RESULT_OK) {
                String videoPath = data.getStringExtra("videoPath");
                String coverPath = data.getStringExtra("coverPath");
                long duration = data.getLongExtra("duration", 0);
                Message message = new UGCMessage(videoPath, coverPath, duration);
                presenter.sendMessage(message.getMessage());
            }
        } else if (requestCode == RESULT_SELECT_PRIVATE_PHOTO) {
            if (resultCode == RESULT_OK) {
                sendPrivateImg(data.getStringExtra("img_id"), data.getStringExtra("img_url"));
            }
        }

    }


    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists()) {
            if (file.length() > 1024 * 1024 * 10) {
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                Message message = new FileMessage(path);
                presenter.sendMessage(message.getMessage());
            }
        } else {
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
            title.setTitleText(titleStr);
        }
    };

    @OnClick({R.id.chat_attribute_tv, R.id.chat_attribute_close_iv})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.chat_attribute_tv:
                requestFollow();
                break;
            case R.id.chat_attribute_close_iv:
                attributeCv.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    private void requestFollow() {
        Api.doLoveTheUser(
                identify,
                uId,
                uToken,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JsonRequest requestObj = JsonRequest.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            attributeCv.setVisibility(View.GONE);
                            showToastMsg(getString(R.string.follow_success));
                        } else {
                            ToastUtils.showLong(requestObj.getMsg());
                        }
                    }
                }
        );
    }

    //点击打赏礼物
    private void clickShowGift() {
        if (giftBottomDialog == null) {
            giftBottomDialog = new GiftBottomDialog(this, identify);
            if (isAuth == 1) {
                giftBottomDialog.hideMenu();
            }
            giftBottomDialog.setDoSendGiftListen(this);
        }
        giftBottomDialog.show();
    }

    //点击打赏金币
    private void clickReward() {
        new CuckooRewardCoinDialog(this).show();
    }

    //选择私照
    private void clickSelectPrivatePhoto() {

        Intent intent = new Intent(this, PrivatePhotoActivity.class);
        intent.putExtra(PrivatePhotoActivity.USER_ID, SaveData.getInstance().getId());
        intent.putExtra(PrivatePhotoActivity.USER_NAME, userName);
        intent.putExtra(PrivatePhotoActivity.ACTION_TYPE, 1);
        startActivityForResult(intent, RESULT_SELECT_PRIVATE_PHOTO);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventClickPrivateImg(EventChatClickPrivateImgMessage var1) {
        Common.requestSelectPic(this, var1.getId());
    }

    /**
     * 送礼物
     *
     * @param sendGif
     */
    @Override
    public void onSuccess(JsonRequestDoPrivateSendGif sendGif) {

        CustomMsgPrivateGift gift = new CustomMsgPrivateGift();
        gift.fillData(sendGif.getSend());
        Message message = new CustomMessage(gift, LiveConstant.CustomMsgType.MSG_PRIVATE_GIFT);
        presenter.sendMessage(message.getMessage());
        //giftBottomDialog.dismiss();
    }


    @Override
    public void onChildrenClick(int id) {
        switch (id) {
            case R.id.leftAvatar:

                Common.jumpUserPage(this, identify);
                break;
            case R.id.rightAvatar:

                Common.jumpUserPage(this, SaveData.getInstance().getId());
                break;
            default:
                break;
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
    }
}
