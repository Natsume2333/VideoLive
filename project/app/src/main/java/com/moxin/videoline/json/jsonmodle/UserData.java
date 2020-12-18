package com.moxin.videoline.json.jsonmodle;

import com.moxin.videoline.modle.UserImgModel;

import java.util.ArrayList;

/**
 * 用户基本信息
 */

public class UserData {

    private String id;//用户id
    private String token;//用户token

    private String user_nickname;//用户名
    private String avatar;//用户头像地址
    private int sex;//用户性别##0保密-1男-2女
    private String is_change_name;
    private String sign;
    private ArrayList<UserImgModel> img;//用户轮播图地址0,img
    private String height;
    private String weight;
    private String constellation;
    private String introduce;
    private String image_label;


    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getImage_label() {
        return image_label;
    }

    public void setImage_label(String image_label) {
        this.image_label = image_label;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getIs_change_name() {
        return is_change_name;
    }

    public void setIs_change_name(String is_change_name) {
        this.is_change_name = is_change_name;
    }
    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getSex() {
        return sex;
    }

    public ArrayList<UserImgModel> getImg() {
        return img;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setImg(ArrayList<UserImgModel> img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", user_nickname='" + user_nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", img=" + img +
                '}';
    }
}
