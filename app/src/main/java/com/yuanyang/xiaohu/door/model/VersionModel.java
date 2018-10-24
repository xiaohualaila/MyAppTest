package com.yuanyang.xiaohu.door.model;

public class VersionModel  {

    /**
     * id : bc27dbc9c5b111e7a42f6c92bf1b6721
     * vnumber : 3.1.2
     * vdetails : 多媒体双屏
     * download : null
     * createtime : 2017-11-10 09:00:00
     * status : 0
     * genre : 0
     */

    private String id;

    private String vnumber;

    private String vdetails;

    private String download;

    private String createtime;

    private int status;

    private int genre;

    private int build;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVnumber() {
        return vnumber;
    }

    public void setVnumber(String vnumber) {
        this.vnumber = vnumber;
    }

    public String getVdetails() {
        return vdetails;
    }

    public void setVdetails(String vdetails) {
        this.vdetails = vdetails;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }
}
