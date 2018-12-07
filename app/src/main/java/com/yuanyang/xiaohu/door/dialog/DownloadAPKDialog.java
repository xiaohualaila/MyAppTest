package com.yuanyang.xiaohu.door.dialog;

import android.content.Context;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.yuanyang.xiaohu.door.R;
import java.util.List;

/**
 * Created by linzp on 2017/11/20.
 */

public class DownloadAPKDialog extends BaseDialog {
    ProgressBar seekBar;
    TextView num_progress;
    TextView file_name;
    TextView file_num;
    public DownloadAPKDialog(Context context) {
        super(context, R.style.dim_dialog);
        seekBar=  findViewById(R.id.seek);
        num_progress = findViewById(R.id.loading_pro);
        file_name =  findViewById(R.id.loading_file_name);
        file_num =  findViewById(R.id.loading_num);
        seekBar.setEnabled(false);
    }

    public TextView getNum_progress() {
        return num_progress;
    }

    public void setNum_progress(TextView num_progress) {
        this.num_progress = num_progress;
    }

    public TextView getFile_name() {
        return file_name;
    }

    public void setFile_name(TextView file_name) {
        this.file_name = file_name;
    }

    public TextView getFile_num() {
        return file_num;
    }

    public void setFile_num(TextView file_num) {
        this.file_num = file_num;
    }

    public ProgressBar getSeekBar() {
        return seekBar;
    }

    public void setSeekBar(ProgressBar seekBar) {
        this.seekBar = seekBar;
    }

    public void setNumProBar(TextView num_progress) {
        this.num_progress = num_progress;
    }


    public TextView getNumProBar() {
        return num_progress;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.apk_down_dialog;
    }

    @Override
    protected void findViews() {

        addListeners();
    }

    private void addListeners() {


    }

    @Override
    protected void setWindowParam() {
        setWindowParams(-1, -2, Gravity.CENTER, 0);
    }

    public void setList(List<String> listType) {

    }

    public interface GetString{
        void getString(String s);
    }
}
