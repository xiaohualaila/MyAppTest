package com.yuanyang.xiaohu.door.present;


import android.util.Log;

import com.yuanyang.xiaohu.door.activity.AccessDoorActivity2;
import com.yuanyang.xiaohu.door.bean.CardBean;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.MessageBodyBean;
import com.yuanyang.xiaohu.door.net.BillboardApi;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.APKVersionCodeUtils;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;

import java.util.ArrayList;
import java.util.List;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;


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
    public void sendState(String mac,String ip){
                    BillboardApi.getDataService().sendState(mac,ip)
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
                                            CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
                                            int reset = bean.getResetstatus();
                                            /**
                                             *重置数据
                                             */
                                            if(reset==1){
                                                cardDao.deleteAll();
                                            }
                                            List<String> add_list = bean.getAddedcards();
                                            List<String> dele_list = bean.getDeletedcards();

                                            /**
                                             *往数据库中增加
                                             */
                                            if (add_list.size() > 0) {
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
                                                for (int i = 0; i < dele_list.size(); i++) {
                                                    CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(dele_list.get(i))).unique();
                                                    if (cardBean != null) {
                                                        cardDao.delete(cardBean);
                                                    }
                                                }
                                            }
                                            //查询card号
                                            String cards =bean.getCardnos();
                                            if(cards!=null){
                                                String[] strings =cards.split(",");
                                                if(strings.length>0){
                                                    List<String> ls = new ArrayList<>();
                                                    for(String s:strings){
                                                        CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(s)).unique();
                                                        if(cardBean != null){
                                                            ls.add("Y");
                                                        }else {
                                                            ls.add("N");
                                                        }
                                                    }
                                                    sendFindResult(mac,cards,listToString2(ls,","));
                                                }
                                            }

                                            Log.i("sss", "十分钟请求一次数据");
                                            //////////////////////////
                                            List<CardBean> ls = cardDao.queryBuilder().list();
//                                            Log.i("sss", "总数 " + ls.size());
//                                            if (ls.size() > 0) {
//                                                for (int i = 0; i < ls.size(); i++) {
//                                                    Log.i("sss", "sss" + ls.get(i).getNum());
//                                                }
//                                            }
                                            ///////////////////////
                                            sendDataBaseSize(mac,ls.size());
                                        }
                                    }else {
                                        getV().showToast(model.getDescribe());
                                    }
                                }
                            });

    }

    /**
     * 查询卡号
     * @param mac
     * @param cards
     * @param objects
     */
    private void sendFindResult(String mac, String cards, String objects) {
        BillboardApi.getDataService().sendfindResult(mac,cards,objects)
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
                        }
                    }
                });

    }

    public String listToString2(List list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                sb.append(list.get(i));
            } else {
                sb.append(list.get(i));
                sb.append(separator);
            }
        }
        return sb.toString();
    }


    /**
     * 发送重置后的数据
     */
    public void sendDataBaseSize(String mac,int size){
        BillboardApi.getDataService().sendDataBaseSize(mac,size)
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

                        }else {
                            getV().showToast(model.getDescribe());
                        }
                    }
                });

    }



    }