package com.yuanyang.xiaohu.door.present;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.yuanyang.xiaohu.door.activity.AccessDoorActivity;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.CardBean;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.net.BillboardApi;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;

import java.util.concurrent.TimeUnit;

import cn.com.library.event.BusProvider;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class AccessPresent extends XPresent<AccessDoorActivity> {


    /**
     * 上传门禁日志
     */
    public void uploadLog(String[] strings, AccessModel model) {
        String directionDoor = AppSharePreferenceMgr.get(getV(), UserInfoKey.OPEN_DOOR_DIRECTION_ID, "").toString();
        BillboardApi.getDataService().uploadLog(strings[4], strings[5], strings[1], strings[2], strings[3], directionDoor, model.getAccessible(),
                "", "", "").compose(XApi.<BaseBean>getApiTransformer())
                .compose(XApi.<BaseBean>getScheduler())
                .compose(getV().<BaseBean>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastManager.showShort(getV(), "上传日志失败！");
                    }

                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            ToastManager.showShort(getV(), "上传日志成功！");
                        } else {
                            ToastManager.showShort(getV(), model.getDescribe());
                        }
                    }
                });
    }

    /**
     * 轮训判断是否有无数据
     */
    public void uploadDate() {
        String ANDROID_ID = getDeviceUniqID(getV());//202227054248108
        BillboardApi.getDataService().uploadDate(ANDROID_ID).compose(XApi.<BaseBean>getApiTransformer())
                .compose(XApi.<BaseBean>getScheduler())
                .compose(getV().<BaseBean>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean>() {
                    @Override
                    protected void onFail(NetError error) {
                      ToastManager.showShort(getV(), "更新数据失败！");
                        ObservableTimer();
                    }

                    @Override
                    public void onNext(BaseBean model) {


                        //TODO 对数据库信息进行增删改查
//                        CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
//                        cardDao.insert(new CardBean());
//
//                        GreenDaoManager.getInstance().getSession().getCardBeanDao().delete(null);
                        ObservableTimer();
                    }
                });
    }

    /** * 获取设备唯一ID * @param context * @return */
    @SuppressLint("MissingPermission")
    public static String getDeviceUniqID(Context context) {
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String unique_id ;
        unique_id = tm.getDeviceId();
        if (TextUtils.isEmpty(unique_id)) {
            unique_id=android.os.Build.SERIAL;
        }
        return unique_id;
    }
    
    private void ObservableTimer(){
        //10秒
        Observable.timer(10000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Long value) {
                
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                uploadDate();
                Log.i("sss","uploadDate>>>>>>>>>>>>>>>>>>>>>");
            }
        });
    }




}
