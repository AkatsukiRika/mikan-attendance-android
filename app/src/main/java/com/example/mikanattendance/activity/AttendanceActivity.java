package com.example.mikanattendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mikanattendance.R;
import com.example.mikanattendance.adapter.AttendanceAdapter;
import com.example.mikanattendance.adapter.InfoAdapter;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.Attendance;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class AttendanceActivity extends Activity {
    String token;
    List<User> users;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                users = (List<User>) message.getData().getSerializable("USERS");
                createList(users);
            }
        }
    };

    // 创建列表
    private void createList(List<User> users) {
        // 获取ListView
        ListView listView = (ListView) findViewById(R.id.select_list);

        // 显示
        AttendanceAdapter attendanceAdapter = new AttendanceAdapter(this, R.layout.attendance_item, users);
        listView.setAdapter(attendanceAdapter);

        // 设置说明文字
        TextView description = (TextView) findViewById(R.id.description);
        description.setText("欢迎使用蜜柑考勤！您当前所处位置为：管理员工考勤");
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_attendance);

        // 获取JWTToken
        token = getIntent().getStringExtra("TOKEN");

        // 获取全部的用户
        getAllUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllUsers();
    }

    private void getAllUsers() {
        String url = BaseConfigurations.baseUrl + "user/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("UserGetAll#Succeed", result);

                // Gson解析泛型
                Type type = new TypeToken<BasicResponse<List<User>>>(){}.getType();
                BasicResponse<List<User>> response = new Gson().fromJson(result, type);

                if (response.getCode() == 0) {
                    List<User> users = (List<User>) response.getData();
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("USERS", (Serializable) users);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("UserGetAll#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("UserGetAll#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("UserGetAll#Finished", "请求完成");
            }
        });
    }
}
