package com.yuanyang.xiaohu.door.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class CodeRecord {
    @Id(autoincrement = true)
    private Long id;

    private String phoneNum;
    private String str1;
    private String str2;
    private String str3;
    private String str4;
    private String directionDoor;
    private String accessible;
    @Generated(hash = 717160759)
    public CodeRecord(Long id, String phoneNum, String str1, String str2,
            String str3, String str4, String directionDoor, String accessible) {
        this.id = id;
        this.phoneNum = phoneNum;
        this.str1 = str1;
        this.str2 = str2;
        this.str3 = str3;
        this.str4 = str4;
        this.directionDoor = directionDoor;
        this.accessible = accessible;
    }
    @Generated(hash = 1462233196)
    public CodeRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPhoneNum() {
        return this.phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public String getStr1() {
        return this.str1;
    }
    public void setStr1(String str1) {
        this.str1 = str1;
    }
    public String getStr2() {
        return this.str2;
    }
    public void setStr2(String str2) {
        this.str2 = str2;
    }
    public String getStr3() {
        return this.str3;
    }
    public void setStr3(String str3) {
        this.str3 = str3;
    }
    public String getStr4() {
        return this.str4;
    }
    public void setStr4(String str4) {
        this.str4 = str4;
    }
    public String getDirectionDoor() {
        return this.directionDoor;
    }
    public void setDirectionDoor(String directionDoor) {
        this.directionDoor = directionDoor;
    }
    public String getAccessible() {
        return this.accessible;
    }
    public void setAccessible(String accessible) {
        this.accessible = accessible;
    }


}
