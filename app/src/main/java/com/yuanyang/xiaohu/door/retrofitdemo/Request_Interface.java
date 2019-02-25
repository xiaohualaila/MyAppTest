package com.yuanyang.xiaohu.door.retrofitdemo;

import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.DoorModel;
import com.yuanyang.xiaohu.door.model.MessageBodyBean;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Carson_Ho on 17/3/21.
 */
public interface Request_Interface {

    /**
     * 上报状态
     *
     * @param screenIP
     * @return
     */
    @POST("pc/multimedia/screen/completeMultimediaMessage")
    Observable<BaseBean> upState(@Query("id") String screenIP);

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
    Observable<BaseBean> uploadLog(@Query("memberMobile") String memberMobile,
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
    Observable<BaseBean> queryCard( @Query("devicemac") String devicemac,
                                  @Query("commid") String commid,
                                  @Query("cardno") String cardno);

    /**
     * 心跳发送服务器状态
     *
     * @return
     */
    @GET("system/card/selectDeviceCards")
    Observable<BaseBean<MessageBodyBean>> sendState(@Query("devicemac") String mac,
                                                  @Query("deviceip") String ip);


    @GET("system/door/getDeviceInfoByMac")
    Observable<BaseBean<DoorModel>> initData(@Query("devicemac") String mac);

    @POST("system/card/unResetDeviceDoor")
    Observable<BaseBean<MessageBodyBean>> sendDataBaseSize(@Query("devicemac") String mac,
                                                         @Query("affectrow") int size);

    /**
     * c查询结果上传
     */
    @POST("system/card/updateInquiryCards")
    Observable<BaseBean<MessageBodyBean>> sendfindResult(@Query("devicemac") String mac,
                                                       @Query("cardnos") String cardnos,
                                                       @Query("results") String results);

    /**
     * 上传因断网没有上传的开门记录数据
     * @param record
     * @return
     */
    @POST("")
    Observable<BaseBean<MessageBodyBean>> sendRecordLog(@Body RequestBody record);
}

