package com.example.mikanattendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.lang.UScript;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.example.mikanattendance.R;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.Order;
import com.example.mikanattendance.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class OrderForm extends Activity {
    String token;
    int userId;
    String orderSide, product, remark;
    int submitDate, endDate;
    boolean isEdit;
    // 显示组件
    EditText userE, sideE, productE, remarkE;
    Button submitB, endB, saveB;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.order_form);

        // 从Bundle中获取必要的数据
        token = getIntent().getStringExtra("TOKEN");
        userId = getIntent().getIntExtra("USER_ID", 0);
        orderSide = getIntent().getStringExtra("ORDER_SIDE");
        product = getIntent().getStringExtra("PRODUCT");
        remark = getIntent().getStringExtra("REMARK");
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        submitDate = getIntent().getIntExtra("SUBMIT_DATE", (int)(time / 1000));
        endDate = getIntent().getIntExtra("END_DATE", (int)(time / 1000));
        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);

        // 初始化组件
        userE = (EditText) findViewById(R.id.userEdit);
        sideE = (EditText) findViewById(R.id.orderSideEdit);
        productE = (EditText) findViewById(R.id.productEdit);
        remarkE = (EditText) findViewById(R.id.remarkEdit);
        submitB = (Button) findViewById(R.id.submitDateSelect);
        endB = (Button) findViewById(R.id.endDateSelect);
        saveB = (Button) findViewById(R.id.btnSave);

        // 设置每个组件的显示
        String userIdStr = "" + userId;
        userE.setText(userIdStr);
        sideE.setText(orderSide);
        productE.setText(product);
        remarkE.setText(remark);

        // 不允许编辑
        userE.setEnabled(false);
        if (isEdit) {
            sideE.setEnabled(false);
            productE.setEnabled(false);
            remarkE.setEnabled(false);
            submitB.setEnabled(false);
            endB.setEnabled(false);
            saveB.setVisibility(View.INVISIBLE);
        }

        // 提交日期对话框
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis((long)submitDate * 1000);
                int submitYear = calendar.get(Calendar.YEAR);
                int submitMonth = calendar.get(Calendar.MONTH);
                int submitDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        calendar.set(y, m, d);
                        submitDate = (int)(calendar.getTimeInMillis() / 1000);
                    }
                }, submitYear, submitMonth, submitDay);
                datePickerDialog.show();
            }
        });

        // 截止日期对话框
        endB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis((long)endDate * 1000);
                int endYear = calendar.get(Calendar.YEAR);
                int endMonth = calendar.get(Calendar.MONTH);
                int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        calendar.set(y, m, d);
                        endDate = (int)(calendar.getTimeInMillis() / 1000);
                    }
                }, endYear, endMonth, endDay);
                datePickerDialog.show();
            }
        });
    }

    // 保存
    public void save(View view) {
        String url = BaseConfigurations.baseUrl + "productOrder/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");

        Order order = new Order();
        // 构建实体
        try {
            order.setUserID(userId);
            order.setOrderSide(sideE.getText().toString());
            order.setProduct(productE.getText().toString());
            order.setSubmitDate(submitDate);
            order.setEndDate(endDate);
            order.setRemark(remarkE.getText().toString());
        } catch (Exception e) {
            // 表格内容非法直接让用户点了没反应
            e.printStackTrace();
        }

        // 构建请求体
        Gson gson = new Gson();
        String jsonStr = gson.toJson(order);
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
