package com.yuanyang.xiaohu.door.present;

import android.util.Log;

import com.yuanyang.xiaohu.door.activity.AccessDoorActivity;
import com.yuanyang.xiaohu.door.bean.CardBean;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.MessageBodyBean;
import com.yuanyang.xiaohu.door.net.BillboardApi;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.NetStateUtil;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.library.event.BusProvider;
import cn.com.library.kit.ToastManager;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


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
     * 心跳
     */
    public void sendState(){
        //10分钟
        Observable.interval(10, TimeUnit.MINUTES).
                subscribeOn(Schedulers.io()).
                subscribe(new Consumer<Long>() {
                    @Override public void accept(Long num) throws Exception {
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
                                                List<CardBean> ls = GreenDaoManager.getInstance().getSession().getCardBeanDao().queryBuilder().list();
                                                if (ls.size() > 0) {
                                                    for (int i = 0; i < ls.size(); i++) {
                                                        Log.i("sss", "sss" + ls.get(i).getNum());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });

                    }
                });
    }



}
