package com.moxin.videoline.json;

/**
 * Created by 魏鹏 on 2018/3/26.
 * email:1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 */

public class JsonRequestCheckIsCharging extends JsonRequestBase{
    private int is_need_charging;
    private String video_deduction;
    private String resolving_power;
    private int free_time;

    public int getFree_time() {
        return free_time;
    }

    public void setFree_time(int free_time) {
        this.free_time = free_time;
    }

    public String getResolving_power() {
        return resolving_power;
    }

    public void setResolving_power(String resolving_power) {
        this.resolving_power = resolving_power;
    }

    public String getVideo_deduction() {
        return video_deduction;
    }

    public void setVideo_deduction(String video_deduction) {
        this.video_deduction = video_deduction;
    }

    public int getIs_need_charging() {
        return is_need_charging;
    }

    public void setIs_need_charging(int is_need_charging) {
        this.is_need_charging = is_need_charging;
    }
}
