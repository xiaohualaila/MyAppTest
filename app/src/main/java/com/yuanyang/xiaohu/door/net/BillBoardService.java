package com.yuanyang.xiaohu.door.net;



import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.DoorModel;
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

    /**
     * 上班开门日志
     * @param memberMobile
     * @param VistorMobile
     * @param ComID
     * @param UnitID
     * @param ComdoorID
     * @param orientation
     * @param category
     * @param addr
     * @param lat
     * @param lng
     * @param cardno
     * @param devicemac
     * @param type
     * @return
     */
    @GET("app/opendoor/addOpendoor")
    Flowable<BaseBean> uploadLog(@Query("memberMobile") String memberMobile,
                                 @Query("VistorMobile") String VistorMobile,
                                 @Query("ComID") String ComID,
                                 @Query("UnitID") String UnitID,
                                 @Query("ComdoorID") String ComdoorID,
                                 @Query("orientation") String orientation,
                                 @Query("category") String category,
                                 @Query("addr") String addr,
                                 @Query("lat") String lat,
                                 @Query("lng") String lng,
                                 @Query("cardno") String cardno,
                                 @Query("devicemac") String devicemac,
                                 @Query("type") String type);

    /**
     * 查询卡号
     * @param devicemac
     * @param commid
     * @param cardno
     * @return
     */
    @GET("system/card/cardExitInComm")
    Flowable<BaseBean> queryCard( @Query("devicemac") String devicemac,
                                 @Query("commid") String commid,
                                  @Query("cardno") String cardno);

    /**
     * 心跳发送服务器状态
     *
     * @return
     */
    @GET("system/card/selectDeviceCards")
    Flowable<BaseBean<MessageBodyBean>> sendState(@Query("devicemac") String mac,
                                                  @Query("deviceip") String ip);


    @GET("system/door/getDeviceInfoByMac")
    Flowable<BaseBean<DoorModel>> initData(@Query("devicemac") String mac);

    @POST("system/card/unResetDeviceDoor")
    Flowable<BaseBean<MessageBodyBean>> sendDataBaseSize(@Query("devicemac") String mac,
                                                  @Query("affectrow") int size);

    /**
     * c查询结果上传
     */
    @POST("system/card/updateInquiryCards")
    Flowable<BaseBean<MessageBodyBean>> sendfindResult(@Query("devicemac") String mac,
                                                       @Query("cardnos") String cardnos,
                                                       @Query("results") String results);
}
