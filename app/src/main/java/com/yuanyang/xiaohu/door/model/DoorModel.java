package com.yuanyang.xiaohu.door.model;

public class DoorModel {
    /**
     * unit : 一单元
     * unitno : 1
     * build : 1#
     * name : 远洋门禁
     * commid : 610103001
     * gatetype : 2
     * commname : 东新世纪广场
     * buildno : 1
     */

    private String unit;
    private int unitno;
    private String build;
    private String name;
    private String commid;
    private int gatetype;
    private String commname;
    private String heartinterval;
    private int buildno;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getUnitno() {
        return unitno;
    }

    public void setUnitno(int unitno) {
        this.unitno = unitno;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommid() {
        return commid;
    }

    public void setCommid(String commid) {
        this.commid = commid;
    }

    public int getGatetype() {
        return gatetype;
    }

    public void setGatetype(int gatetype) {
        this.gatetype = gatetype;
    }

    public String getCommname() {
        return commname;
    }

    public void setCommname(String commname) {
        this.commname = commname;
    }

    public int getBuildno() {
        return buildno;
    }

    public String getHeartinterval() {
        return heartinterval;
    }

    public void setHeartinterval(String heartinterval) {
        this.heartinterval = heartinterval;
    }

    public void setBuildno(int buildno) {
        this.buildno = buildno;
    }
}
