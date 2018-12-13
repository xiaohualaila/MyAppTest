package com.yuanyang.xiaohu.door.net;

import cn.com.library.net.XApi;

public class BillboardApi {

   public static String API_BASE_URL = "http://www.xazhsq.cn:8080/yykjZhCommunity/";
  //  public static String API_BASE_URL = "http://192.168.1.33:8080/yykjZhCommunity/";//测试的

    private static BillBoardService billBoardService;

    public static BillBoardService getDataService() {
        if (billBoardService == null) {
            synchronized (XApi.class) {
                if (billBoardService == null) {
                    billBoardService = XApi.getInstance().getRetrofit(API_BASE_URL, true).create(BillBoardService.class);
                }
            }
        }
        return billBoardService;
    }

}
