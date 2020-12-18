package com.moxin.videoline.base;


public class GlobContents {
    static GlobContents instance = null;

    private void Singleton() {
    }

    public static GlobContents getInstance() {

        if (instance == null) {

            instance = new GlobContents();

        }

        return instance;

    }


    public boolean isShowDialog = true;

}
