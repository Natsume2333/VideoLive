package com.moxin.videoline.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxin.videoline.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 魏鹏
 * email 1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 * @dw 分享Dialog
 */
public class InputTextDialog extends Dialog {

    @BindView(R.id.et_live_chat_input)
    protected EditText mChatInput;

    //chatInput
    @BindView(R.id.ll_live_chat_edit)
    protected LinearLayout mLiveChatEdit;

    @BindView(R.id.bt_send_chat)
    protected TextView send;

    private Context context;

    public InputTextDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_text_layout);//这行一定要写在前面
        ButterKnife.bind(this);

        setCancelable(true);//点击外部不可dismiss
        setCanceledOnTouchOutside(true);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 10); //消除边距
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        mChatInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatInput.requestFocus();
                InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null) manager.showSoftInput(mChatInput, 0);
            }
        }, 10);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSendChat();
            }
        });


    }


    private void clickSendChat() {
        final String trim = mChatInput.getText().toString().trim();
        sendMessage.onSendMsgListener(trim);
        dismiss();
    }


    SendMsgListener sendMessage;
    public void setSendMsgListener(SendMsgListener listener){
        sendMessage = listener;
    }
    public interface SendMsgListener {
        void onSendMsgListener(String msg);
    }

}