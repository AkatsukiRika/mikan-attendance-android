package com.example.mikanattendance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mikanattendance.R;
import com.example.mikanattendance.adapter.OrderAdapter;
import com.example.mikanattendance.adapter.SalaryAdapter;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.Salary;
import com.example.mikanattendance.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class SalaryActivity extends Activity {
    String token;
    List<Salary> salaries;
    User user;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                salaries = (List<Salary>) message.getData().getSerializable("SALARIES");
                createList(salaries);
            }
        }
    };

    // 创建列表
    private void createList(List<Salary> orders) {
        // 获取ListView
        ListView listView = (ListView) findViewById(R.id.select_list);

        // 添加监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 放入参数
                Salary salary = salaries.get(position);
                Intent intent = new Intent(getApplication(), SalaryForm.class);
                intent.putExtra("TOKEN", token);
                intent.putExtra("USER_ID", salary.getUserID());
                intent.putExtra("PAY_DAY", salary.getPayDay());
                intent.putExtra("SALARY", salary.getSalary());
                intent.putExtra("REMARK", salary.getRemark());
                intent.putExtra("IS_EDIT", true);
                startActivity(intent);
            }
        });

        // 显示
        SalaryAdapter salaryAdapter = new SalaryAdapter(this, R.layout.salary_item, salaries);
        listView.setAdapter(salaryAdapter);

        // 设置说明文字
        TextView description = (TextView) findViewById(R.id.description);
        description.setText("欢迎使用蜜柑考勤！您当前所处位置为：查看工资");
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_salary);

        // 获取JWTToken
        token = getIntent().getStringExtra("TOKEN");

        // 获取全部订单
        getUserSalaries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserSalaries();
    }

    private void getUserSalaries() {
        String url = BaseConfigurations.baseUrl + "salary/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");
        params.addParameter("userID", BaseConfigurations.userId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("SalaryGetAll#Succeed", result);

                // Gson解析泛型
                Type type = new TypeToken<BasicResponse<List<Salary>>>(){}.getType();
                BasicResponse<List<Salary>> response = new Gson().fromJson(result, type);

                if (response.getCode() == 0) {
                    List<Salary> salaries = (List<Salary>) response.getData();
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SALARIES", (Serializable) salaries);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("SalaryGetAll#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("SalaryGetAll#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("SalaryGetAll#Finished", "请求完成");
            }
        });
    }
}
