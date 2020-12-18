package com.moxin.videoline.modle;

import java.util.List;

public class MyLevelBean {

    /**
     * code : 1
     * msg :
     * level_my : {"level_name":"当前等级","split":"获取提成比例","msum":"获取充值金币和消费金币总数","down_name":"下一个等级","spread":"距离下一等级还差数","progress":"进度条数"}
     * level : [{"levelid":"等级id","level_name":"等级名称","level_up":"男性的充值等级","split":"0","addtime":1522139312,"level_up_female":"女性的收益等级"}]
     */

    private int code;
    private String msg;
    private LevelMyBean level_my;
    private List<LevelBean> level;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LevelMyBean getLevel_my() {
        return level_my;
    }

    public void setLevel_my(LevelMyBean level_my) {
        this.level_my = level_my;
    }

    public List<LevelBean> getLevel() {
        return level;
    }

    public void setLevel(List<LevelBean> level) {
        this.level = level;
    }

    public static class LevelMyBean {
        /**
         * level_name : 当前等级
         * split : 获取提成比例
         * msum : 获取充值金币和消费金币总数
         * down_name : 下一个等级
         * spread : 距离下一等级还差数
         * progress : 进度条数
         */

        private String level_name;
        private String split;
        private String msum;
        private String down_name;
        private String spread;
        private String progress;

        public String getLevel_name() {
            return level_name;
        }

        public void setLevel_name(String level_name) {
            this.level_name = level_name;
        }

        public String getSplit() {
            return split;
        }

        public void setSplit(String split) {
            this.split = split;
        }

        public String getMsum() {
            return msum;
        }

        public void setMsum(String msum) {
            this.msum = msum;
        }

        public String getDown_name() {
            return down_name;
        }

        public void setDown_name(String down_name) {
            this.down_name = down_name;
        }

        public String getSpread() {
            return spread;
        }

        public void setSpread(String spread) {
            this.spread = spread;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }
    }

    public static class LevelBean {
        /**
         * levelid : 等级id
         * level_name : 等级名称
         * level_up : 男性的充值等级
         * split : 0
         * addtime : 1522139312
         * level_up_female : 女性的收益等级
         */

        private String levelid;
        private String level_name;
        private String level_up;
        private String split;
        private int addtime;
        private String level_up_female;

        public String getLevelid() {
            return levelid;
        }

        public void setLevelid(String levelid) {
            this.levelid = levelid;
        }

        public String getLevel_name() {
            return level_name;
        }

        public void setLevel_name(String level_name) {
            this.level_name = level_name;
        }

        public String getLevel_up() {
            return level_up;
        }

        public void setLevel_up(String level_up) {
            this.level_up = level_up;
        }

        public String getSplit() {
            return split;
        }

        public void setSplit(String split) {
            this.split = split;
        }

        public int getAddtime() {
            return addtime;
        }

        public void setAddtime(int addtime) {
            this.addtime = addtime;
        }

        public String getLevel_up_female() {
            return level_up_female;
        }

        public void setLevel_up_female(String level_up_female) {
            this.level_up_female = level_up_female;
        }
    }
}
