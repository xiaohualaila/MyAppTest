package com.yuanyang.xiaohu.door.present;


import android.util.Log;

import com.yuanyang.xiaohu.door.activity.AccessDoorActivity2;
import com.yuanyang.xiaohu.door.bean.CardBean;
import com.yuanyang.xiaohu.door.bean.SharepreferenceBean;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.MessageBodyBean;
import com.yuanyang.xiaohu.door.net.BillboardApi;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.APKVersionCodeUtils;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import com.yuanyang.xiaohu.door.util.NetStateUtil;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.SharepreferenceBeanDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


public class AccessPresent2 extends XPresent<AccessDoorActivity2> {

    /**
     * 上传门禁日志 --扫描
     */
    public void uploadLog(String[] strings,String mac, AccessModel model) {
        String directionDoor = AppSharePreferenceMgr.get(getV(), UserInfoKey.OPEN_DOOR_DIRECTION_ID, "").toString();
        BillboardApi.getDataService().uploadLog(strings[4], strings[5], strings[1], strings[2], strings[3], directionDoor, model.getAccessible(),
                "", "", "","",mac,"1")
                .compose(XApi.<BaseBean>getApiTransformer())
                .compose(XApi.<BaseBean>getScheduler())
                .compose(getV().<BaseBean>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastManager.showShort(getV(), "上传日志失败！");
                        XLog.e("上传日志失败！");
                    }

                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            XLog.e("上传日志成功！");
                            ToastManager.showShort(getV(), "上传日志成功！");
                        } else {
                            ToastManager.showShort(getV(), model.getDescribe());
                        }
                    }
                });
    }
    /**
     * 上传门禁日志 --刷卡
     */
    public void uploadCardLog(String cardNo,String mac, AccessModel model) {
        String directionDoor = AppSharePreferenceMgr.get(getV(), UserInfoKey.OPEN_DOOR_DIRECTION_ID, "").toString();
        BillboardApi.getDataService().uploadLog("", "", "", "", "",
                directionDoor, model.getAccessible(),
                "", "", "",cardNo,mac,"2")
                .compose(XApi.<BaseBean>getApiTransformer())
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
     * 心跳
     */
    public void sendState(){
        //10分钟
         Observable.interval(0,10, TimeUnit.MINUTES).
                subscribeOn(Schedulers.io()).
                subscribe(num -> {
                    String macAddress = NetStateUtil.getMacAddress();
                    if(macAddress==null){
                      getV().showToast("网络无法连接");
                        return;
                    }
                    BillboardApi.getDataService().sendState(macAddress)
                            .compose(XApi.<BaseBean<MessageBodyBean>>getApiTransformer())
                            .compose(XApi.<BaseBean<MessageBodyBean>>getScheduler())
                            .compose(getV().<BaseBean<MessageBodyBean>>bindToLifecycle())
                            .subscribe(new ApiSubscriber<BaseBean>() {
                                @Override
                                protected void onFail(NetError error) {
                                    getV().showError(error);
                                }

                                @Override
                                public void onNext(BaseBean model) {
                                    if (model.isSuccess()) {
                                        MessageBodyBean bean = (MessageBodyBean) model.getMessageBody();
                                        String s_ver = bean.getBuild();
                                        int v_no = APKVersionCodeUtils.getVersionCode(getV());
                                        if(s_ver != null){
                                            int a = Integer.parseInt(s_ver);
                                            if(a > v_no){
                                                //更新app
                                                getV().updateVersion(bean.getApkurl(),s_ver);
                                                return;
                                            }
                                        }
//                                        Log.i("sss",  new Gson().toJson(model));
                                        if (bean != null) {
                                            List<String> add_list = bean.getAddedcards();
                                            List<String> dele_list = bean.getDeletedcards();
                                            /**
                                             *往数据库中增加
                                             */
                                            if (add_list.size() > 0) {
                                                CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
                                                CardBean card = null;
                                                for (int i = 0; i < add_list.size(); i++) {
                                                    CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(add_list.get(i))).unique();
                                                    if (cardBean == null) {
                                                        card = new CardBean(null, add_list.get(i));
                                                        cardDao.insert(card);
                                                    }
                                                }
                                            }
                                            /**
                                             * 从数据库中删除
                                             */
                                            if (dele_list.size() > 0) {
                                                CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
                                                for (int i = 0; i < dele_list.size(); i++) {
                                                    CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(dele_list.get(i))).unique();
                                                    if (cardBean != null) {
                                                        cardDao.delete(cardBean);
                                                    }
                                                }
                                            }
                                            Log.i("sss", "十分钟请求一次数据");
//                                            List<CardBean> ls = GreenDaoManager.getInstance().getSession().getCardBeanDao().queryBuilder().list();
//                                            Log.i("sss", "总数 " + ls.size());
//                                            if (ls.size() > 0) {
//                                                for (int i = 0; i < ls.size(); i++) {
//                                                    Log.i("sss", "sss" + ls.get(i).getNum());
//                                                }
//                                            }
                                        }
                                    }else {
                                        getV().showToast(model.getDescribe());
                                    }
                                }
                            });
                });
    }

    public  void initDate() {
//        BillboardApi.getDataService().sendState(macAddress)
//                .compose(XApi.<BaseBean<MessageBodyBean>>getApiTransformer())
//                .compose(XApi.<BaseBean<MessageBodyBean>>getScheduler())
//                .compose(getV().<BaseBean<MessageBodyBean>>bindToLifecycle())
//                .subscribe(new ApiSubscriber<BaseBean>() {
//                    @Override
//                    protected void onFail(NetError error) {
//                        getV().showError(error);
//                    }
//
//                    @Override
//                    public void onNext(BaseBean model) {
//
//                    }
        List<AccessModel> list = new ArrayList<>();
        AccessModel model = new AccessModel();
        model.setErCode(1);
        model.setRelay(0);
        model.setDoorNum("1");
        model.setAccessible("进");
        AccessModel model2 = new AccessModel();
        model2.setErCode(2);
        model2.setRelay(0);
        model2.setDoorNum("2");
        model2.setAccessible("出");
        AccessModel model3 = new AccessModel();
        model3.setErCode(3);
        model3.setRelay(0);
        model3.setDoorNum("3");
        model3.setAccessible("进");
        AccessModel model4 = new AccessModel();
        model4.setErCode(4);
        model4.setRelay(0);
        model4.setDoorNum("4");
        model4.setAccessible("出");
        list.add(model);
        list.add(model2);
        list.add(model3);
        list.add(model4);

        AppSharePreferenceMgr.put(getV(), UserInfoKey.OPEN_DOOR_NUM, list.size());
        AppSharePreferenceMgr.put(getV(), UserInfoKey.OPEN_DOOR_VILLAGE_ID, "610103001");//小区编号
        AppSharePreferenceMgr.put(getV(), UserInfoKey.OPEN_DOOR_DIRECTION_ID, "东门");//门
        AppSharePreferenceMgr.put(getV(), UserInfoKey.OPEN_DOOR_BUILDING, "");//单元门
        AppSharePreferenceMgr.put(getV(), UserInfoKey.OPEN_DOOR_PARAMS, GsonProvider.getInstance().getGson().toJson(list));

        SharepreferenceBeanDao dao = GreenDaoManager.getInstance().getSession().getSharepreferenceBeanDao();
        List<SharepreferenceBean> list_dao = dao.queryBuilder().list();
        if(list_dao.size()>0){
            dao.deleteAll();
        }

        SharepreferenceBean sharepreferenceBean = new SharepreferenceBean();
        sharepreferenceBean.setOpen_door_num("4");
        sharepreferenceBean.setOpen_village_id("610103001");
        sharepreferenceBean.setOpen_door_direction_id("东门");
        sharepreferenceBean.setOpen_door_building( "");
        sharepreferenceBean.setOpen_door_params( GsonProvider.getInstance().getGson().toJson(list));
        dao.insert(sharepreferenceBean);
        getV().initViewData();
    }

}
