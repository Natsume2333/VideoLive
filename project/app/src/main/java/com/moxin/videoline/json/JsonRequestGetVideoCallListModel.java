package com.moxin.videoline.json;

import com.moxin.videoline.modle.CuckooVideoCallListModel;

import java.util.List;

public class JsonRequestGetVideoCallListModel extends JsonRequestBase{
    private List<CuckooVideoCallListModel> list;

    public List<CuckooVideoCallListModel> getList() {
        return list;
    }

    public void setList(List<CuckooVideoCallListModel> list) {
        this.list = list;
    }
}
