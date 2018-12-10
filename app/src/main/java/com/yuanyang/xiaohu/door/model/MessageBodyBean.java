package com.yuanyang.xiaohu.door.model;

import java.util.List;

public class MessageBodyBean {

    private String apkurl;
    private String build;
    private List<String> deletedcards;
    private List<String> addedcards;
    private String cardnos;
    private int  resetstatus;//1重置
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

    public String getCardnos() {
        return cardnos;
    }

    public void setCardnos(String cardnos) {
        this.cardnos = cardnos;
    }

    public int getResetstatus() {
        return resetstatus;
    }

    public void setResetstatus(int resetstatus) {
        this.resetstatus = resetstatus;
    }
}
