package com.moxin.videoline.modle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LablesBean {

    /**
     * code : 1
     * msg :
     * data : [{"id":1,"name":"形象名称","sort":15,"type":1,"checked":"0未选中1选中"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;
    private String image_label;

    public String getImage_label() {
        return image_label;
    }

    public void setImage_label(String image_label) {
        this.image_label = image_label;
    }

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * name : 形象名称
         * sort : 15
         * type : 1
         * checked : 0未选中1选中
         */

        private int id;
        @SerializedName("label_name")
        private String name;
        @SerializedName("orderno")
        private int sort;
        private int type;
        private String checked;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getChecked() {
            return checked;
        }

        public void setChecked(String checked) {
            this.checked = checked;
        }
    }
}
