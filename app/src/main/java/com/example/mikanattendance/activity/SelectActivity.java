package com.example.mikanattendance.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mikanattendance.R;
import com.example.mikanattendance.adapter.SalaryAdapter;
import com.example.mikanattendance.adapter.SelectAdapter;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.Choice;

import java.util.ArrayList;

public class SelectActivity extends Activity implements AdapterView.OnItemClickListener {
    private String[] choices = { "角色信息管理", "意见收集及反馈", "管理员工考勤", "发布工资", "查询订单", "登记订单", "查看工资", "登记考勤", "查询考勤" };
    private int[] bmps = { R.drawable.info, R.drawable.advice, R.drawable.attendance, R.drawable.salary,
    R.drawable.order_query, R.drawable.order_add, R.drawable.salary_query, R.drawable.attendance_add, R.drawable.attendance_query };
    private int[] available;
    String userType;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_select);

        // 获取用户类型
        Intent intent = getIntent();
        userType = intent.getStringExtra("USER_TYPE");
        assert userType != null;
        switch (userType) {
            case "PM": available = new int[]{2, 3}; break;
            case "SALES": available = new int[]{4, 5, 6}; break;
            case "HR": available = new int[]{0, 1}; break;
            default: available = new int[]{6, 7, 8}; break;
        }

        // 设置说明文字
        TextView description = (TextView) findViewById(R.id.description);
        String job;
        switch (userType) {
            case "PM": job = "项目经理"; break;
            case "SALES": job = "销售人员"; break;
            case "HR": job = "人事经理"; break;
            default: job = "员工"; break;
        }
        String text = intent.getStringExtra("REAL_NAME") + "，欢迎您！您的身份是" + job;
        description.setText(text);

        // 获取ListView
        ListView selectList = (ListView) findViewById(R.id.select_list);

        // 设置ListView元素
        ArrayList<Choice> choices = new ArrayList<>();
        for (int i : available) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), this.bmps[i]);
            Choice choice = new Choice();
            choice.setName(this.choices[i]);
            choice.setThumbnail(bmp);
            choices.add(choice);
        }

        // 显示
        SelectAdapter selectAdapter = new SelectAdapter(this, R.layout.select_item, choices);
        selectList.setAdapter(selectAdapter);

        // 设置跳转
        selectList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String token = getIntent().getStringExtra("TOKEN");
        Intent intent = new Intent();
        boolean direct = false;
        if (userType.equals("HR")) {
            switch (position) {
                case 0: intent = new Intent(getApplication(), InfoActivity.class); break;
                case 1: intent = new Intent(getApplication(), SuggestActivity.class); break;
                default: break;
            }
        } else if (userType.equals("PM")) {
            switch (position) {
                case 0: intent = new Intent(getApplication(), AttendanceActivity.class); break;
                default: break;
            }
        } else if (userType.equals("SALES")) {
            switch (position) {
                case 0: intent = new Intent(getApplication(), OrderActivity.class); break;
                case 1: orderAdd(token); direct = true; break;
                case 2: intent = new Intent(getApplication(), SalaryActivity.class); break;
                default: break;
            }
        }
        if (!direct) {
            // 放入参数
            intent.putExtra("TOKEN", token);
            // 跳转
            startActivity(intent);
        }
    }

    public void orderAdd(String token) {
        // 获取JWTToken
        token = getIntent().getStringExtra("TOKEN");

        // 放入参数
        Intent intent = new Intent(getApplication(), OrderForm.class);
        intent.putExtra("TOKEN", token);
        intent.putExtra("USER_ID", BaseConfigurations.userId);
        intent.putExtra("IS_EDIT", false);
        startActivity(intent);
    }
}
