package com.zfsbs.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.model.ShiftRoom;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.utils.AlertUtils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.myapplication.MyApplication;

import static com.zfsbs.common.CommonFunc.startAction;

public class ShiftRoomActivity extends BaseActivity {

    private LinearLayout btnShitRoom;
    private LinearLayout btnShitRoomDay;
    private LinearLayout btnShitRoomRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_shift_room);
//        AppManager.getAppManager().addActivity(this);
        initTitle("班结");

        btnShitRoom = (LinearLayout) findViewById(R.id.id_ll_shiftroom);
        btnShitRoomDay = (LinearLayout) findViewById(R.id.id_ll_shiftroom_day);
        btnShitRoomRecord = (LinearLayout) findViewById(R.id.id_ll_shiftroom_record);

        btnShitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShiftRoom();
            }
        });

        btnShitRoomDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = ShiftRoomActivity.this.getLayoutInflater().inflate(R.layout.activity_password, null);
                AlertUtils.alertSetPassword(mContext, "请输入主管理员密码。", "确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 获取EditView中的输入内容
                        EditText edit_text = (EditText) view.findViewById(R.id.et_password);

                        String pass = (String) SPUtils.get(mContext, Constants.MASTER_PASS, Constants.DEFAULT_MASTER_PASS);
                        if (StringUtils.isEmpty(edit_text.getText().toString())) {
                            ToastUtils.CustomShow(mContext, "请输入主管理密码");
                            return;
                        }
                        if (!StringUtils.isEquals(pass, edit_text.getText().toString())) {
                            ToastUtils.CustomShow(mContext, "主管理密码错误");
                            return;
                        }
                        dialog.dismiss();
                        getShiftRoomDay();
                    }

                }, view);


            }
        });

        btnShitRoomRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAction(ShiftRoomActivity.this, ShiftRoomRecordActivity.class, false);
            }
        });

    }




    /**
     * 班接
     */
    private void getShiftRoom() {

        Long sid = MyApplication.getInstance().getLoginData().getSid();
        final long start_time = StringUtils.getdate2TimeStamp((String) SPUtils.get(this, Constants.SHIFT_ROOM_TIME, Constants.DEFAULT_SHIFT_ROOM_TIME));
        LogUtils.e("start_time=", (String) SPUtils.get(this, Constants.SHIFT_ROOM_TIME, Constants.DEFAULT_SHIFT_ROOM_TIME));
        final long end_time = StringUtils.getdate2TimeStamp(StringUtils.getCurTime());
        LogUtils.e("end_time", StringUtils.getCurTime());
        this.sbsAction.shift_room(this, sid, start_time, end_time, new ActionCallbackListener<ShiftRoom>() {
            @Override
            public void onSuccess(final ShiftRoom data) {
                LogUtils.e("onSucess"+data.toString());


                Bundle bundle = new Bundle();
                bundle.putSerializable("ShiftRoom", data);
                bundle.putLong("start_time", start_time);
                bundle.putLong("end_time", end_time);
                bundle.putInt("type", Constants.PRINTER_SHIFT_ROOM);
                startAction(ShiftRoomActivity.this, ShiftRoomShowActivity.class, bundle, false);

            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ShiftRoomActivity.this, errorEvent+message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity1.class, false);
                }

            }
        });
    }

    /**
     * 班接
     */
    private void getShiftRoomDay() {

        Long sid = MyApplication.getInstance().getLoginData().getSid();
        final long start_time = StringUtils.getdate2TimeStamp(StringUtils.formatTime(StringUtils.getCurDate()+"000000"));
        LogUtils.e("start_time=", StringUtils.formatTime(StringUtils.getCurDate()+"000000"));
        final long end_time = StringUtils.getdate2TimeStamp(StringUtils.getCurTime());
        LogUtils.e("end_time", StringUtils.getCurTime());
        this.sbsAction.shift_room(this, sid, start_time, end_time, new ActionCallbackListener<ShiftRoom>() {
            @Override
            public void onSuccess(final ShiftRoom data) {
                LogUtils.e("onSuccess"+data.toString());


                Bundle bundle = new Bundle();
                bundle.putSerializable("ShiftRoom", data);
                bundle.putLong("start_time", start_time);
                bundle.putLong("end_time", end_time);
                bundle.putInt("type", Constants.PRINTER_SHIFT_ROOM_DAY);
                startAction(ShiftRoomActivity.this, ShiftRoomShowActivity.class, bundle, false);

            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ShiftRoomActivity.this, errorEvent+message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }
}
