package com.example.mikanattendance.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.mikanattendance.R;
import com.example.mikanattendance.entity.Suggestion;
import com.example.mikanattendance.entity.User;

import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<User> {
    private int resource;
    private List<User> users;
    private LayoutInflater inflater;

    public AttendanceAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);

        this.resource = resource;
        this.users = users;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(resource, null);
        }

        // 获取ListView中元素
        User user = users.get(position);
        // 设置图像
        Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(this.getContext(), R.drawable.hamster)).getBitmap();
        ImageView thumbnail = (ImageView) view.findViewById(R.id.select_thumbnail);
        thumbnail.setImageBitmap(bitmap);
        // 设置标题
        TextView name = (TextView) view.findViewById(R.id.select_item);
        name.setText(user.getRealName());

        return view;
    }
}
