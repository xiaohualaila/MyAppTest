package com.yuanyang.xiaohu.door.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bjw.utils.FuncUtil;
import com.bjw.utils.SerialHelper;
import com.yuanyang.xiaohu.door.bean.CardBean;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.ChangeTool;
import com.yuanyang.xiaohu.door.util.Constants;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import com.yuanyang.xiaohu.door.util.SoundPoolUtil;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.library.encrpt.Base64Utils;
import cn.com.library.encrpt.TDESUtils;
import cn.com.library.event.BusProvider;
import cn.com.library.log.XLog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class Service extends android.app.Service {

    private String openDoorLastData = "";

    private StringBuffer stringBuffer;
    private SerialHelper serialHelper;
    private SerialHelper serialHelperScan;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    /**
     * 初始化串口
     */
    private void init() {
        serialHelper = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {

            }
        };
        serialHelper.setPort(Constants.PORT);
        serialHelper.setBaudRate(Constants.BAUDRATE);
        stringBuffer = new StringBuffer();
        serialHelperScan = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {
                String str = ChangeTool.decodeHexStr(FuncUtil.ByteArrToHex(comBean.bRec));
                if (str.substring(str.length() - 2, str.length()).equals("##")) {
                    String mm = str.substring(2, str.length());
                    mm = mm.substring(0, mm.length() - 2);
                    CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
                    CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(mm)).unique();
                    if (cardBean != null) {
                        openDoor(1);
                    }
                } else {
                    if (str.contains("&&")) {
                        stringBuffer.append(str);
                    } else {
                        String sss = str.substring(str.length() - 2, str.length() - 1);
                        if (str.substring(str.length() - 2, str.length() - 1).contains("#")) {
                            stringBuffer.append(str);
                            String content = stringBuffer.toString();
                            String which_door = content.substring(content.length() - 1, content.length());
                            content = content.substring(2, content.length());
                            content = content.substring(0, content.length() - 2);
                            if (!content.equals(openDoorLastData)) {
                                openDoorLastData = content;
                                decryptData(content, Integer.parseInt(which_door));//解密
                            }
                            Log.i("xxxx", ">>>>>>>>" + content);
                            stringBuffer.delete(0, stringBuffer.length());
                        } else {
                            stringBuffer.append(str);
                        }
                    }
                }
            }
        };

        serialHelperScan.setPort(Constants.PORT_SCAN);
        serialHelperScan.setBaudRate(Constants.BAUDRATE_SCAN);
        try {
            serialHelper.open();
            serialHelperScan.open();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("sss", e.getMessage());
            BusProvider.getBus().post(new EventModel("串口打开失败"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serialHelper.close();
        serialHelperScan.close();
    }

    /**
     * 解密数据、解析数据、
     */
    private void decryptData(String doorData, int num) {
        try {
            //    Log.i("sss","doorData====" + doorData);
            String data = new String(TDESUtils.decrypt(Base64Utils.decodeString2Byte(doorData), Base64Utils.decodeString2Byte("5kxi7J1zqHBAxAiwQ2GJwnVUH8JoFrqn")), "UTF-8");//身份证号
            //   Log.i("sss", "data====" + data);//data 001,610103001,610103,001126,18392393600,00000000000,1532505747025
            String[] strings = data.split(",");
            if (System.currentTimeMillis() - Long.parseLong(strings[6]) > 1000 * 300) {
                BusProvider.getBus().post(new EventModel("二维码失效，请刷新二维码!"));
                SoundPoolUtil.play(3);
            } else {
                Log.i("sss", "检测是否门已开");
                checkIsOpenDoor(strings, num);
                SoundPoolUtil.play(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("sss", "号门开门失败");
            BusProvider.getBus().post(new EventModel("号门开门失败"));
        }
    }

    /**
     * 判断是否开门
     */
    private void checkIsOpenDoor(String[] strings, int num) {
        String village = AppSharePreferenceMgr.get(this, UserInfoKey.OPEN_DOOR_VILLAGE_ID, "").toString();
        String directionDoor = AppSharePreferenceMgr.get(this, UserInfoKey.OPEN_DOOR_DIRECTION_ID, "").toString();
        String building = AppSharePreferenceMgr.get(this, UserInfoKey.OPEN_DOOR_BUILDING, "").toString();
        String params = AppSharePreferenceMgr.get(this, UserInfoKey.OPEN_DOOR_PARAMS, "[]").toString();
        List<AccessModel> list = GsonProvider.stringToList(params, AccessModel.class);
        AccessModel model = null;
        if (list.size() > 0) {
            model = getModel(list, num);
        }
        if (!TextUtils.isEmpty(village) && !TextUtils.isEmpty(directionDoor) && list.size() > 0) {
            if (!TextUtils.isEmpty(building)) {
                if (village.equals(strings[1]) && ((building + model.getDoorNum()).equals(strings[2]))) {//测试 strings[1]610103001
                    openDoor(strings, model);
                }
            } else {
                if (village.equals(strings[1])) {
                    openDoor(strings, model);
                } else {
                    BusProvider.getBus().post(new EventModel("资料匹配失败，请确认小区是否正确"));
                }
            }
        } else {
            BusProvider.getBus().post(new EventModel("主板未参数未设置，请设置主板参数"));
        }
    }

    /**
     * 获取当前扫码盒对应的数据
     *
     * @param list 扫码盒列表
     * @param door 扫码盒号
     * @return
     */
    private AccessModel getModel(List<AccessModel> list, int door) {
        AccessModel model = null;
        for (AccessModel accessModel : list) {
            if (accessModel.getErCode() == door) {
                model = accessModel;
                break;
            }
        }
        return model;
    }

    /**
     * 开门代码
     */
    private void openDoor(final String[] strings, final AccessModel model) {
        int num = model.getRelay();
        /**四个继电器的*/
        byte[] sendArr = new byte[5];//打开继电器指令
        sendArr[0] = (byte) 0xFF;
        sendArr[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01); //0x25全开
        sendArr[2] = 0x01;
        sendArr[3] = (byte) (num == 1 ? 0x02 : num == 2 ? 0x03 : num == 3 ? 0x04 : num == 4 ? 0x05 : 0x02); //0x26全开
        sendArr[4] = (byte) 0xEE;
        final byte[] sendArr_ = new byte[5];//复位继电器指令
        sendArr_[0] = (byte) 0xFF;
        sendArr_[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[2] = 0x00;
        sendArr_[3] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[4] = (byte) 0xEE;
        serialHelper.send(sendArr);
        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                serialHelper.send(sendArr_);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                BusProvider.getBus().post(new EventModel(strings[0].trim().equals("001") ? "Success!开门成功！" : "预约开门成功"));
                BusProvider.getBus().post(new UploadModel(strings, model));
            }
        });
    }

    /**
     * 开门代码
     */
    private void openDoor(int num) {
        /**四个继电器的*/
        byte[] sendArr = new byte[5];//打开继电器指令
        sendArr[0] = (byte) 0xFF;
        sendArr[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01); //0x25全开
        sendArr[2] = 0x01;
        sendArr[3] = (byte) (num == 1 ? 0x02 : num == 2 ? 0x03 : num == 3 ? 0x04 : num == 4 ? 0x05 : 0x02); //0x26全开
        sendArr[4] = (byte) 0xEE;
        final byte[] sendArr_ = new byte[5];//复位继电器指令
        sendArr_[0] = (byte) 0xFF;
        sendArr_[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[2] = 0x00;
        sendArr_[3] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[4] = (byte) 0xEE;
        serialHelper.send(sendArr);
        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                serialHelper.send(sendArr_);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                BusProvider.getBus().post(new EventModel("开门成功！"));
            }
        });
    }

}
