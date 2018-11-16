package com.yuanyang.xiaohu.door.model;

import java.util.List;

public class MessageBodyBean {

    private String apkurl;
    private String build;
    private List<String> deletedcards;
    private List<String> addedcards;

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public List<String> getDeletedcards() {
        return deletedcards;
    }

    public void setDeletedcards(List<String> deletedcards) {
        this.deletedcards = deletedcards;
    }

    public List<String> getAddedcards() {
        return addedcards;
    }

    public void setAddedcards(List<String> addedcards) {
        this.addedcards = addedcards;
    }
}
