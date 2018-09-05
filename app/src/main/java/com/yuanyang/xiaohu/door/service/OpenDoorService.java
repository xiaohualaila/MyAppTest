package com.yuanyang.xiaohu.door.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.yuanyang.xiaohu.door.activity.MainActivity;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.MusicModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.net.BillboardApi;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.serialPortUtil.ChangeTool;
import com.yuanyang.xiaohu.door.serialPortUtil.ComBean;
import com.yuanyang.xiaohu.door.serialPortUtil.SerialPortHelper;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.GsonProvider;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.library.encrpt.Base64Utils;
import cn.com.library.encrpt.TDESUtils;
import cn.com.library.event.BusProvider;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class OpenDoorService extends Service {

    private SerialControl serialControlA, serialControlB;//串口

    private String openDoorLastData = "";

    public SendData sendData; //发送获取数据指令线程

    private boolean flag = true;

    private StringBuffer stringBuffer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        open_chuanko();
    }

    //打开串口
    public void open_chuanko() {
        serialControlA = new SerialControl();
        serialControlB = new SerialControl();

        serialControlA.setPort("/dev/ttyS2");//836 rs232
        serialControlA.setBaudRate(9600);
        OpenComPort(serialControlA);

        serialControlB.setPort("/dev/ttyS3");//836 ttl232
        serialControlB.setBaudRate(9600);
        OpenComPort(serialControlB);

        sendData = new SendData();
        sendData.start();

    }


    /**
     * 发送取值命令
     */
    private class SendData extends Thread {
        @Override
        public void run() {
            while (flag) {
                int  door_num = (int) AppSharePreferenceMgr.get(OpenDoorService.this, UserInfoKey.OPEN_DOOR_NUM,0);
                if (door_num > 0) {
                    for (int i = 0; i < door_num; i++) {
                        int j = i + 1;
                        sendPortData(serialControlA, ChangeTool.makeDataChecksum("01330" + j + "2123000000000000000000000000000303000000000000060101001000000301010010000003"));
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        CloseComPort(serialControlA);
        CloseComPort(serialControlB);
    }


    /**
     * 串口控制类
     */
    private class SerialControl extends SerialPortHelper {

        public SerialControl() {
            stringBuffer = new StringBuffer();
        }

        @Override
        protected void onDataReceived(ComBean ComRecData) {
            if (ComRecData.sComPort.equals("/dev/ttyS4")) {
                String call = ChangeTool.ByteArrToHex(ComRecData.bRec);
                if (call.contains("A1")) {
                    try {
                        //          AppPhoneMgr.callPhone(getV(), "18729903883");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (ComRecData.sComPort.equals("/dev/ttyS2")) {
//            } else if (ComRecData.sComPort.equals("/dev/ttyS1")) {
                String returnHex = ChangeTool.ByteArrToHex(ComRecData.bRec).replace(" ", "");
//                Log.i("sss",">>>>>>>>>" + returnHex);
                if (ComRecData.bRec.length > 8) {
                    stringBuffer.append(returnHex);
                    if (stringBuffer.toString().length() >= 212) {
                        int doorNum = Integer.parseInt(stringBuffer.toString().substring(4, 6));
                        Log.i("sss","第" + doorNum + " 门");
                        String openDoorData = stringBuffer.toString().substring(14, 206);
                        if (!openDoorData.equals(openDoorLastData)) {
                            openDoorLastData = openDoorData;
                            decryptData(ChangeTool.decodeHexStr(openDoorData), doorNum);//解密
                        }
                        stringBuffer.delete(0, stringBuffer.length());
                    }
                } else {
                    stringBuffer.delete(0, stringBuffer.length());
                }
            }
        }
    }


    /**
     * 解密数据、解析数据、
     */
    private void decryptData(String doorData, int num) {
        try {
        //    Log.i("sss","doorData====" + doorData);
            String data = new String(TDESUtils.decrypt(Base64Utils.decodeString2Byte(doorData), Base64Utils.decodeString2Byte("5kxi7J1zqHBAxAiwQ2GJwnVUH8JoFrqn")), "UTF-8");//身份证号
            Log.i("sss","data====" + data);//data 001,610103001,610103,001126,18392393600,00000000000,1532505747025
            String[] strings = data.split(",");
            if (System.currentTimeMillis() - Long.parseLong(strings[6]) > 1000 * 300) {
                BusProvider.getBus().post(new EventModel("二维码失效，请刷新二维码!"));
                BusProvider.getBus().post(new MusicModel(3));
            } else {
                Log.i("sss","检测是否门已开");
                checkIsOpenDoor(strings, num);
                BusProvider.getBus().post(new MusicModel(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("sss","号门开门失败");
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
        serialControlB.send(sendArr);//打开继电器
        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                serialControlB.send(sendArr_);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                BusProvider.getBus().post(new EventModel(strings[0].trim().equals("001") ? "Success!开门成功！" : "预约开门成功"));
                BusProvider.getBus().post(new UploadModel(strings,model));
            }
        });
    }


    //----------------------------------------------------打开串口
    private void OpenComPort(SerialPortHelper ComPort) {
        try {
            ComPort.open();
        } catch (SecurityException e) {
            BusProvider.getBus().post(new EventModel("打开串口失败:没有串口读/写权限!"));
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("打开串口失败:未知错误!"));
        } catch (InvalidParameterException e) {
            BusProvider.getBus().post(new EventModel("打开串口失败:参数错误!"));
        }
    }


    //----------------------------------------------------串口发送
    private void sendPortData(SerialPortHelper ComPort, String sOut) {
        if (ComPort != null && ComPort.isOpen()) {
            ComPort.sendHex(sOut);
        }
    }

    //----------------------------------------------------关闭串口
    private void CloseComPort(SerialPortHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }


}
