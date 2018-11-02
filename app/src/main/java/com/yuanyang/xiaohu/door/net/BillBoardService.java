package com.yuanyang.xiaohu.door.net;



import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.MessageBodyBean;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BillBoardService {



    /**
     * 上报状态
     *
     * @param screenIP
     * @return
     */
    @POST("pc/multimedia/screen/completeMultimediaMessage")
    Flowable<BaseBean> upState(@Query("id") String screenIP);

    @GET("/yykjZhCommunity/app/opendoor/addOpendoor")
    Flowable<BaseBean> uploadLog(@Query("memberMobile") String memberMobile,
                                 @Query("VistorMobile") String VistorMobile,
                                 @Query("ComID") String ComID,
                                 @Query("UnitID") String UnitID,
                                 @Query("ComdoorID") String ComdoorID,
                                 @Query("orientation") String orientation,
                                 @Query("category") String category,
                                 @Query("addr") String addr,
                                 @Query("lat") String lat,
                                 @Query("lng") String lng);

    /**
     * 心跳发送服务器状态
     *
     * @return
     */
    @GET("system/card/selectDeviceCards")
    Flowable<BaseBean<MessageBodyBean>> sendState(@Query("devicemac") String mac);

}
