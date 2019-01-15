package com.yuanyang.xiaohu.door.present;


import android.content.Intent;
import com.yuanyang.xiaohu.door.activity.AccessDoorActivity2;
import com.yuanyang.xiaohu.door.activity.StartActivity;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.model.DoorModel;
import com.yuanyang.xiaohu.door.net.BillboardApi;
import com.yuanyang.xiaohu.door.retrofitdemo.UserInfoKey;
import com.yuanyang.xiaohu.door.util.SharedPreferencesUtil;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import java.util.ArrayList;
import java.util.List;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;


public class StartPresent extends XPresent<StartActivity> {

    public  void initDate(String mac,String banzi) {
        BillboardApi.getDataService().initData(mac)
                .compose(XApi.<BaseBean<DoorModel>>getApiTransformer())
                .compose(XApi.<BaseBean<DoorModel>>getScheduler())
                .compose(getV().<BaseBean<DoorModel>>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showError(error);
                        startActivity(new Intent(getV(),AccessDoorActivity2.class));
                        getV().finish();
                    }

                    @Override
                    public void onNext(BaseBean bean) {
                        if(bean.isSuccess()){
                            DoorModel doorModel= (DoorModel) bean.getMessageBody();
                            List<AccessModel> list = new ArrayList<>();
                            AccessModel model = new AccessModel();
                            model.setErCode(1);//扫码盒
                            model.setRelay(1);//设置继电器
                            model.setDoorNum("1");//设置门
                            model.setAccessible("进");
                            AccessModel model2 = new AccessModel();
                            model2.setErCode(2);
                            model2.setRelay(2);
                            model2.setDoorNum("2");
                            model2.setAccessible("出");
                            list.add(model);
                            list.add(model2);
                            if(banzi.equals("3280")||banzi.equals("SoftwinerEvb")){
                                AccessModel model3 = new AccessModel();
                                model3.setErCode(3);
                                model3.setRelay(3);
                                model3.setDoorNum("3");
                                model3.setAccessible("进");
                                AccessModel model4 = new AccessModel();
                                model4.setErCode(4);
                                model4.setRelay(4);
                                model4.setDoorNum("4");
                                model4.setAccessible("出");
                                list.add(model3);
                                list.add(model4);
                            }

                            SharedPreferencesUtil.putInt(getV(), UserInfoKey.OPEN_DOOR_NUM, list.size());
                            SharedPreferencesUtil.putString(getV(), UserInfoKey.VILLAGE_NAME,doorModel.getCommname());
                            SharedPreferencesUtil.putString(getV(), UserInfoKey.OPEN_DOOR_VILLAGE_ID, doorModel.getCommid());//小区编号
                            SharedPreferencesUtil.putString(getV(), UserInfoKey.OPEN_DOOR_DIRECTION_ID, doorModel.getName());//门
                            SharedPreferencesUtil.putInt(getV(), UserInfoKey.HEARTINTERVAL,doorModel.getHeartinterval());//心跳时间
                            SharedPreferencesUtil.putString(getV(), UserInfoKey.OPEN_DOOR_PARAMS, GsonProvider.getInstance().getGson().toJson(list));

                           int type =  doorModel.getGatetype();
                           if(type != 1){
                               SharedPreferencesUtil.putInt(getV(), UserInfoKey.OPEN_DOOR_BUILDING, doorModel.getBuildno());//单元
                               SharedPreferencesUtil.putInt(getV(), UserInfoKey.OPEN_DOOR_UNIT_ID, doorModel.getUnitno());//单元门
                           }
                        }
                        startActivity(new Intent(getV(),AccessDoorActivity2.class));
                        getV().finish();
                    }
                });
   }

}