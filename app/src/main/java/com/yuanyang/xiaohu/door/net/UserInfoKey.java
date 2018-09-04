package com.yuanyang.xiaohu.door.net;

import com.yuanyang.xiaohu.door.App;
import com.yuanyang.xiaohu.door.util.SDCardUtil;

public class UserInfoKey {

    public static String JSON_DATA = SDCardUtil.getStoragePath(App.getContext());

    /** 多媒体*/
    public static String SCREEN_NUM = "screen_num";
    public static String MAIN_SCREEN_IP = "main_screen_ip"; //主屏ip
    public static String SUB_SCREEN_IP = "sub_screen_ip"; // 副屏ip
    public static String BIG_SCREEN_IP = "big_screen_ip"; // 大屏ip

    public static String MAIN_SCREEN_ID = "main_screen_id";//主屏id
    public static String SUB_SCREEN_ID = "sub_screen_id"; // 副屏id
    public static String BIG_SCREEN_ID = "sub_screen_id"; // 大屏id

    public static String MAIN_VIDEO_FILE = "main_video_file"; //主屏视频(本地存储地址)
    public static String MAIN_PICTURE_FILE = "main_picture_file";//主屏图片(本地存储地址)
    public static String SUB_VIDEO_FILE = "sub_video_file";//副屏视频(本地存储地址)
    public static String SUB_PICTURE_FILE = "sub_picture_file";//副屏图片(本地存储地址)
    public static String BIG_VIDEO_FILE = "big_video_file";//大屏视频(本地存储地址)
    public static String BIG_PICTURE_FILE = "big_picture_file";//大屏图片(本地存储地址)

    public static String MAIN_SHOW_VIDEO_URL = "main_show_video_url";//主屏视频(本地存储URL)
    public static String MAIN_SHOW_PICTURE_URL = "main_show_picture_url";//主屏图片(本地存储URL)
    public static String SUB_SHOW_VIDEO_URL = "sub_show_video_url"; //副屏视频(本地存储URL)
    public static String SUB_SHOW_PICTURE_URL = "sub_show_picture_url"; //副屏图片(本地存储URL)
    public static String BIG_SHOW_VIDEO_URL = "big_show_video_url"; //大屏视频(本地存储URL)
    public static String BIG_SHOW_PICTURE_URL = "big_show_picture_url"; //大屏图片(本地存储URL)

    public static String FILE_APK = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/apk";
    public static String FILE_MAIN_VIDEO = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/main/video";
    public static String FILE_MAIN_PICTURE = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/main/picture";
    public static String FILE_SUB_VIDEO = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/sub/video";
    public static String FILE_SUB_PICTURE = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/sub/picture";
    public static String FILE_BIG_VIDEO = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/big/video";
    public static String FILE_BIG_PICTURE = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/big/picture";

    /**门禁*/
    public static String OPEN_DOOR_PARAMS = "open_door_param"; // 门禁参数
    public static String OPEN_DOOR_BUILDING = "open_door_building"; // 楼栋号
    public static String OPEN_DOOR_VILLAGE_ID = "open_door_village_id"; // 小区ID
    public static String OPEN_DOOR_UNIT_ID = "open_door_unit_id"; // 单元ID
    public static String OPEN_DOOR_ROOM_ID = "open_door_room_id"; // 房间号
    public static String OPEN_DOOR_DIRECTION_ID = "open_door_direction_id"; // (大门)朝向
    public static String OPEN_DOOR_ENTER_EXIT_ID = "open_door_enter_exit_id"; // 进门或者出门

    /**拍照保存路径*/
    public static String BILLBOARD_PICTURE_PATH = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/camera"; // 拍照保存路径
    public static String BILLBOARD_VIDEO_PATH = SDCardUtil.getStoragePath(App.getContext()) + "/billboard/file/video"; // 视频保存路径
}
