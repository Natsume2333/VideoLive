package com.moxin.videoline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.moxin.videoline.R;
import com.moxin.videoline.modle.FriendsBean;
import com.moxin.videoline.utils.FriendUtils;
import com.moxin.videoline.utils.Utils;

import java.util.List;

/**
 * Created by weipeng on 2017/5/9.
 */

public class UserAdapter extends BaseAdapter {
    private Context mContext;
    private List<FriendsBean.DataBean> list;
    private int imageViewHeight;
    private int imageViewWidth;


    public UserAdapter(Context mContext, List<FriendsBean.DataBean> mCardList) {
        this.mContext = mContext;
        this.list = mCardList;
    }


    public void setUserData(List<FriendsBean.DataBean> mCardList) {
        list.clear();
        list.addAll(mCardList);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        try {
            return list.get(position);
        } catch (Exception e) {
            return new FriendsBean.DataBean();
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View itemView, ViewGroup parent) {
        ViewHolder holder = null;
        if (itemView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder();

            holder.avatarImageView = (ImageView) itemView.findViewById(R.id.item_iv_pic);
            holder.item_tv_address = (TextView) itemView.findViewById(R.id.item_tv_address);
            holder.item_tv_introduce = (TextView) itemView.findViewById(R.id.item_tv_introduce);

            holder.item_tv_detail_info = (TextView) itemView.findViewById(R.id.item_tv_detail_info);
            holder.item_tv_name = (TextView) itemView.findViewById(R.id.item_tv_name);
            holder.item_iv_vip = (ImageView) itemView.findViewById(R.id.item_iv_vip);

            holder.sexBac = itemView.findViewById(R.id.sex_bac);
            holder.constellationLl = (LinearLayout) itemView.findViewById(R.id.constellation_ll);
            holder.vipLl = (LinearLayout) itemView.findViewById(R.id.tv_vip_ll);
            holder.age = (TextView) itemView.findViewById(R.id.tv_age);
            holder.isMeet = (TextView) itemView.findViewById(R.id.is_meet_tv);
            holder.sex = (ImageView) itemView.findViewById(R.id.im_sex);
            holder.constellation = (TextView) itemView.findViewById(R.id.tv_constellation);

            holder.allLl = (LinearLayout) itemView.findViewById(R.id.all_ll);

            holder.bottomInfoLL = itemView.findViewById(R.id.item_bottom_info_ll);

            holder.superLikeMeLl = itemView.findViewById(R.id.super_like_me_ll);
            itemView.setTag(holder);

        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        final FriendsBean.DataBean item = list.get(position);

        Utils.loadImg(Utils.getCompleteImgUrl(item.getAvatar()), holder.avatarImageView);

        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        holder.avatarImageView.measure(w, h);

        imageViewHeight = holder.avatarImageView.getMeasuredHeight();
        imageViewWidth = holder.avatarImageView.getMeasuredWidth();


        holder.item_tv_name.setText(item.getUser_nickname());


        //年龄
        String[] time = FriendUtils.getTime(item.getBirthday());
        if (time.length > 0) {
            int age = FriendUtils.getAge(time[0]);
            holder.age.setText(age + "");
        }

        if (item.getSex().equals("1")) {
            holder.sex.setImageResource(R.drawable.man);
            holder.sexBac.setBackgroundResource(R.drawable.bg_blue_sex);
        } else if (item.getSex().equals("2")) {
            holder.sex.setImageResource(R.drawable.woman);
            holder.sexBac.setBackgroundResource(R.drawable.bg_blue_woman_sex);
        }
        //星座
        if (time.length > 2) {
            String astro = FriendUtils.getAstro(Integer.parseInt(time[1]), Integer.parseInt(time[2]));
            holder.constellation.setText(astro);
        }

//
////        //职业 最后一次在线时间 距离
//        StringBuilder stringBuilder = new StringBuilder();
//        if (!TextUtils.isEmpty(item.getIndustry_info())) {
//            stringBuilder.append(item.getIndustry_info());
//        }
//
//        if (!TextUtils.isEmpty(item.getLast_login_time())) {
//            if (TextUtils.isEmpty(stringBuilder.toString().trim())) {
//                stringBuilder.append(item.getLast_login_time());
//            } else {
//                stringBuilder.append(" ,  " + item.getLast_login_time());
//            }
//        }
//
//        if (!TextUtils.isEmpty(item.getDistance() + "")) {
//            if (TextUtils.isEmpty(stringBuilder.toString().trim())) {
//                stringBuilder.append(item.getDistance() + "km");
//            } else {
//                stringBuilder.append(" ,  " + item.getDistance() + "km");
//            }
//        }
//
//        holder.item_tv_introduce.setText(stringBuilder.toString());
//
//
//        //vip
//        if (item.getIs_vip() == 1) {
//            holder.vipLl.setVisibility(View.VISIBLE);
//        } else {
//            holder.vipLl.setVisibility(View.GONE);
//        }
//
//
//        if (item.getIs_meet() == 1) {
//            holder.isMeet.setVisibility(View.VISIBLE);
//        } else {
//            holder.isMeet.setVisibility(View.GONE);
//        }
//
//        //1 认证
//        if ("1".equals(item.getIsauto())) {
//            holder.item_tv_name.setTextColor(Color.parseColor("#FF007F"));
//        } else {
//            holder.item_tv_name.setTextColor(Color.parseColor("#202020"));
//        }


        return itemView;
    }


    class ViewHolder {
        ImageView avatarImageView;
        TextView item_tv_address;
        TextView item_tv_introduce;
        TextView item_tv_detail_info;
        TextView item_tv_name;

        ImageView item_iv_vip;


        ImageView sex;
        TextView age, isMeet;
        TextView constellation;
        LinearLayout allLl, sexBac, vipLl, constellationLl;

        LinearLayout bottomInfoLL;
        LinearLayout superLikeMeLl;
    }

    public MatchmakerClickListener imgClickListener;

    public void setMatchmakerClickListener(MatchmakerClickListener imgClickListener) {
        this.imgClickListener = imgClickListener;
    }

    public interface MatchmakerClickListener {
        void onMatchmakerClickListener(int position);

    }

}
