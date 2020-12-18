package com.moxin.videoline.json;

import com.moxin.videoline.modle.CuckooUserEvaluateListModel;

import java.util.List;

public class JsonRequestDoGetUserPageInfo extends JsonRequestBase {

    private List<CuckooUserEvaluateListModel> evaluate_list;
    private String signature;

    private String height;
    private String weight;
    private String constellation;
    private String introduce;
    private List<String> image_label;
    private String city;
    private int sex;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public List<String> getImage_label() {
        return image_label;
    }

    public void setImage_label(List<String> image_label) {
        this.image_label = image_label;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<CuckooUserEvaluateListModel> getEvaluate_list() {
        return evaluate_list;
    }

    public void setEvaluate_list(List<CuckooUserEvaluateListModel> evaluate_list) {
        this.evaluate_list = evaluate_list;
    }
}
