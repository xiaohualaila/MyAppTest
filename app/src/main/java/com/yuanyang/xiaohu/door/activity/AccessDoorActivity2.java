package com.yuanyang.xiaohu.door.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.yuanyang.xiaohu.door.BuildConfig;
import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.adapter.AccessDoorAdapter;
import com.yuanyang.xiaohu.door.dialog.DownloadAPKDialog;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.CardModel;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.present.AccessPresent2;
import com.yuanyang.xiaohu.door.service.Service3288;
import com.yuanyang.xiaohu.door.service.Service836;
import com.yuanyang.xiaohu.door.service.ServiceA20;
import com.yuanyang.xiaohu.door.util.APKVersionCodeUtils;
import com.yuanyang.xiaohu.door.util.AppDownload;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import com.yuanyang.xiaohu.door.util.SoundPoolUtil;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import cn.com.library.base.SimpleRecAdapter;
import cn.com.library.event.BusProvider;
import cn.com.library.kit.Kits;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XActivity;
import cn.com.library.net.NetError;
import cn.droidlover.xrecyclerview.XRecyclerView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 当前版本小区编号等信息从服务端设置通过MAC地址获取配置参数
 */
public class AccessDoorActivity2 extends XActivity<AccessPresent2> implements AppDownload.Callback{

    @BindView(R.id.open_door_param)
    XRecyclerView rx;
    @BindView(R.id.village_id)
    EditText villageId;
    @BindView(R.id.village_name)
    EditText village_name;
    @BindView(R.id.direction_door)
    TextView directionDoor;
    @BindView(R.id.direction_door_down)
    ImageView directionDown;
    @BindView(R.id.building)
    EditText building;
    @BindView(R.id.building_unit)
    EditText building_unit;
    @BindView(R.id.tv_content)
    TextView tipContent;
    @BindView(R.id.tv_ver)
    TextView tv_ver;
    private SmdtManager smdt;
    private String mac = "";
    private String ip = "";
    public DownloadAPKDialog dialog_app;
    private List<AccessModel> list;
    private Disposable mDisposable;
    AccessDoorAdapter adapter;
    @Override
    public void initData(Bundle savedInstanceState) {

        initToolbar();
        initAdapter();
        setAppendContent("门禁终端启动\n");

        SoundPoolUtil.play(1);
        Handler handler = new Handler();
        smdt = SmdtManager.create(this);
        smdt.smdtWatchDogEnable((char) 1);//开启看门狗
        mac= smdt.smdtGetEthMacAddress();
        ip = smdt.smdtGetEthIPAddress();

        if(mac == null || ip == null){
            setAppendContent("网络异常,请检查网络！\n");
        }
        doSomeThing();//发送心跳获取数据
        initViewData();
        new Timer().schedule(timerTask, 0, 5000);

        /**
         * 根据不同的板子开启不同的Service
         */
        String banzi = Build.MODEL;
        if(banzi.equals("3280")) {
            handler.postDelayed(() -> startService(new Intent(AccessDoorActivity2.this,
                    Service3288.class)),10000);
        }else if(banzi.equals("SoftwinerEvb")){
            handler.postDelayed(() -> startService(new Intent(AccessDoorActivity2.this,
                    ServiceA20.class)),10000);
        }else {
            handler.postDelayed(() -> startService(new Intent(AccessDoorActivity2.this,
                    Service836.class)),10000);
        }

        BusProvider.getBus().toFlowable(EventModel.class).observeOn(AndroidSchedulers.mainThread()).subscribe(
                eventModel -> {
                    XLog.e("EventModel===" + eventModel.value);
                    ToastManager.showShort(AccessDoorActivity2.this, eventModel.value);
                }
        );
        //二维码
        BusProvider.getBus().toFlowable(UploadModel.class).subscribe(
                uploadModel -> getP().uploadLog(uploadModel.strings,mac ,uploadModel.model)
        );
        //刷卡
        BusProvider.getBus().toFlowable(CardModel.class).subscribe(
                cardModel -> getP().uploadCardLog(cardModel.card_no,mac, cardModel.model)
        );

    }

    private void doSomeThing() {
       int time = (int) AppSharePreferenceMgr.get(this, UserInfoKey.HEARTINTERVAL,10);//心跳时间
        mDisposable = Flowable.interval(0,time, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    getP().sendState(mac,ip);
                });
    }

    TimerTask timerTask = new TimerTask(){
        @Override
        public void run() {
            smdt.smdtWatchDogFeed();//喂狗
        }
    };


    /**
     * 设置title
     */
    private void initToolbar() {
        String ver_name =APKVersionCodeUtils.getVerName(this);
        tv_ver.setText("版本号："+ver_name);
    }

    /**
     * 添加提示
     */
    public void setAppendContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            tipContent.append("\n" + content);
        } else {
            tipContent.append(content);
        }
    }

    private void initAdapter() {
        setLayoutManager(rx);
        rx.setAdapter(getAdapter());
    }

    private void setLayoutManager(XRecyclerView recyclerView) {
        recyclerView.verticalLayoutManager(context);
    }

    private SimpleRecAdapter getAdapter() {
        if (adapter == null) {
            adapter = new AccessDoorAdapter(context, true);
        }
        return adapter;
    }

    public void updateVersion(String apkurl, String s_ver){
        Kits.File.deleteFile(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/download/");
        File directory = new File(Environment.getExternalStorageDirectory() + "/download/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        dialog_app = new DownloadAPKDialog(this);
        dialog_app.show();
        dialog_app.setCancelable(false);
        dialog_app.getFile_name().setText("");
        dialog_app.getFile_num().setText(s_ver);
        AppDownload appDownload = new AppDownload();
        appDownload.setProgressInterface(this);
        appDownload.downApk(apkurl,this);
    }


    public void initViewData() {
        if (!TextUtils.isEmpty(AppSharePreferenceMgr.get(context, UserInfoKey.VILLAGE_NAME, "").toString()))
            village_name.setText(AppSharePreferenceMgr.get(context, UserInfoKey.VILLAGE_NAME, "").toString());
        else
            village_name.setText("");
        if (!TextUtils.isEmpty(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_VILLAGE_ID, "").toString()))
            villageId.setText(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_VILLAGE_ID, "").toString());
        else
            villageId.setText("");
        if (!TextUtils.isEmpty(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_DIRECTION_ID, "").toString()))
            directionDoor.setText(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_DIRECTION_ID, "").toString());
        else
            directionDoor.setText("请选择");
        if (!TextUtils.isEmpty(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_BUILDING, "").toString())) {
            building.setVisibility(View.VISIBLE);
            building.setText(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_BUILDING, "").toString());
        } else {
            building.setVisibility(View.INVISIBLE);
            building.setText("");
        }
        if (!TextUtils.isEmpty(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_UNIT_ID, "").toString())) {
            building_unit.setVisibility(View.VISIBLE);
            building_unit.setText(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_UNIT_ID, "").toString());
        } else {
            building_unit.setVisibility(View.INVISIBLE);
            building_unit.setText("");
        }
        list = GsonProvider.stringToList(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_PARAMS, "[]").toString(), AccessModel.class);
        if (list.size() == 0) {
            adapter.setIsSelect(true);
            AccessModel model = new AccessModel();
            model.setErCode(1);
            model.setRelay(0);
            model.setDoorNum("无");
            model.setAccessible("请选择");
            list.add(model);
            setAppendContent("未获取到配置信息\n");
        }else {
            setAppendContent("参数信息设置成功\n");
        }
        adapter.setData(list);
    }
    /**
     * 退出页面销毁
     */
    public void onDestroy() {
        super.onDestroy();
        smdt.smdtWatchDogEnable((char)0);
        String model = Build.MODEL;
        if(model.equals("3280")) {
            stopService(new Intent(this, Service3288.class));
        }else if(model.equals("SoftwinerEvb")) {
            stopService(new Intent(this, ServiceA20.class));
        }else {
             stopService(new Intent(this, Service836.class));

        }
        if (mDisposable != null){
            mDisposable.dispose();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_access_door2;
    }

    @Override
    public AccessPresent2 newP() {
        return new AccessPresent2();
    }

    @Override
    public void callProgress(int progress) {
        if (progress >= 100) {
            runOnUiThread(() -> {
                dialog_app.dismiss();
                String sdcardDir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/download/door.apk";
                install(sdcardDir);
            });

        }else {
            runOnUiThread(() -> {
                dialog_app.getSeekBar().setProgress( progress );
                dialog_app.getNum_progress().setText(progress+"%");
            });
        }
    }

    /**
     * 开启安装过程
     * @param fileName
     */
    private void install(String fileName) {
        //承接我的代码，filename指获取到了我的文件相应路径
        if (fileName != null) {
            if (fileName.endsWith(".apk")) {
                if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
                    File file= new File(fileName);
                    Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".fileprovider", file);
                    //在AndroidManifest中的android:authorities值
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    context.startActivity(install);
                } else{
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                }
            }
        }
    }
    public void showToast(String msg){
        runOnUiThread(() -> ToastManager.showShort(AccessDoorActivity2.this, msg));
    }

    /**
     * 请求返回错误
     */
    public void showError(NetError error) {
        if (error != null) {
            switch (error.getType()) {
                case NetError.ParseError:
                    ToastManager.showShort(context, "数据解析异常");
                    break;

                case NetError.AuthError:
                    ToastManager.showShort(context, "身份验证异常");
                    break;

                case NetError.BusinessError:
                    ToastManager.showShort(context, "业务异常");
                    break;

                case NetError.NoConnectError:
                    ToastManager.showShort(context, "网络无连接");
                    break;

                case NetError.NoDataError:
                    ToastManager.showShort(context, "数据为空");
                    break;

                case NetError.OtherError:
                    ToastManager.showShort(context, "网络无连接");
                    break;
            }
        }
    }

}
