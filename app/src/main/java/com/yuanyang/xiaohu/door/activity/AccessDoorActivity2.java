package com.yuanyang.xiaohu.door.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanyang.xiaohu.door.BuildConfig;
import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.adapter.AccessDoorAdapter;
import com.yuanyang.xiaohu.door.dialog.DownloadAPKDialog;
import com.yuanyang.xiaohu.door.event.BusProvider;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.CardModel;
import com.yuanyang.xiaohu.door.model.CardNoModel;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.retrofitdemo.UserInfoKey;
import com.yuanyang.xiaohu.door.present.AccessContract;
import com.yuanyang.xiaohu.door.present.AccessPresent2;
import com.yuanyang.xiaohu.door.service.Service3288;
import com.yuanyang.xiaohu.door.service.Service836;
import com.yuanyang.xiaohu.door.service.ServiceA20;
import com.yuanyang.xiaohu.door.util.APKVersionCodeUtils;
import com.yuanyang.xiaohu.door.util.AppDownload;
import com.yuanyang.xiaohu.door.util.Kits;
import com.yuanyang.xiaohu.door.util.SharedPreferencesUtil;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import com.yuanyang.xiaohu.door.util.SimpleRecAdapter;
import com.yuanyang.xiaohu.door.util.SoundPoolUtil;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.droidlover.xrecyclerview.XRecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.Observable;
/**
 * 当前版本小区编号等信息从服务端设置通过MAC地址获取配置参数
 */
public class AccessDoorActivity2 extends AppCompatActivity implements AppDownload.Callback,AccessContract.View {
    private AccessContract.Presenter presenter;
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

    AccessDoorAdapter adapter;
    private Handler handler = new Handler();
    private static AccessDoorActivity2 instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        new AccessPresent2(this);
        initToolbar();
        initAdapter();
        setAppendContent("门禁终端启动\n");
        SoundPoolUtil.play(1);
        smdt = SmdtManager.create(this);
        smdt.smdtWatchDogEnable((char) 1);//开启看门狗
        mac = smdt.smdtGetEthMacAddress();
        ip = smdt.smdtGetEthIPAddress();
        heartinterval();
        getBus();
        startService();
        initViewData();
        new Timer().schedule(timerTask, 0, 5000);
        instance = this;
    }


    private void getBus() {
        BusProvider.getBus().toFlowable(EventModel.class).observeOn(AndroidSchedulers.mainThread()).subscribe(
                eventModel -> Toast.makeText(AccessDoorActivity2.this,eventModel.value,Toast.LENGTH_LONG).show()
        );
        //二维码
        BusProvider.getBus().toFlowable(UploadModel.class).subscribe(
                uploadModel ->
                        presenter.uploadLog(this,uploadModel.strings, mac, uploadModel.model)
        );
        //刷卡
        BusProvider.getBus().toFlowable(CardModel.class).subscribe(
                cardModel -> presenter.uploadCardLog(this,cardModel.card_no, mac, cardModel.model)
        );
        //查询卡号
        BusProvider.getBus().toFlowable(CardNoModel.class).subscribe(
                cardModel -> presenter.queryServer(this, mac,cardModel.value,cardModel.scanBox,cardModel.accessModel)
        );
    }

    /**
     * 根据不同的板子开启不同的Service
     */
    private void startService() {
        String banzi = Build.MODEL;
        if (banzi.equals("3280")) {
            handler.postDelayed(() -> startService(new Intent(AccessDoorActivity2.this,
                    Service3288.class)), 10000);
        } else if (banzi.equals("SoftwinerEvb")) {
            handler.postDelayed(() -> startService(new Intent(AccessDoorActivity2.this,
                    ServiceA20.class)), 10000);
        } else {
            handler.postDelayed(() -> startService(new Intent(AccessDoorActivity2.this,
                    Service836.class)), 5000);
        }
    }

    /**
     * 发送心跳数据
     */
    private void heartinterval() {
        int time =  SharedPreferencesUtil.getInt(this, UserInfoKey.HEARTINTERVAL, 10);
         Observable.interval(0, time, TimeUnit.MINUTES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if(TextUtils.isEmpty(mac) && TextUtils.isEmpty(ip)){
                        Toast.makeText(this,"Mac地址或IP地址不能为空，请检查网络！",Toast.LENGTH_LONG).show();
                        return;
                    }
                    presenter.sendState(mac, ip);
                });
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            smdt.smdtWatchDogFeed();//喂狗
        }
    };

    /**
     * 设置title
     */
    private void initToolbar() {
        String ver_name = APKVersionCodeUtils.getVerName(this);
        tv_ver.setText("版本号：" + ver_name);
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
        recyclerView.verticalLayoutManager(this);
    }

    private SimpleRecAdapter getAdapter() {
        if (adapter == null) {
            adapter = new AccessDoorAdapter(this, true);
        }
        return adapter;
    }

    public void updateVersion(String apkurl, String s_ver) {
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
        appDownload.downApk(apkurl, this);
    }


    public void initViewData() {
        if (!TextUtils.isEmpty(SharedPreferencesUtil.getString(this, UserInfoKey.VILLAGE_NAME, "")))
            village_name.setText(SharedPreferencesUtil.getString(this, UserInfoKey.VILLAGE_NAME, ""));
        else
            village_name.setText("");
        if (!TextUtils.isEmpty(SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_VILLAGE_ID, "")))
            villageId.setText(SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_VILLAGE_ID, ""));
        else
            villageId.setText("");
        if (!TextUtils.isEmpty(SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_DIRECTION_ID, "")))
            directionDoor.setText(SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_DIRECTION_ID, ""));
        else
            directionDoor.setText("请选择");

        int build_id = SharedPreferencesUtil.getInt(this, UserInfoKey.OPEN_DOOR_BUILDING, 0);
        if (build_id != 0) {
            building.setVisibility(View.VISIBLE);
            building.setText(build_id + "");
        } else {
            building.setVisibility(View.INVISIBLE);
            building.setText("");
        }
        int unit_id = SharedPreferencesUtil.getInt(this, UserInfoKey.OPEN_DOOR_UNIT_ID, 0);
        if (unit_id != 0) {
            building_unit.setVisibility(View.VISIBLE);
            building_unit.setText(unit_id + "");
        } else {
            building_unit.setVisibility(View.INVISIBLE);
            building_unit.setText("");
        }

        list = GsonProvider.stringToList(SharedPreferencesUtil.getString(this, UserInfoKey.OPEN_DOOR_PARAMS, "[]"), AccessModel.class);
        if (list.size() == 0) {
            adapter.setIsSelect(true);
            AccessModel model = new AccessModel();
            model.setErCode(1);
            model.setRelay(0);
            model.setDoorNum("无");
            model.setAccessible("请选择");
            list.add(model);
            setAppendContent("未获取到配置信息\n");
        } else {
            setAppendContent("参数信息设置成功\n");
        }
        adapter.setData(list);
    }

    /**
     * 退出页面销毁
     */
    public void onDestroy() {
        super.onDestroy();
        smdt.smdtWatchDogEnable((char) 0);
        stopService();

    }

    private void stopService() {
        String model = Build.MODEL;
        if (model.equals("3280")) {
            stopService(new Intent(this, Service3288.class));
        } else if (model.equals("SoftwinerEvb")) {
            stopService(new Intent(this, ServiceA20.class));
        } else {
            stopService(new Intent(this, Service836.class));
        }
    }


    public int getLayoutId() {
        return R.layout.activity_access_door2;
    }


    @Override
    public void callProgress(int progress) {
        if (progress >= 100) {
            runOnUiThread(() -> {
                dialog_app.dismiss();
                String sdcardDir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/download/door.apk";
                install(sdcardDir);
            });

        } else {
            runOnUiThread(() -> {
                dialog_app.getSeekBar().setProgress(progress);
                dialog_app.getNum_progress().setText(progress + "%");
            });
        }
    }

    /**
     * 开启安装过程
     *
     * @param fileName
     */
    private void install(String fileName) {
        //承接我的代码，filename指获取到了我的文件相应路径
        if (fileName != null) {
            if (fileName.endsWith(".apk")) {
                if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                    File file = new File(fileName);
                    Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                    //在AndroidManifest中的android:authorities值
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(install);
                } else {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(install);
                }
            }
        }
    }

    @Override
    public void setPresenter(AccessContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public static AccessDoorActivity2 instance() {
        return instance;
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }
}
