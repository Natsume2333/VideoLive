package com.moxin.chat.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.chat.model.Conversation;
import com.moxin.chat.utils.TimeUtil;
import com.moxin.videoline.R;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 会话界面adapter
 */
public class ConversationRvAdapter extends BaseItemDraggableAdapter<Conversation, BaseViewHolder> {

    private Context context;

    public ConversationRvAdapter(Context c, int resource, @Nullable List<Conversation> data) {
        super(resource, data);
        context = c;
    }

    @Override
    protected void convert(BaseViewHolder helper, Conversation data) {

//        final Conversation data = getItem(position);
        helper.setText(R.id.name, data.getName());

        Utils.loadHttpIconImg(context, Utils.getCompleteImgUrl(data.getAvatar()), (ImageView) helper.getView(R.id.avatar), 0);

        helper.setText(R.id.last_message, data.getLastMessageSummary());
        helper.setText(R.id.message_time, TimeUtil.getTimeStr(data.getLastMessageTime()));
        long unRead = data.getUnreadNum();
        if (unRead <= 0) {
            helper.setVisible(R.id.unread_num, false);
        } else {
            helper.setVisible(R.id.unread_num, true);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10) {
                helper.getView(R.id.unread_num).setBackground( context.getResources().getDrawable(R.drawable.point1));
            } else {
                helper.getView(R.id.unread_num).setBackground(  context.getResources().getDrawable(R.drawable.point2));
                if (unRead > 99) {
                    unReadStr = context.getResources().getString(R.string.time_more);
                }
            }
            helper.setText(R.id.unread_num, unReadStr);
        }
    }

}