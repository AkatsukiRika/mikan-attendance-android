package com.example.mikanattendance.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.example.mikanattendance.adapter.SuggestionAdapter;
import com.example.mikanattendance.base.BaseConfigurations;
import com.example.mikanattendance.entity.BasicResponse;
import com.example.mikanattendance.entity.Suggestion;
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

public class SuggestActivity extends Activity {
    String token;
    List<Suggestion> suggestions;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                suggestions = (List<Suggestion>) message.getData().getSerializable("SUGGESTIONS");
                createList(suggestions);
            }
        }
    };

    // 创建列表
    private void createList(List<Suggestion> suggestions) {
        // 获取ListView
        ListView listView = (ListView) findViewById(R.id.select_list);

        // 添加监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 使用Dialog显示意见的内容
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle(suggestions.get(position).getTitle()).setMessage(suggestions.get(position).getContent());
                dialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        // 显示
        SuggestionAdapter suggestionAdapter = new SuggestionAdapter(this, R.layout.suggest_item, suggestions);
        listView.setAdapter(suggestionAdapter);

        // 设置说明文字
        TextView description = (TextView) findViewById(R.id.description);
        description.setText("欢迎使用蜜柑考勤！您当前所处位置为：意见收集及反馈");
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_select);

        // 获取JWTToken
        token = getIntent().getStringExtra("TOKEN");

        // 获取全部的用户
        getAllSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllSuggestions();
    }

    private void getAllSuggestions() {
        String url = BaseConfigurations.baseUrl + "suggestion/";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", token);
        params.addHeader("Content-Type", "application/json");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("SuggestGetAll#Succeed", result);

                // Gson解析泛型
                Type type = new TypeToken<BasicResponse<List<Suggestion>>>(){}.getType();
                BasicResponse<List<Suggestion>> response = new Gson().fromJson(result, type);

                if (response.getCode() == 0) {
                    List<Suggestion> suggestions = (List<Suggestion>) response.getData();
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SUGGESTIONS", (Serializable) suggestions);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("SuggestGetAll#Error", Objects.requireNonNull(ex.getMessage()));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("SuggestGetAll#Cancelled", Objects.requireNonNull(cex.getMessage()));
            }

            @Override
            public void onFinished() {
                Log.i("SuggestGetAll#Finished", "请求完成");
            }
        });
    }
}
