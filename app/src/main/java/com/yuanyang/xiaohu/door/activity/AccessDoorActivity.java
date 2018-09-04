package com.yuanyang.xiaohu.door.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.MusicModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.present.AccessPresent;
import com.yuanyang.xiaohu.door.service.OpenDoorService;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;
import com.yuanyang.xiaohu.door.util.GsonProvider;
import java.util.List;
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

public class AccessDoorActivity extends XActivity<AccessPresent> {

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

    @Override
    public void initData(Bundle savedInstanceState) {

        initToolbar();
        initAdapter();
        setAppendContent("门禁终端启动");
        setAppendContent("请设置参数\n参数设置说明:\n小区编号:长度为9，不足前补0，如小区编号为：123456789(正常模式，直接写入即可)，又如编号为：1234,不足9位，前补0，即输入000001234" + "" +
                "\n\n楼栋号:长度为6(可为空)，不足前补0，参考小区编号设置，如:123456 --> 123456 又如:452 --> 000452" + "");
        initViewData();
        getP().initMusic();

        startService(new Intent(this,OpenDoorService.class));
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
        BusProvider.getBus().toFlowable(MusicModel.class).subscribe(
                new Consumer<MusicModel>() {
                    @Override
                    public void accept(MusicModel musicModel) throws Exception {
                        getP().startMusic(musicModel.num);
                    }
                }
        );
        BusProvider.getBus().toFlowable(UploadModel.class).subscribe(
                new Consumer<UploadModel>() {
                    @Override
                    public void accept(UploadModel uploadModel) throws Exception {
                        getP().uploadLog(uploadModel.strings,uploadModel.model);
                    }
                }
        );
    }

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
//                if (list.size() == 8) {
//                    findViewById(R.id.add_er_code).setVisibility(View.GONE);
//                }
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
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                directionDown.setImageResource(R.drawable.ic_arrow_drop_down_black);
            }
        });
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
                if ( (!directionDoor.getText().toString().equals("请选择")) && (directionDoor.getText().toString().equals("楼栋"))) {
                    if (TextUtils.isEmpty(building.getText().toString())) {
                        ToastManager.showShort(context, "请设置楼栋号");
                        return false;
                    }
                } else if (GsonProvider.getInstance().getGson().toJson(adapter.getDataSource()).contains("请选择")) {
                    ToastManager.showShort(context, "请设置参数");
                    return false;
                }
                int door_num  = adapter.getDataSource().size();
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


    /**
     * 退出页面销毁
     */
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,OpenDoorService.class));
        getP().onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_access_door;
    }

    @Override
    public AccessPresent newP() {
        return new AccessPresent();
    }
}
