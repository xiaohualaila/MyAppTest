package com.yuanyang.xiaohu.door.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class RecordLogModel {
    @Id(autoincrement = true)
    private Long id;
    private String memberMobile;
    private String vistorMobile;
    private String comID;
    private String unitID;
    private String comdoorID;
    private String orientation;
    private String category;
    private String addr;
    private String lat;
    private String lng;
    private String cardno;
    private String devicemac;
    private String type;
    private String date;
    @Generated(hash = 1953041634)
    public RecordLogModel(Long id, String memberMobile, String vistorMobile,
            String comID, String unitID, String comdoorID, String orientation,
            String category, String addr, String lat, String lng, String cardno,
            String devicemac, String type, String date) {
        this.id = id;
        this.memberMobile = memberMobile;
        this.vistorMobile = vistorMobile;
        this.comID = comID;
        this.unitID = unitID;
        this.comdoorID = comdoorID;
        this.orientation = orientation;
        this.category = category;
        this.addr = addr;
        this.lat = lat;
        this.lng = lng;
        this.cardno = cardno;
        this.devicemac = devicemac;
        this.type = type;
        this.date = date;
    }
    @Generated(hash = 969730380)
    public RecordLogModel() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMemberMobile() {
        return this.memberMobile;
    }
    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
    }
    public String getVistorMobile() {
        return this.vistorMobile;
    }
    public void setVistorMobile(String vistorMobile) {
        this.vistorMobile = vistorMobile;
    }
    public String getComID() {
        return this.comID;
    }
    public void setComID(String comID) {
        this.comID = comID;
    }
    public String getUnitID() {
        return this.unitID;
    }
    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }
    public String getComdoorID() {
        return this.comdoorID;
    }
    public void setComdoorID(String comdoorID) {
        this.comdoorID = comdoorID;
    }
    public String getOrientation() {
        return this.orientation;
    }
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getAddr() {
        return this.addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getLat() {
        return this.lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLng() {
        return this.lng;
    }
    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getCardno() {
        return this.cardno;
    }
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }
    public String getDevicemac() {
        return this.devicemac;
    }
    public void setDevicemac(String devicemac) {
        this.devicemac = devicemac;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }


}
    
