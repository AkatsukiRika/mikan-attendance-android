package com.example.mikanattendance.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.mikanattendance.R;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                User user = (User)message.getData().getSerializable("USER");
                jump(user);
            }
        }
    };
    String token;

    // 跳转
    private void jump(User user) {
        Intent intent = new Intent(getApplication(), SelectActivity.class);
        // 放入参数
        intent.putExtra("USER_TYPE", user.getUserType());
        intent.putExtra("REAL_NAME", user.getRealName());
        intent.putExtra("TOKEN", token);
        // 跳转页面
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化组件
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    public void login(View view) {
        String username = this.username.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        // 发送HTTP请求，地址格式为http://123.123.123.123:1234/user/
        String url = BaseConfigurations.baseUrl + "user/login";
        RequestParams params = new RequestParams(url);
        params.addHeader("Content-Type", "application/json");

        User user = new User();
        user.setId(Integer.parseInt(username));
        user.setUserPass(password);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(user);
        Log.i("Login#Request", jsonStr);
        params.setBodyContent(jsonStr);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("Login#Succeed", result);
                BasicResponse<String> response = new BasicResponse<String>();
                response = new Gson().fromJson(result, BasicResponse.class);
                if (response.getCode() == 0) {
                    token = response.getData().toString();
                    Log.i("Login#JWTToken", token);
                    userQuery(Integer.parseInt(username));
                    // 将目前已登录的用户名记载到全局
                    BaseConfigurations.userId = user.getId();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("Login#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("Login#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("Login#Finished", "请求完成");
            }
        });
    }

    private void userQuery(Integer userId) {
        // 发送HTTP请求给服务器
        String url = BaseConfigurations.baseUrl + "user/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");
        params.addParameter("ID", userId);  // ID一定要传大写才行
        Log.i("UserGet#Request", params.toString());
        final User[] user = new User[1];

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("UserGet#Succeed", result);

                // Gson解析泛型
                Type type = new TypeToken<BasicResponse<List<User>>>(){}.getType();
                BasicResponse<List<User>> response = new Gson().fromJson(result, type);

                if (response.getCode() == 0) {
                    List<User> userList = (List<User>) response.getData();
                    user[0] = (User)userList.get(0);
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("USER", user[0]);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("UserGet#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("UserGet#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("UserGet#Finished", "请求完成");
            }
        });
    }
}
