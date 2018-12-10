package com.yuanyang.xiaohu.door.net;

import com.yuanyang.xiaohu.door.App;
import com.yuanyang.xiaohu.door.util.SDCardUtil;

public class UserInfoKey {

    public static String JSON_DATA = SDCardUtil.getStoragePath(App.getContext());

    /**门禁*/
    public static String OPEN_DOOR_PARAMS = "open_door_param"; // 门禁参数
    public static String OPEN_DOOR_BUILDING = "open_door_building"; // 楼栋号
    public static String OPEN_DOOR_VILLAGE_ID = "open_door_village_id"; // 小区ID
    public static String OPEN_DOOR_UNIT_ID = "open_door_unit_id"; // 单元ID
    public static String OPEN_DOOR_ROOM_ID = "open_door_room_id"; // 房间号
    public static String OPEN_DOOR_DIRECTION_ID = "open_door_direction_id"; // (大门)朝向
    public static String OPEN_DOOR_ENTER_EXIT_ID = "open_door_enter_exit_id"; // 进门或者出门
    public static String OPEN_DOOR_NUM = "open_door_num"; // 保存门的个数
    public static String VILLAGE_NAME = "village_name"; //
    public static String HEARTINTERVAL= "heartinterval"; // 心跳时间
}
