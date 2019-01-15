package com.yuanyang.xiaohu.door.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.util.SimpleRecAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AccessDoorAdapter extends SimpleRecAdapter<AccessModel, AccessDoorAdapter.ViewHolder> {

    private PopupWindow popupWindow;

    private Context context;

    private boolean isClick = true;

    private String[] relays = {"第1路", "第2路", "第3路", "第4路"};

    private String[] doorNums = {"1号门", "2号门", "3号门", "4号门"};

    private String[] accessibles = {"进", "出"};

    public AccessDoorAdapter(Context context, boolean isClick) {
        super(context);
        this.context = context;
        this.isClick = isClick;
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AccessModel model = data.get(position);
        holder.erCode.setText(model.getErCode() + "号");
        holder.relay.setText(model.getRelay() == 0 ? "请选择" : "第" + model.getRelay() + "路");
        holder.doorNum.setText(model.getDoorNum().equals("无") ? model.getDoorNum() : model.getDoorNum() + "号门");
        holder.accessible.setText(model.getAccessible());
        holder.relayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClick) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        showSelectWindow(1, model, holder);
                        holder.relayDown.setImageResource(R.drawable.ic_arrow_drop_up_black);
                    }
                }
            }
        });
        holder.accessibleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClick) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        showSelectWindow(2, model, holder);
                        holder.accessibleDown.setImageResource(R.drawable.ic_arrow_drop_up_black);
                    }
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_setting;
    }

    public void setIsSelect(boolean isClick){
        this.isClick = isClick;
    }

    /**
     * 选择大门朝向以及进出方式窗口
     */
    private void showSelectWindow(final int type, final AccessModel model, final ViewHolder holder) {
        View view = type == 1 ? holder.relayView : holder.accessibleView;
        final String[] list = type == 1 ? relays : accessibles;
        View window = LayoutInflater.from(context).inflate(R.layout.layout_drop_down, null);
        popupWindow = new PopupWindow(window, view.getWidth(), (view.getHeight() + 2) * list.length);
        ListView listView = (ListView) window.findViewById(R.id.down_list_view);
        listView.setAdapter(new ArrayAdapter<String>(context, R.layout.item_text, list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (type == 1) {
                    model.setRelay(i + 1);
                    model.setDoorNum((i + 1) + "");
                    holder.relay.setText(list[i]);
                    holder.doorNum.setText(doorNums[i]);
                } else if (type == 2) {
                    model.setAccessible(list[i]);
                    holder.accessible.setText(list[i]);
                }
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
                (type == 1 ? holder.relayDown : holder.accessibleDown).setImageResource(R.drawable.ic_arrow_drop_down_black);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.er_code_view)
        View erCodeView;
        @BindView(R.id.er_code)
        TextView erCode;
        @BindView(R.id.relay_view)
        View relayView;
        @BindView(R.id.relay)
        TextView relay;
        @BindView(R.id.relay_down)
        ImageView relayDown;
        @BindView(R.id.door_view)
        View doorView;
        @BindView(R.id.door_num)
        TextView doorNum;
        @BindView(R.id.accessible_view)
        View accessibleView;
        @BindView(R.id.accessible)
        TextView accessible;
        @BindView(R.id.accessible_down)
        ImageView accessibleDown;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
