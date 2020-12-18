package com.moxin.videoline.json;

import com.moxin.videoline.modle.DynamicCommonListModel;

import java.util.List;

public class JsonRequestDoGetDynamicCommonList extends JsonRequestBase {
    private List<DynamicCommonListModel> list;

    public List<DynamicCommonListModel> getList() {
        return list;
    }

    public void setList(List<DynamicCommonListModel> list) {
        this.list = list;
    }
}
