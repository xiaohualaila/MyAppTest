package com.yuanyang.xiaohu.door.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CardRecord {
    @Id(autoincrement = true)
    private Long id;

    private String no;
    private String directionDoor;
    private String accessible;
    @Generated(hash = 1874454121)
    public CardRecord(Long id, String no, String directionDoor, String accessible) {
        this.id = id;
        this.no = no;
        this.directionDoor = directionDoor;
        this.accessible = accessible;
    }
    @Generated(hash = 19461391)
    public CardRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNo() {
        return this.no;
    }
    public void setNo(String no) {
        this.no = no;
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
