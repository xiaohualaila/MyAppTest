package com.yuanyang.xiaohu.door.activity.access;


import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.google.gson.Gson;
import com.yuanyang.xiaohu.door.base.BasePresenter;
import com.yuanyang.xiaohu.door.bean.CardBean;
import com.yuanyang.xiaohu.door.bean.CardRecord;
import com.yuanyang.xiaohu.door.bean.CodeRecord;
import com.yuanyang.xiaohu.door.bean.RecordLogModel;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.MessageBodyBean;
import com.yuanyang.xiaohu.door.retrofitdemo.UserInfoKey;
import com.yuanyang.xiaohu.door.retrofitdemo.Request_Interface;
import com.yuanyang.xiaohu.door.retrofitdemo.RetrofitManager;
import com.yuanyang.xiaohu.door.service.Service3288;
import com.yuanyang.xiaohu.door.service.Service836;
import com.yuanyang.xiaohu.door.service.ServiceA20;
import com.yuanyang.xiaohu.door.util.APKVersionCodeUtils;
import com.yuanyang.xiaohu.door.util.SharedPreferencesUtil;
import com.yuanyang.xiaohu.door.util.SoundPoolUtil;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CodeRecordDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.RecordLogModelDao;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class AccessPresent2  extends BasePresenter implements AccessContract.Presenter {

    private AccessContract.View view;

    public AccessPresent2(AccessContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }
    /**
     * 上传门禁日志 --扫描二维码开门
     */
    public void uploadLog(Context context, String[] strings, String mac, AccessModel model) {
        String directionDoor = SharedPreferencesUtil.getString(context, UserInfoKey.OPEN_DOOR_DIRECTION_ID, "");
        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.uploadLog(strings[4], strings[5], strings[1], strings[2], strings[3], directionDoor, model.getAccessible(),
                "", "", "","",mac,"1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("sss","上传日志失败！");
                        Date currentTime = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yy:MM:dd HH:mm:ss");
                        String dateString = formatter.format(currentTime);
                        RecordLogModel recordLogModel = new RecordLogModel(null,strings[4], strings[5], strings[1], strings[2], strings[3], directionDoor, model.getAccessible(),
                                "", "", "","",mac,"1",dateString);
                        RecordLogModelDao dao = GreenDaoManager.getInstance().getSession().getRecordLogModelDao();
                        dao.insert(recordLogModel);
                    }
                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            Log.i("sss","上传日志成功！");

                        } else {
                            Log.i("sss","上传日志失败！");
                        }

                    }
                });
    }

    /**
     * 上传门禁日志 --刷卡开门
     */
    public void uploadCardLog(Context context,String cardNo, String mac, AccessModel model) {
        String directionDoor = SharedPreferencesUtil.getString(context, UserInfoKey.OPEN_DOOR_DIRECTION_ID, "");
        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.uploadLog("", "", "", "", "",
                directionDoor, model.getAccessible(), "", "", "", cardNo, mac, "2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("sss","上传日志失败！");
                        Date currentTime = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yy:MM:dd HH:mm:ss");
                        String dateString = formatter.format(currentTime);
                        RecordLogModel recordLogModel = new RecordLogModel(null,"", "", "", "", "",
                                directionDoor, model.getAccessible(), "", "", "", cardNo, mac, "2",dateString);
                        RecordLogModelDao dao = GreenDaoManager.getInstance().getSession().getRecordLogModelDao();
                        dao.insert(recordLogModel);
                    }
                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            Log.i("sss","上传日志成功！");

                        } else {
                            Log.i("sss","上传日志失败！");
                        }

                    }
                });
    }


    /**
     * 心跳
     */
    public void sendState(String mac, String ip) {

        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.sendState(mac, ip)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage() + " 发送心跳失败！");
                    }
                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            MessageBodyBean bean = (MessageBodyBean) model.getMessageBody();
                            String s_ver = bean.getBuild();
                            int v_no = APKVersionCodeUtils.getVersionCode(AccessDoorActivity2.instance());
                            if (s_ver != null) {
                                int a = Integer.parseInt(s_ver);
                                if (a > v_no) {
                                    //更新app
                                    view.updateVersion(bean.getApkurl(), s_ver);
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
                                if (reset == 1) {
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
                                String cards = bean.getCardnos();
                                if (cards != null) {
                                    String[] strings = cards.split(",");
                                    if (strings.length > 0) {
                                        List<String> ls = new ArrayList<>();
                                        for (String s : strings) {
                                            CardBean cardBean = cardDao.queryBuilder().where(CardBeanDao.Properties.Num.eq(s)).unique();
                                            if (cardBean != null) {
                                                ls.add("Y");
                                            } else {
                                                ls.add("N");
                                            }
                                        }
                                        sendFindResult(mac, cards, listToString2(ls, ","));
                                    }
                                }

                                Log.i("sss", "十分钟请求一次数据");
                                //////////////////////////
                                List<CardBean> ls = cardDao.queryBuilder().list();
                                Log.i("sss", "总数 " + ls.size());
//                                if (ls.size() > 0) {
//                                    for (int i = 0; i < ls.size(); i++) {
//                                        Log.i("sss", "保存的卡" + ls.get(i).getNum());
//                                    }
//                                }
                                ///////////////////////
                                sendDataBaseSize(mac, ls.size());
                            }
                        } else {
                            view.showError(model.getDescribe());
                        }
                    }


                });
    }

    @Override
    public void uploadRecordLog() {
        List<RecordLogModel> ls = GreenDaoManager.getInstance().getSession().getRecordLogModelDao().queryBuilder().list();
        if (ls.size()== 0) {
            return;
        }
        Gson gsons = new Gson();
        String postInfoStr = gsons.toJson(ls);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),postInfoStr);
        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.sendRecordLog(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                    }
                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                           GreenDaoManager.getInstance().getSession().getRecordLogModelDao().deleteAll();//上传成功删除数据库所有数据
                        } else {
                            view.showError(model.getDescribe());
                        }
                    }
                });
    }

    /**
     * 发送重置后的数据
     */
    public void sendDataBaseSize(String mac, int size) {
        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.sendDataBaseSize(mac, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                    }
                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {

                        } else {
                            view.showError(model.getDescribe());
                        }

                    }
                });
    }

    /**
     * 从服务器查询卡号
     * @param mac
     * @param cardNo
     * @param box
     * @param accessModel
     */
    public void queryServer(Context context,String mac, String cardNo,int box,AccessModel accessModel) {
        String village_id = SharedPreferencesUtil.getString(context, UserInfoKey.OPEN_DOOR_VILLAGE_ID, "");
        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.queryCard(mac,village_id, cardNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                    }
                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            String save = (String) model.getMessageBody();
                            if(save.equals("Y")){
                                Log.i("sss","请求的服务器Y");
                                String banzi = Build.MODEL;
                                if (banzi.equals("3280")) {
                                    Service3288.getInstance().openCardDoor(cardNo,accessModel);
                                } else if (banzi.equals("SoftwinerEvb")) {
                                    ServiceA20.getInstance().openCardDoor(cardNo,accessModel);
                                } else {
                                    Service836.getInstance().openCardDoor(cardNo,accessModel);
                                }

                                CardBeanDao cardDao = GreenDaoManager.getInstance().getSession().getCardBeanDao();
                                CardBean cardBean = new CardBean(null,cardNo);
                                cardDao.insert(cardBean);
                            }else {
                                SoundPoolUtil.play(4);
                                Log.i("sss",model.getDescribe());
                            }
                        }else {
                            SoundPoolUtil.play(4);
                            Log.i("sss",model.getDescribe());
                        }

                    }
                });
    }

    /**
     * 查询卡号
     *
     * @param mac
     * @param cards
     * @param objects
     */
    private void sendFindResult(String mac, String cards, String objects) {
        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.sendfindResult(mac, cards, objects)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
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


    private void saveCodeRecord(String[] strings, String directionDoor, AccessModel model) {
        CodeRecord codeRecord = new CodeRecord(null, strings[4], strings[5], strings[1], strings[2], strings[3],
                directionDoor, model.getAccessible());
        CodeRecordDao codeRecordDao = GreenDaoManager.getInstance().getSession().getCodeRecordDao();
        codeRecordDao.insert(codeRecord);
    }

    private void saveCard(String cardNo, String directionDoor, AccessModel model) {
        CardRecord cardRecord = new CardRecord(null, cardNo, directionDoor, model.getAccessible());
        GreenDaoManager.getInstance().getSession().getCardRecordDao().insert(cardRecord);
    }


    @Override
    public void start() {

    }
}