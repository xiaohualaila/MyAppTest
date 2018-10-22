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
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.ChangeTool;
import com.yuanyang.xiaohu.door.util.Constants;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import com.yuanyang.xiaohu.door.util.IOUtil;
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

    private StringBuffer stringBuffer_ttyS1;
    private SerialHelper serialHelper_ttyS1;

    private StringBuffer stringBuffer_ttyS4;
    private SerialHelper serialHelper_ttyS4;

    private StringBuffer stringBuffer_ttyXRM0;
    private SerialHelper serialHelper_ttyXRM0;

    private StringBuffer stringBuffer_ttyXRM1;
    private SerialHelper serialHelper_ttyXRM1;

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
         *  ttyS1  1号扫码盒
         */
        stringBuffer_ttyS1 = new StringBuffer();
        serialHelper_ttyS1 = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyS1,1);
            }
        };

        serialHelper_ttyS1.setPort(Constants.PORT_ttyS1);
        serialHelper_ttyS1.setBaudRate(Constants.BAUDRATE);


        /**
         *  ttyS4  2号扫码盒
         */
        stringBuffer_ttyS4 = new StringBuffer();
        serialHelper_ttyS4 = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyS4,2);
            }
        };

        serialHelper_ttyS4.setPort(Constants.PORT_ttyS4);
        serialHelper_ttyS4.setBaudRate(Constants.BAUDRATE);

        /**
         *  ttyXRM0  3号扫码盒
         */
        stringBuffer_ttyXRM0 = new StringBuffer();
        serialHelper_ttyXRM0 = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyXRM0,3);
            }
        };

        serialHelper_ttyXRM0.setPort(Constants.PORT_ttyXRM0);
        serialHelper_ttyXRM0.setBaudRate(Constants.BAUDRATE);

        /**
         *  ttyXRM1  4号扫码盒
         */
        stringBuffer_ttyXRM1 = new StringBuffer();
        serialHelper_ttyXRM1 = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {
                dealMsg(comBean, stringBuffer_ttyXRM1,4);
            }
        };

        serialHelper_ttyXRM1.setPort(Constants.PORT_ttyXRM1);
        serialHelper_ttyXRM1.setBaudRate(Constants.BAUDRATE);

        try {
            serialHelper_ttyS1.open();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("sss", e.getMessage());
            BusProvider.getBus().post(new EventModel("串口打开失败"));
        }
        try {
            serialHelper_ttyS4.open();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("sss", e.getMessage());
            BusProvider.getBus().post(new EventModel("串口打开失败"));
        }
        try {
            serialHelper_ttyXRM0.open();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("sss", e.getMessage());
            BusProvider.getBus().post(new EventModel("串口打开失败"));
        }
        try {
            serialHelper_ttyXRM1.open();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("sss", e.getMessage());
            BusProvider.getBus().post(new EventModel("串口打开失败"));
        }


    }

    private void dealMsg(ComBean comBean, StringBuffer stringBuffer,int scanBox) {
        String str = ChangeTool.decodeHexStr(FuncUtil.ByteArrToHex(comBean.bRec));
        Log.i("sss","sss>>>>>>>>"+ str + "  " + str.length());

            if (str.contains("&&")) {
                stringBuffer.delete(0, stringBuffer.length());
                if(str.contains("&&")&& str.contains("##")){
                    dealCardNo(scanBox, str);
                }else {
                    stringBuffer.append(str);
                }
            } else {
                if (str.substring(str.length() - 2, str.length() - 1).contains("#")) {
                    stringBuffer.append(str);

                    String content = stringBuffer.toString();
                    if(content.length() > 14){
                        content = content.substring(2, content.length());
                        content = content.substring(0, content.length() - 2);
                        if (!content.equals(openDoorLastData)) {
                            openDoorLastData = content;
                            //   decryptData(content, Integer.parseInt(which_door));//解密
                            decryptData(content,scanBox);//解密
                            Log.i("sss",">>>>>>>>>>二维码");
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
        Log.i("sss",">>>>>>>>>>卡 "+ str);
        String mm = str.substring(2, str.length());
        mm = mm.substring(0, mm.length() - 2);
        CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
        CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(mm)).unique();
        if (cardBean != null) {
            openDoor(scanBox);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serialHelper_ttyS1.close();
        serialHelper_ttyS4.close();
        serialHelper_ttyXRM0.close();
        serialHelper_ttyXRM1.close();
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
        final int num = model.getRelay();
        IOUtil.door_io_1(num);
        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                IOUtil.door_io_0(num);
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
    private void openDoor(final int num) {
        /**四个继电器的*/
        IOUtil.door_io_1(num);
        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                IOUtil.door_io_0(num);
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
