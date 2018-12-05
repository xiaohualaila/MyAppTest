package com.yuanyang.xiaohu.door.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bjw.bean.ComBean;
import com.bjw.utils.FuncUtil;
import com.bjw.utils.SerialHelper;
import com.yuanyang.xiaohu.door.bean.CardBean;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.CardModel;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.ChangeTool;
import com.yuanyang.xiaohu.door.util.Constants;
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

/**
 * 3288板子的
 */
public class ServiceA20 extends android.app.Service {

    private String openDoorLastData = "";

    private StringBuffer stringBuffer_ttyS1;
    private SerialHelper serialHelper_ttyS1;

    private StringBuffer stringBuffer_ttyS4;
    private SerialHelper serialHelper_ttyS4;

    private StringBuffer stringBuffer_ttyS5;
    private SerialHelper serialHelper_ttyS5;

    private StringBuffer stringBuffer_ttyS6;
    private SerialHelper serialHelper_ttyS6;

    private SerialHelper serialHelper;
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
        /**
         *  ttyS2  2号串口控制门禁
         */
        serialHelper = new SerialHelper() {
            @Override
            protected void onDataReceived(final ComBean comBean) {

            }
        };
        serialHelper.setPort(Constants.PORT_ttyS2);
        serialHelper.setBaudRate(Constants.BAUDRATE);
        /**
         *  ttyS1  1号扫码盒
         */
        stringBuffer_ttyS1 = new StringBuffer();
        serialHelper_ttyS1 = new SerialHelper() {
            @Override
            protected void onDataReceived(final ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyS1,1);
            }
        };
        serialHelper_ttyS1.setPort(Constants.PORT_ttyS1);
        serialHelper_ttyS1.setBaudRate(Constants.BAUDRATE);

        /**
         *  ttyS4  4号扫码盒
         */
        stringBuffer_ttyS4 = new StringBuffer();
        serialHelper_ttyS4 = new SerialHelper() {
            @Override
            protected void onDataReceived(final ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyS4,2);
            }
        };
        serialHelper_ttyS4.setPort(Constants.PORT_ttyS4);
        serialHelper_ttyS4.setBaudRate(Constants.BAUDRATE);

        /**
         *  ttyS5 5号扫码盒
         */
        stringBuffer_ttyS5 = new StringBuffer();
        serialHelper_ttyS5 = new SerialHelper() {
            @Override
            protected void onDataReceived(final ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyS5,3);
            }
        };
        serialHelper_ttyS5.setPort(Constants.PORT_ttyS5);
        serialHelper_ttyS5.setBaudRate(Constants.BAUDRATE);

        /**
         *  ttyS6  6号扫码盒
         */
        stringBuffer_ttyS6 = new StringBuffer();
        serialHelper_ttyS6 = new SerialHelper() {
            @Override
            protected void onDataReceived(final ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyS6,4);
            }
        };
        serialHelper_ttyS6.setPort(Constants.PORT_ttyS6);
        serialHelper_ttyS6.setBaudRate(Constants.BAUDRATE);
        try {
            serialHelper.open();
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("ttyS3串口打开失败"));
        }
        try {
            serialHelper_ttyS1.open();
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("ttyS1串口打开失败"));
        }
        try {
            serialHelper_ttyS4.open();
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("ttyS4串口打开失败"));
        }
        try {
            serialHelper_ttyS5.open();
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("ttyS5串口打开失败"));
        }
        try {
            serialHelper_ttyS6.open();
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("ttyS6串口打开失败"));
        }
    }

    private void dealMsg(ComBean comBean, StringBuffer stringBuffer,int scanBox) {
        String str = ChangeTool.decodeHexStr(FuncUtil.ByteArrToHex(comBean.bRec));
        Log.i("sss","sss>>>>>>>>"+ str + "  " + str.length());
        if (str.contains("&")) {
            stringBuffer.delete(0, stringBuffer.length());
            if(str.contains("&")&& str.contains("#")){
                dealCardNo(scanBox, str);
            }else {
                stringBuffer.append(str);
            }
        } else {
            if (str.contains("#")) {
                stringBuffer.append(str);
                String content = stringBuffer.toString();
                if(content.length() > 12){//小于等于12判断为卡，大于判断是二维码
                    content = content.substring(1, content.length()-1);
                    if (!content.equals(openDoorLastData)) {
                        openDoorLastData = content;
                        decryptData(content,scanBox);//解密
                    }else {
                        BusProvider.getBus().post(new EventModel("二维码已刷过！"));
                        SoundPoolUtil.play(4);
                    }
                }else {
                    dealCardNo(scanBox, content);
                }
            } else {
                stringBuffer.append(str);
            }
        }
    }


    private void dealCardNo(int scanBox, String str) {
        String card_no = str.substring(1, str.length()-1);
        CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
        CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(card_no)).unique();
        if (cardBean != null) {
            String params = AppSharePreferenceMgr.get(this, UserInfoKey.OPEN_DOOR_PARAMS, "[]").toString();
            List<AccessModel> list = GsonProvider.stringToList(params, AccessModel.class);
            AccessModel model = null;
            if (list.size() > 0) {
                model = getModel(list, scanBox);
            }
            openCardDoor(scanBox,card_no,model);
        }else {
            BusProvider.getBus().post(new EventModel("卡号不存在！"));
            SoundPoolUtil.play(4);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serialHelper_ttyS1.close();
        serialHelper_ttyS4.close();
        serialHelper_ttyS5.close();
        serialHelper_ttyS6.close();
        serialHelper.close();
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
                Log.i("sss", "门已开");
                checkIsOpenDoor(strings, num);
                SoundPoolUtil.play(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("sss", "开门失败");
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
     * 刷二维码开门
     */
    private void openDoor(final String[] strings, final AccessModel model) {
        final int num = model.getRelay();
        if (serialHelper.isOpen()) {
            serialHelper.send(getArrOpenDoor(num));
        } else {
            Log.i("sss","串口都没打开");
            return;
        }

        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                if (serialHelper.isOpen()) {
                    serialHelper.send(getArrCloseDoor(num));
                } else {
                    Log.i("sss","串口都没打开");
                }
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
     * 刷卡开门
     */
    private void openCardDoor(final int scanBox, String cardno, AccessModel model) {
        if (serialHelper.isOpen()) {
            serialHelper.send(getArrOpenDoor(scanBox));
        } else {
            Log.i("sss","串口都没打开");
            return;
        }

        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                serialHelper.send(getArrCloseDoor(scanBox));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                BusProvider.getBus().post(new CardModel(cardno, model));
            }
        });
    }

    //开门指令
    private byte[]  getArrOpenDoor(int num){
        /**四个继电器的*/
        byte[] sendArr = new byte[5];//打开继电器指令
        sendArr[0] = (byte) 0xFF;
        sendArr[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01); //0x25全开
        sendArr[2] = 0x01;
        sendArr[3] = (byte) (num == 1 ? 0x02 : num == 2 ? 0x03 : num == 3 ? 0x04 : num == 4 ? 0x05 : 0x02); //0x26全开
        sendArr[4] = (byte) 0xEE;
        return sendArr;
    }

    //关门指令
    private byte[]  getArrCloseDoor(int num){
        /**四个继电器的*/
        final byte[] sendArr_ = new byte[5];//复位继电器指令
        sendArr_[0] = (byte) 0xFF;
        sendArr_[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[2] = 0x00;
        sendArr_[3] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[4] = (byte) 0xEE;
        return sendArr_;
    }
}
