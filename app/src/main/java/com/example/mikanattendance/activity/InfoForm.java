package com.example.mikanattendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.mikanattendance.R;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.User;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InfoForm extends Activity {
    String token;
    int id;
    String realName, phone, email, userType;
    boolean isEdit;
    // 显示组件
    EditText idE, nameE, passE, phoneE, emailE;
    RadioGroup typeE;
    // 所有的用户类型
    String[] userTypesArr = {"PM", "HR", "SALES", "OTHERS"};
    List<String> userTypes = Arrays.asList(userTypesArr);
    // 用户类型RadioButton
    Integer[] userTypeRadios = { R.id.radio_pm, R.id.radio_hr, R.id.radio_sales, R.id.radio_others };
    List<Integer> userTypeRadiosList = Arrays.asList(userTypeRadios);

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.info_form);

        // 从Bundle中获取必要的数据
        token = getIntent().getStringExtra("TOKEN");
        id = getIntent().getIntExtra("ID", 0);
        realName = getIntent().getStringExtra("REAL_NAME");
        phone = getIntent().getStringExtra("PHONE");
        email = getIntent().getStringExtra("EMAIL");
        userType = getIntent().getStringExtra("USER_TYPE");
        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);

        // 初始化组件
        idE = (EditText) findViewById(R.id.idEdit);
        nameE = (EditText) findViewById(R.id.nameEdit);
        passE = (EditText) findViewById(R.id.passEdit);
        phoneE = (EditText) findViewById(R.id.phoneEdit);
        emailE = (EditText) findViewById(R.id.mailEdit);
        typeE = (RadioGroup) findViewById(R.id.typeEdit);

        // 设置每个组件的显示
        if (isEdit) {
            String idStr = "" + id;
            idE.setText(idStr);
        } else {
            idE.setText("新增模式下该项不可用");
        }
        idE.setEnabled(false);
        nameE.setText(realName);
        phoneE.setText(phone);
        emailE.setText(email);
        if (isEdit) {
            int checkedIndex = userTypes.indexOf(userType);
            typeE.check(userTypeRadios[checkedIndex]);
        }
    }

    // 保存
    public void save(View view) {
        String url = BaseConfigurations.baseUrl + "user/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");

        Context context = view.getContext();

        User user = new User();
        // 构建实体
        try {
            if (isEdit) {
                user.setId(id);
            }
            user.setRealName(nameE.getText().toString());
            if (!passE.getText().toString().equals("")) {
                user.setUserPass(passE.getText().toString());
            }
            user.setPhone(phoneE.getText().toString());
            user.setEmail(emailE.getText().toString());
            int checkedId = typeE.getCheckedRadioButtonId();
            int checkedIndex = userTypeRadiosList.indexOf(checkedId);
            user.setUserType(userTypes.get(checkedIndex));
        } catch (Exception e) {
            // 表格内容非法直接让用户点了没反应
            e.printStackTrace();
        }

        // 构建请求体
        Gson gson = new Gson();
        String jsonStr = gson.toJson(user);
        Log.i("UserUpdate/Add#Request", jsonStr);
        params.setBodyContent(jsonStr);

        if (isEdit) {
            x.http().request(HttpMethod.PATCH, params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("UserUpdate#Succeed", result);
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
                    Log.i("UserUpdate#Error", Objects.requireNonNull(ex.getMessage()));
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i("UserUpdate#Cancelled", Objects.requireNonNull(cex.getMessage()));
                }

                @Override
                public void onFinished() {
                    Log.i("UserUpdate#Finished", "请求完成");

                }
            });
        } else {
            x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("UserAdd#Succeed", result);
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
                    Log.i("UserAdd#Error", Objects.requireNonNull(ex.getMessage()));
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i("UserAdd#Cancelled", Objects.requireNonNull(cex.getMessage()));
                }

                @Override
                public void onFinished() {
                    Log.i("UserAdd#Finished", "请求完成");
                }
            });
        }
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
