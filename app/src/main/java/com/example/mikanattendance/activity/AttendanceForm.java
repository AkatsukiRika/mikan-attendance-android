package com.example.mikanattendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.mikanattendance.R;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.Attendance;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class AttendanceForm extends Activity {
    String token;
    int userId, attendanceTime;
    String attendanceStatus, remark;
    short attendanceType;
    // 显示组件
    EditText userE, timeE, remarkE;
    RadioGroup statusR, typeR;
    Button saveB;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.order_form);

        // 从Bundle中获取必要的数据
        token = getIntent().getStringExtra("TOKEN");
        userId = getIntent().getIntExtra("USER_ID", 0);
        remark = getIntent().getStringExtra("REMARK");
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        attendanceTime = getIntent().getIntExtra("ATD_TIME", (int)(time / 1000));
        attendanceStatus = getIntent().getStringExtra("ATD_STATUS");
        attendanceType = getIntent().getShortExtra("ATD_TYPE", (short) 0);

        // 初始化组件
        userE = (EditText) findViewById(R.id.userEdit);
        timeE = (EditText) findViewById(R.id.atdTimeEdit);
        remarkE = (EditText) findViewById(R.id.remarkEdit);
        statusR = (RadioGroup) findViewById(R.id.atdStatus);
        typeR = (RadioGroup) findViewById(R.id.atdType);
        saveB = (Button) findViewById(R.id.btnSave);

        // 设置每个组件的显示
        String userIdStr = "" + userId;
        userE.setText(userIdStr);
        // 时间处理
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = simpleDateFormat.format(((long)attendanceTime * 1000));
        timeE.setText(timeStr);
        remarkE.setText(remark);

        // 不允许编辑
        userE.setEnabled(false);
        timeE.setEnabled(false);
    }

    // 保存
    public void save(View view) {
        String url = BaseConfigurations.baseUrl + "productOrder/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");

        Attendance attendance = new Attendance();
        // 构建实体
        try {
            attendance.setUserID(Integer.parseInt(userE.getText().toString()));
            // TODO: 设定状态
            attendance.setRemark(remarkE.getText().toString());
        } catch (Exception e) {
            // 表格内容非法直接让用户点了没反应
            e.printStackTrace();
        }

        // 构建请求体
        Gson gson = new Gson();
        String jsonStr = gson.toJson(attendance);
        Log.i("OrderAdd#Request", jsonStr);
        params.setBodyContent(jsonStr);

        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("OrderAdd#Succeed", result);
                BasicResponse<String> response = new Gson().fromJson(result, BasicResponse.class);

                // 错误提示
                if (response.getCode() != 0) {
                    error(view, response.getMessage());
                } else {
                    finish();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("OrderAdd#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("OrderAdd#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("OrderAdd#Finished", "请求完成");

            }
        });
    }

    // 捕获请求异常
    private void error(View view, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
        dialog.setTitle("请求错误").setMessage(message);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
