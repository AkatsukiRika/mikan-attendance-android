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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mikanattendance.adapter.InfoAdapter;
import com.example.mikanattendance.R;
import com.example.mikanattendance.base.BaseConfigurations;
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

public class InfoActivity extends Activity {
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
        InfoAdapter infoAdapter = new InfoAdapter(this, R.layout.info_item, users);
        listView.setAdapter(infoAdapter);

        // 设置说明文字
        TextView description = (TextView) findViewById(R.id.description);
        description.setText("欢迎使用蜜柑考勤！您当前所处位置为：角色信息管理");
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_info);

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

    public void delete(View view) {
        Button deleteBtn = view.findViewById(R.id.delete);
        int position = Integer.parseInt(deleteBtn.getTag().toString());
        // 显示删除对话框
        showDeleteDialog(position);
    }

    public void edit(View view) {
        Button editBtn = view.findViewById(R.id.edit);
        int position = Integer.parseInt(editBtn.getTag().toString());
        User user = users.get(position);

        Intent intent = new Intent(getApplication(), InfoForm.class);
        // 放入数据
        intent.putExtra("TOKEN", token);
        intent.putExtra("ID", user.getId());
        intent.putExtra("REAL_NAME", user.getRealName());
        intent.putExtra("PHONE", user.getPhone());
        intent.putExtra("EMAIL", user.getEmail());
        intent.putExtra("USER_TYPE", user.getUserType());
        intent.putExtra("IS_EDIT", true);
        // 跳转页面
        startActivity(intent);
    }

    public void add(View view) {
        Intent intent = new Intent(getApplication(), InfoForm.class);
        intent.putExtra("TOKEN", token);
        intent.putExtra("ID", 0);
        intent.putExtra("REAL_NAME", "");
        intent.putExtra("PHONE", "");
        intent.putExtra("EMAIL", "");
        intent.putExtra("USER_TYPE", "");
        intent.putExtra("IS_EDIT", false);
        startActivity(intent);
    }

    // 删除确认对话框
    private void showDeleteDialog(int position) {
        // 从position获取用户数据
        User selectedUser = users.get(position);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("删除数据").setMessage("确定删除此数据吗？");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(selectedUser);
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    // 删除数据项
    private void deleteUser(User selectedUser) {
        String url = BaseConfigurations.baseUrl + "user/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");

        // 构建请求体
        Gson gson = new Gson();
        String jsonStr = gson.toJson(selectedUser);
        Log.i("UserDelete#Request", jsonStr);
        params.setBodyContent(jsonStr);

        x.http().request(HttpMethod.DELETE, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("UserDelete#Succeed", result);

                // Gson解析泛型
                Type type = new TypeToken<BasicResponse>(){}.getType();
                BasicResponse<String> response = new BasicResponse<String>();
                response = new Gson().fromJson(result, BasicResponse.class);

                if (response.getCode() == 0) {
                    // 刷新界面
                    getAllUsers();
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
