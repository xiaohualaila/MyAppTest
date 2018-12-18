package com.yuanyang.xiaohu.door.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SharepreferenceBean {
    @Id(autoincrement = true)
    private Long id;
    private int open_door_num;
    private String open_door_direction_id;
    private String open_village_id;
    private int open_door_building;
    private int open_door_unit;
    private String open_door_params;
    @Generated(hash = 516770264)
    public SharepreferenceBean(Long id, int open_door_num,
            String open_door_direction_id, String open_village_id,
            int open_door_building, int open_door_unit, String open_door_params) {
        this.id = id;
        this.open_door_num = open_door_num;
        this.open_door_direction_id = open_door_direction_id;
        this.open_village_id = open_village_id;
        this.open_door_building = open_door_building;
        this.open_door_unit = open_door_unit;
        this.open_door_params = open_door_params;
    }
    @Generated(hash = 194759561)
    public SharepreferenceBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getOpen_door_num() {
        return this.open_door_num;
    }
    public void setOpen_door_num(int open_door_num) {
        this.open_door_num = open_door_num;
    }
    public String getOpen_door_direction_id() {
        return this.open_door_direction_id;
    }
    public void setOpen_door_direction_id(String open_door_direction_id) {
        this.open_door_direction_id = open_door_direction_id;
    }
    public String getOpen_village_id() {
        return this.open_village_id;
    }
    public void setOpen_village_id(String open_village_id) {
        this.open_village_id = open_village_id;
    }
    public int getOpen_door_building() {
        return this.open_door_building;
    }
    public void setOpen_door_building(int open_door_building) {
        this.open_door_building = open_door_building;
    }
    public int getOpen_door_unit() {
        return this.open_door_unit;
    }
    public void setOpen_door_unit(int open_door_unit) {
        this.open_door_unit = open_door_unit;
    }
    public String getOpen_door_params() {
        return this.open_door_params;
    }
    public void setOpen_door_params(String open_door_params) {
        this.open_door_params = open_door_params;
    }


}
