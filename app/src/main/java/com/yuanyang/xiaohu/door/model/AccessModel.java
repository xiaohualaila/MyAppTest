package com.yuanyang.xiaohu.door.model;

public class AccessModel {

    private int erCode; // 扫码盒号码
    private int relay;

    private String doorNum; // 第几个门

    private String accessible; // 0 进  1 出

    public int getErCode() {
        return erCode;
    }

    public void setErCode(int erCode) {
        this.erCode = erCode;
    }

    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    public String getDoorNum() {
        return doorNum;
    }

    public void setDoorNum(String doorNum) {
        this.doorNum = doorNum;
    }

    public String getAccessible() {
        return accessible;
    }

    public void setAccessible(String accessible) {
        this.accessible = accessible;
    }
}
