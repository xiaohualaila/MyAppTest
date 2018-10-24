package com.yuanyang.xiaohu.door.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.adapter.AccessDoorAdapter;
import com.yuanyang.xiaohu.door.dialog.DownloadAPKDialog;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.model.VersionModel;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.present.AccessPresent;
import com.yuanyang.xiaohu.door.service.Service;
import com.yuanyang.xiaohu.door.util.AppDownload;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import com.yuanyang.xiaohu.door.util.SoundPoolUtil;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.library.base.SimpleRecAdapter;
import cn.com.library.event.BusProvider;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XActivity;
import cn.droidlover.xrecyclerview.XRecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class AccessDoorActivity extends XActivity<AccessPresent> implements AppDownload.Callback{
    /**
     * 610103001
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.open_door_param)
    XRecyclerView param;
    @BindView(R.id.village_id)
    EditText villageId;
    @BindView(R.id.direction_door)
    TextView directionDoor;
    @BindView(R.id.direction_door_down)
    ImageView directionDown;
    @BindView(R.id.building)
    EditText building;
    @BindView(R.id.tv_content)
    TextView tipContent;

    private List<AccessModel> list;

    AccessDoorAdapter adapter;

    private PopupWindow popupWindow;

    private String[] direction = {"东门", "西门", "南门", "北门", "楼栋"};

    //读卡部分
//    @BindView(R.id.ed)
//    EditText editText;
    private Thread thread;
    private boolean isAuto = true;
    private String msg;
    private StringBuffer buffer;
    private SmdtManager smdt;

    public DownloadAPKDialog dialog_app;
    @Override
    public void initData(Bundle savedInstanceState) {

        initToolbar();
        initAdapter();
        setAppendContent("门禁终端启动");
        setAppendContent("请设置参数\n参数设置说明:\n小区编号:长度为9，不足前补0，如小区编号为：123456789(正常模式，直接写入即可)，又如编号为：1234,不足9位，前补0，即输入000001234" + "" +
                "\n\n楼栋号:长度为6(可为空)，不足前补0，参考小区编号设置，如:123456 --> 123456 又如:452 --> 000452" + "");
        initViewData();
        SoundPoolUtil.play(1);

        BusProvider.getBus().toFlowable(EventModel.class).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Consumer<EventModel>() {
                    @Override
                    public void accept(EventModel eventModel) throws Exception {
                        XLog.e("EventModel===" + eventModel.value);
                        //      tv.setText(eventModel.value);
                        ToastManager.showShort(AccessDoorActivity.this, eventModel.value);
                    }
                }
        );

        BusProvider.getBus().toFlowable(UploadModel.class).subscribe(
                new Consumer<UploadModel>() {
                    @Override
                    public void accept(UploadModel uploadModel) throws Exception {
                        getP().uploadLog(uploadModel.strings, uploadModel.model);
                    }
                }
        );

        getP().sendState();

        Handler handler = new Handler();
        handler.postDelayed(() -> startService(new Intent(AccessDoorActivity.this,
                Service.class)),5000);

        String model = Build.MODEL;
        if(model.equals("3280")) {
            smdt = SmdtManager.create(this);
            smdt.smdtWatchDogEnable((char) 1);//开启看门狗
            new Timer().schedule(timerTask, 0, 5000);
        }
    }

    TimerTask timerTask = new TimerTask(){
        @Override
        public void run() {
            smdt.smdtWatchDogFeed();//喂狗
        }
    };

    ///////////////////////读卡部分

//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 0:
//                    editText.setText("");
//                    break;
//            }
//
//
//        }
//    };

//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            while (isAuto) {
//                msg = editText.getText().toString();
////                Log.i("sss","  >>>>"+msg);
//
//
//                if (msg.length()>0){
//                    if(msg.contains(";")){
//                        int index = msg.indexOf(";");
//                        msg = msg.substring(index,msg.length());
//                        if(msg.contains("?")){
//                            index = msg.indexOf("?");
//                            msg = msg.substring(0,index+1);
//
//                            Log.i("xxx","  >>>>"+msg);
//                            Message message =new Message();
//                            message.what = 0;
//                            handler.sendMessage(message);
//                            buffer.delete(0,buffer.length());
//
//                        }else {
//                            buffer.append(msg);
//                        }
//
//                    }else {
//                        if(msg.contains("?")) {
//                            int  index = msg.indexOf("?");
//                            msg = msg.substring(0,index+1);
//                            buffer.append(msg);
//
//                            String result = buffer.toString();
//
//                            Log.i("xxx","  >>>>" + result);
//                            Message message =new Message();
//                            message.what = 0;
//                            handler.sendMessage(message);
//                            buffer.delete(0,buffer.length());
//
//                        }
//                    }
//
//                }
//                try {
//                    thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }
//    };
///////////////////////////

    /**
     * 设置title
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("门禁系统");
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
        setLayoutManager(param);
        param.setAdapter(getAdapter());
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

    private void initViewData() {
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
        list = GsonProvider.stringToList(AppSharePreferenceMgr.get(context, UserInfoKey.OPEN_DOOR_PARAMS, "[]").toString(), AccessModel.class);
        if (list.size() == 0) {
            adapter.setIsSelect(true);
            AccessModel model = new AccessModel();
            model.setErCode(1);
            model.setRelay(0);
            model.setDoorNum("无");
            model.setAccessible("请选择");
            list.add(model);
            findViewById(R.id.add_er_code).setVisibility(View.VISIBLE);
            findViewById(R.id.bt_set).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.add_er_code).setVisibility(View.GONE);
            findViewById(R.id.bt_set).setVisibility(View.GONE);
//            thread = new Thread(runnable);
//            thread.start();
//            editText.requestFocus();
        }
        adapter.setData(list);
    }

    @OnClick({R.id.direction_select, R.id.add_er_code, R.id.bt_set})
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.direction_select:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    showSelectWindow(direction, findViewById(R.id.direction_select));
                    directionDown.setImageResource(R.drawable.ic_arrow_drop_up_black);
                }
                break;
            case R.id.add_er_code://添加扫码盒
                AccessModel model = new AccessModel();
                model.setErCode(list.size() + 1);
                model.setRelay(0);
                model.setDoorNum("无");
                model.setAccessible("请选择");
                list.add(model);
                adapter.setData(list);
                if (list.size() == 4) {
                    findViewById(R.id.add_er_code).setVisibility(View.GONE);
                }
                break;
            case R.id.bt_set://设置参数到终端
                if (checkIsEmpty()) {
//                    AppUtils.relaunchApp();
                    adapter.setIsSelect(false);
                    initViewData();
                }
                break;
        }
    }

    /**
     * 选择大门朝向或者楼栋
     */
    private void showSelectWindow(final String[] list, View view) {
        View window = LayoutInflater.from(context).inflate(R.layout.layout_drop_down, null);
        popupWindow = new PopupWindow(window, view.getWidth(), (view.getHeight() - 4) * list.length);
        ListView listView = window.findViewById(R.id.down_list_view);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.item_text, list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                XLog.e(list[i]);
                directionDoor.setText(list[i]);
                if (list[i].equals("楼栋"))
                    building.setVisibility(View.VISIBLE);
                else
                    building.setVisibility(View.INVISIBLE);
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);// 使其聚集
        popupWindow.setTouchable(true); // 设置允许在外点击消失/
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击其他地方消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view);
        popupWindow.setOnDismissListener(() -> directionDown.setImageResource(R.drawable.ic_arrow_drop_down_black));
    }


    /**
     * 校验输入是否为空、添加提示
     */
    private boolean checkIsEmpty() {
        if (TextUtils.isEmpty(villageId.getText().toString())) {
            ToastManager.showShort(context, "请设置小区编号");
            return false;
        } else {
            if (villageId.getText().toString().length() == 9) {
                if ((!directionDoor.getText().toString().equals("请选择")) && (directionDoor.getText().toString().equals("楼栋"))) {
                    if (TextUtils.isEmpty(building.getText().toString())) {
                        ToastManager.showShort(context, "请设置楼栋号");
                        return false;
                    }
                } else if (GsonProvider.getInstance().getGson().toJson(adapter.getDataSource()).contains("请选择")) {
                    ToastManager.showShort(context, "请设置参数");
                    return false;
                }
                int door_num = adapter.getDataSource().size();
                AppSharePreferenceMgr.put(context, UserInfoKey.OPEN_DOOR_NUM, door_num);
                AppSharePreferenceMgr.put(context, UserInfoKey.OPEN_DOOR_VILLAGE_ID, villageId.getText().toString());
                AppSharePreferenceMgr.put(context, UserInfoKey.OPEN_DOOR_DIRECTION_ID, directionDoor.getText().toString());
                AppSharePreferenceMgr.put(context, UserInfoKey.OPEN_DOOR_BUILDING, building.getText().toString());
                AppSharePreferenceMgr.put(context, UserInfoKey.OPEN_DOOR_PARAMS, GsonProvider.getInstance().getGson().toJson(adapter.getDataSource()));
                tipContent.setText("");
                setAppendContent("参数设置成功！");
            } else {
                ToastManager.showShort(context, "请输入正确的小区编号");
                return false;
            }
        }
        return true;
    }

    public void updateVersion(VersionModel model){
        dialog_app = new DownloadAPKDialog(this);
        dialog_app.show();
        dialog_app.setCancelable(false);
        dialog_app.getFile_name().setText(model.getVdetails());
        dialog_app.getFile_num().setText(model.getVnumber());
        AppDownload appDownload = new AppDownload();
        appDownload.setProgressInterface(this);
        appDownload.downApk(model.getDownload(),this);
    }


    /**
     * 退出页面销毁
     */
    public void onDestroy() {
        super.onDestroy();
        isAuto = false;
        stopService(new Intent(this, Service.class));
        String model = Build.MODEL;
        if(model.equals("3280")) {
            smdt.smdtWatchDogEnable((char)0);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_access_door;
    }

    @Override
    public AccessPresent newP() {
        return new AccessPresent();
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
                    Uri apkUri = FileProvider.getUriForFile(context, "com.yuanyang.xiaohu.door.fileprovider", file);
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
}
