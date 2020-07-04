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

import com.example.mikanattendance.R;
import com.example.mikanattendance.adapter.InfoAdapter;
import com.example.mikanattendance.adapter.OrderAdapter;
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
import java.util.List;
import java.util.Objects;

public class OrderActivity extends Activity {
    String token;
    List<Order> orders;
    User user;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                orders = (List<Order>) message.getData().getSerializable("ORDERS");
                createList(orders);
            }
        }
    };

    // 创建列表
    private void createList(List<Order> orders) {
        // 获取ListView
        ListView listView = (ListView) findViewById(R.id.select_list);

        // 添加监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 放入参数
                Order order = orders.get(position);
                Intent intent = new Intent(getApplication(), OrderForm.class);
                intent.putExtra("TOKEN", token);
                intent.putExtra("USER_ID", order.getUserID());
                // 获取单个User
                intent.putExtra("ORDER_SIDE", order.getOrderSide());
                intent.putExtra("PRODUCT", order.getProduct());
                intent.putExtra("REMARK", order.getRemark());
                intent.putExtra("SUBMIT_DATE", order.getSubmitDate());
                intent.putExtra("END_DATE", order.getEndDate());
                intent.putExtra("IS_EDIT", true);
                startActivity(intent);
            }
        });

        // 显示
        OrderAdapter orderAdapter = new OrderAdapter(this, R.layout.order_item, orders);
        listView.setAdapter(orderAdapter);

        // 设置说明文字
        TextView description = (TextView) findViewById(R.id.description);
        description.setText("欢迎使用蜜柑考勤！您当前所处位置为：查询订单");
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_order);

        // 获取JWTToken
        token = getIntent().getStringExtra("TOKEN");

        // 获取全部订单
        getAllOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllOrders();
    }

    private void getAllOrders() {
        String url = BaseConfigurations.baseUrl + "productOrder/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("OrderGetAll#Succeed", result);

                // Gson解析泛型
                Type type = new TypeToken<BasicResponse<List<Order>>>(){}.getType();
                BasicResponse<List<Order>> response = new Gson().fromJson(result, type);

                if (response.getCode() == 0) {
                    List<Order> orders = (List<Order>) response.getData();
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ORDERS", (Serializable) orders);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("OrderGetAll#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("OrderGetAll#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("OrderGetAll#Finished", "请求完成");
            }
        });
    }
}
