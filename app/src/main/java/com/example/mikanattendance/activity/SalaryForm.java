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

import com.example.mikanattendance.R;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.Order;
import com.example.mikanattendance.entity.Salary;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class SalaryForm extends Activity {
    String token;
    int userId;
    String remark;
    int payDay, salary;
    boolean isEdit;
    // 显示组件
    EditText userE, payDayE, salaryE, remarkE;
    Button saveB;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.salary_form);

        // 从Bundle中获取必要的数据
        token = getIntent().getStringExtra("TOKEN");
        userId = getIntent().getIntExtra("USER_ID", 0);
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        payDay = getIntent().getIntExtra("PAY_DAY", (int)(time / 1000));
        salary = getIntent().getIntExtra("SALARY", (int)0);
        remark = getIntent().getStringExtra("REMARK");
        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);

        // 初始化组件
        userE = (EditText) findViewById(R.id.userEdit);
        payDayE = (EditText) findViewById(R.id.payDayEdit);
        salaryE = (EditText) findViewById(R.id.salaryEdit);
        remarkE = (EditText) findViewById(R.id.remarkEdit);
        saveB = (Button) findViewById(R.id.btnSave);

        // 设置每个组件的显示
        String userIdStr = "" + userId;
        userE.setText(userIdStr);
        // 时间处理
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String payDayStr = simpleDateFormat.format(((long)payDay * 1000));
        payDayE.setText(payDayStr);
        String salaryStr = "" + (double)salary / 100;
        salaryE.setText(salaryStr);
        remarkE.setText(remark);

        // 不允许编辑
        payDayE.setEnabled(false);
        if (isEdit) {
            userE.setEnabled(false);
            salaryE.setEnabled(false);
            remarkE.setEnabled(false);
            saveB.setVisibility(View.INVISIBLE);
        }
    }

    // 保存
    public void save(View view) {
        String url = BaseConfigurations.baseUrl + "salary/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");

        Salary salary = new Salary();
        // 构建实体
        try {
            salary.setUserID(Integer.parseInt(userE.getText().toString()));
            salary.setPayDay(payDay);
            int salaryInt = (int) Double.parseDouble(salaryE.getText().toString()) * 100;
            salary.setSalary(salaryInt);
            salary.setRemark(remarkE.getText().toString());
        } catch (Exception e) {
            // 表格内容非法直接让用户点了没反应
            e.printStackTrace();
        }

        // 构建请求体
        Gson gson = new Gson();
        String jsonStr = gson.toJson(salary);
        Log.i("SalaryAdd#Request", jsonStr);
        params.setBodyContent(jsonStr);

        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("SalaryAdd#Succeed", result);
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
                Log.i("SalaryAdd#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("SalaryAdd#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("SalaryAdd#Finished", "请求完成");
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
