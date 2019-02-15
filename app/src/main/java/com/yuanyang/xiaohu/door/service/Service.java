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
import com.yuanyang.xiaohu.door.encrpt.Base64Utils;
import com.yuanyang.xiaohu.door.encrpt.TDESUtils;
import com.yuanyang.xiaohu.door.event.BusProvider;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.CardModel;
import com.yuanyang.xiaohu.door.model.CardNoModel;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.retrofitdemo.UserInfoKey;
import com.yuanyang.xiaohu.door.util.ChangeTool;
import com.yuanyang.xiaohu.door.util.Constants;
import com.yuanyang.xiaohu.door.util.SharedPreferencesUtil;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import com.yuanyang.xiaohu.door.util.SoundPoolUtil;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * orangePiZero
 */
public class Service extends android.app.Service {

    private String openDoorLastData = "";

    private SerialHelper serialHelper;

    private static Service instance;

    private String current_card="";
    private StringBuffer stringBuffer_ttyS2;
    public static Service getInstance(){
        if(instance == null){
            synchronized (Service.class){
                if (instance == null){
                    instance = new Service();
                }
            }
        }
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TAG","Services onStartCommand");
       return START_REDELIVER_INTENT;
    }

    /**
     * 初始化串口
     */
    private void init() {
        stringBuffer_ttyS2 = new StringBuffer();
        serialHelper = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyS2);
            }
        };
        serialHelper.setPort(Constants.PORT_ttyS2);
        serialHelper.setBaudRate(Constants.BAUDRATE);

        try {
            serialHelper.open();
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("ttyS2串口打开失败"));
        }
    }

    private void dealMsg(ComBean comBean, StringBuffer stringBuffer ) {
        String str = ChangeTool.decodeHexStr(FuncUtil.ByteArrToHex(comBean.bRec));
        Log.i("xxx",">>>>>>>>"+ str );
        String str1 = str;
        if (str.contains("&")) {
            stringBuffer.delete(0, stringBuffer.length());
            if(str.contains("&")&& str.contains("#")){
                int box = Integer.parseInt(str.substring(str.length()-2, str.length()-1));
                String code = str1.substring(1, str1.length()-2);
                dealCardNo(code,box);
            }else {
                stringBuffer.append(str);
            }
        } else {
            if (str.contains("#")) {
                stringBuffer.append(str);
                String content= stringBuffer.toString();
                String content1= stringBuffer.toString();
                Log.i("sss","读卡>>>>>>>>"+ content);
                int box = Integer.parseInt(content.substring(content.length()-2, content.length()-1));
                String code = content1.substring(1, content1.length()-2);
                if(code.length() > 13){//长度小于等于13位判断是刷卡否则是刷二维码
                    if (!code.equals(openDoorLastData)) {
                        openDoorLastData = code;
                        decryptData(code,box);//解密
                    }else {
                        BusProvider.getBus().post(new EventModel("二维码已刷过！"));
                        SoundPoolUtil.play(4);
                    }
                }else {
                    dealCardNo( content,box);
                }
            } else {
                stringBuffer.append(str);
            }
        }
    }

    /**
     * 处理卡号
     * @param str
     */
    public void dealCardNo(String str,int box) {
        Log.i("xxx","卡>>>>>>>>"+ str  + " box " +box);
        String params = SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_PARAMS, "[]");
        List<AccessModel> list = GsonProvider.stringToList(params, AccessModel.class);
        AccessModel model = null;
        if (list.size() > 0) {
            model = getModel(list, box);
        }

        CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
        CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(str)).unique();
        if (cardBean != null) {
            openCardDoor(str,model);
        }else {
            BusProvider.getBus().post(new CardNoModel(str,box,model));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serialHelper.close();
    }

    /**
     * 解密数据、解析数据、
     */
    private void decryptData(String doorData, int scanBox) {
        try {
            //    Log.i("sss","doorData====" + doorData);
            String data = new String(TDESUtils.decrypt(Base64Utils.decodeString2Byte(doorData), Base64Utils.decodeString2Byte("5kxi7J1zqHBAxAiwQ2GJwnVUH8JoFrqn")), "UTF-8");//身份证号
            //   Log.i("sss", "data====" + data);//data 001,610103001,610103,001126,18392393600,00000000000,1532505747025
            String[] strings = data.split(",");
            if (System.currentTimeMillis() - Long.parseLong(strings[6]) > 1000 * 300) {
                BusProvider.getBus().post(new EventModel("二维码失效，请刷新二维码!"));
                SoundPoolUtil.play(3);
            } else {
                checkIsOpenDoor(strings, scanBox);
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
    private void checkIsOpenDoor(String[] strings, int scanBox) {
        String village = SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_VILLAGE_ID, "");
        String directionDoor = SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_DIRECTION_ID, "");
        int building =  SharedPreferencesUtil.getInt(this, UserInfoKey.OPEN_DOOR_BUILDING, 0);
        String params = SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_PARAMS, "[]");
        List<AccessModel> list = GsonProvider.stringToList(params, AccessModel.class);
        AccessModel model = null;
        if (list.size() > 0) {
            model = getModel(list, scanBox);
        }
        if (!TextUtils.isEmpty(village) && !TextUtils.isEmpty(directionDoor) && list.size() > 0) {
            if (building != 0 ) {
                if (village.equals(strings[1]) && ((building + "" + model.getDoorNum()).equals(strings[2]))) {//测试 strings[1]610103001
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
        if (serialHelper.isOpen()) {
            serialHelper.send(getArrOpenDoor(model.getRelay()));
//            serialHelper.sendHex(model.getRelayOpen());
            Log.i("sss","门已打开！");
        } else {
            BusProvider.getBus().post(new EventModel("门禁串口都没打开"));
            return;
        }

        Observable.timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                //XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                if (serialHelper.isOpen()) {
                    serialHelper.send(getArrCloseDoor(model.getRelay()));
          //          serialHelper.sendHex(model.getRelayClose());
                } else {
                    BusProvider.getBus().post(new EventModel("串口都没打开"));
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
    public void openCardDoor(String cardno, AccessModel model) {
        if (serialHelper.isOpen()) {
            serialHelper.send(getArrOpenDoor(model.getRelay()));
        } else {
            Log.i("sss","门禁串口都没打开");
            BusProvider.getBus().post(new EventModel("门禁串口都没打开"));
            return;
        }

        Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                //XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                serialHelper.send(getArrCloseDoor(model.getRelay()));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                 if(current_card.equals(cardno)){
                    return;
                 }
                current_card=cardno;
                BusProvider.getBus().post(new CardModel(cardno, model));
            }
        });
    }
    /**
     *  开门指令
     */
    private byte[]  getArrOpenDoor(int num){
        /**四个继电器的*/
        byte[] sendArr = new byte[1];//打开继电器指令
        sendArr[0] = (byte) (num == 1 ? 0x11 : num == 2 ? 0x21 : num == 3 ? 0x31 : num == 4 ? 0x41 : 0x51); //0x51全开
        return sendArr;
    }

    /**
     * 关门指令
     */
    private byte[]  getArrCloseDoor(int num){
        /**四个继电器的*/
        final byte[] sendArr_ = new byte[1];//复位继电器指令
        sendArr_[0] = (byte) (num == 1 ? 0x10 : num == 2 ? 0x20 : num == 3 ? 0x30 : num == 4 ? 0x40 : 0x50) ;//0x50全关
        return sendArr_;
    }


}
