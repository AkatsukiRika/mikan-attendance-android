package com.example.mikanattendance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.mikanattendance.R;
import com.example.mikanattendance.adapter.AttendanceListAdapter;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.Attendance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class AttendanceList extends Activity {
    String token;
    List<Attendance> attendances;
    int userID;
    String realName;
    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                attendances = (List<Attendance>) message.getData().getSerializable("ATTENDANCES");
                createList(attendances);
            }
        }
    };

    // 创建列表
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void createList(List<Attendance> users) {
        // 获取ListView
        ListView listView = (ListView) findViewById(R.id.select_list);

        // 添加监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 放入参数
                String token = getIntent().getStringExtra("TOKEN");
                Intent intent = new Intent(getApplication(), AttendanceForm.class);

                // 放入参数
                intent.putExtra("TOKEN", token);
                intent.putExtra("USER_ID", BaseConfigurations.userId);
                intent.putExtra("ATD_TIME", attendances.get(position).getAttendanceTime());
                intent.putExtra("ATD_STATUS", attendances.get(position).getAttendanceStatus());
                intent.putExtra("ATD_TYPE", attendances.get(position).getAttendanceType());
                // 跳转
                startActivity(intent);
            }
        });

        // 显示
        AttendanceListAdapter attendanceListAdapter = new AttendanceListAdapter(this, R.layout.attendance_list_item, users);
        listView.setAdapter(attendanceListAdapter);

        // 设置说明文字
        TextView description = (TextView) findViewById(R.id.description);
        description.setText("选择记录后可进行编辑");
        description.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.attendance_list);

        // 获取JWTToken及必要数据
        token = getIntent().getStringExtra("TOKEN");
        userID = getIntent().getIntExtra("USER_ID", 0);
        realName = getIntent().getStringExtra("REAL_NAME");

        // 获取全部的用户
        getAllAttendances();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllAttendances();
    }

    private void getAllAttendances() {
        String url = BaseConfigurations.baseUrl + "attendance/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");
        params.addParameter("userID", userID);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("AtdGetAll#Succeed", result);

                // Gson解析泛型
                Type type = new TypeToken<BasicResponse<List<Attendance>>>(){}.getType();
                BasicResponse<List<Attendance>> response = new Gson().fromJson(result, type);

                if (response.getCode() == 0) {
                    List<Attendance> attendances = (List<Attendance>) response.getData();
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ATTENDANCES", (Serializable) attendances);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("AtdGetAll#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("AtdGetAll#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("AtdGetAll#Finished", "请求完成");
            }
        });
    }
}
