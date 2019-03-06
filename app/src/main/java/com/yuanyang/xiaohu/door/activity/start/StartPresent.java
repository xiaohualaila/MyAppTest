package com.yuanyang.xiaohu.door.activity.start;


import android.content.Context;
import com.yuanyang.xiaohu.door.base.BasePresenter;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.DoorModel;
import com.yuanyang.xiaohu.door.retrofitdemo.Request_Interface;
import com.yuanyang.xiaohu.door.retrofitdemo.RetrofitManager;
import com.yuanyang.xiaohu.door.retrofitdemo.UserInfoKey;
import com.yuanyang.xiaohu.door.util.SharedPreferencesUtil;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class StartPresent extends BasePresenter implements StartContract.Presenter{
    private StartContract.View view;

    public StartPresent(StartContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public  void initDate(String mac, Context context) {
        Request_Interface request = RetrofitManager.getInstance().create(Request_Interface.class);
        request.initData(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<BaseBean>() {
                    @Override
                    public void onComplete() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                        view.toAccessDoorActivity();

                    }
                    @Override
                    public void onNext(BaseBean bean) {
                        if(bean.isSuccess()){
                            DoorModel doorModel= (DoorModel) bean.getMessageBody();
                            List<AccessModel> list = new ArrayList<>();

                            AccessModel model = new AccessModel();
                            model.setErCode(1);//扫码盒
                            model.setRelay(1);
                            model.setDoorNum("1");//设置门
                            model.setAccessible("进");
                            list.add(model);

                            AccessModel model2 = new AccessModel();
                            model2.setErCode(2);
                            model2.setRelay(2);
                            model2.setDoorNum("2");
                            model2.setAccessible("出");
                            list.add(model2);


                            AccessModel model3 = new AccessModel();
                            model3.setErCode(3);
                            model3.setRelay(3);
                            model3.setDoorNum("3");
                            model3.setAccessible("进");
                            list.add(model3);

                            AccessModel model4 = new AccessModel();
                            model4.setErCode(4);
                            model4.setRelay(4);
                            model4.setDoorNum("4");
                            model4.setAccessible("出");
                            list.add(model4);


                            SharedPreferencesUtil.putInt(context, UserInfoKey.OPEN_DOOR_NUM, list.size());
                            SharedPreferencesUtil.putString(context, UserInfoKey.VILLAGE_NAME,doorModel.getCommname());
                            SharedPreferencesUtil.putString(context, UserInfoKey.OPEN_DOOR_VILLAGE_ID, doorModel.getCommid());//小区编号
                            SharedPreferencesUtil.putString(context, UserInfoKey.OPEN_DOOR_DIRECTION_ID, doorModel.getName());//门
                            SharedPreferencesUtil.putInt(context, UserInfoKey.HEARTINTERVAL,doorModel.getHeartinterval());//心跳时间
                            SharedPreferencesUtil.putString(context, UserInfoKey.OPEN_DOOR_PARAMS, GsonProvider.getInstance().getGson().toJson(list));

                            int type =  doorModel.getGatetype();
                            if(type != 1){
                                SharedPreferencesUtil.putInt(context, UserInfoKey.OPEN_DOOR_BUILDING, doorModel.getBuildno());//单元
                                SharedPreferencesUtil.putInt(context, UserInfoKey.OPEN_DOOR_UNIT_ID, doorModel.getUnitno());//单元门
                            }
                        }
                        view.toAccessDoorActivity();
                    }
                });
   }

    @Override
    public void start() {

    }
}