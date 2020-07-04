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
import com.example.mikanattendance.entity.Attendance;

import java.text.SimpleDateFormat;
import java.util.List;

public class AttendanceListAdapter extends ArrayAdapter<Attendance> {
    private int resource;
    private List<Attendance> attendances;
    private LayoutInflater inflater;

    public AttendanceListAdapter(Context context, int resource, List<Attendance> attendances) {
        super(context, resource, attendances);

        this.resource = resource;
        this.attendances = attendances;
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
        Attendance attendance = attendances.get(position);
        // 设置图像
        Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(this.getContext(), R.drawable.attendance_query)).getBitmap();
        ImageView thumbnail = (ImageView) view.findViewById(R.id.select_thumbnail);
        thumbnail.setImageBitmap(bitmap);
        // 设置标题
        TextView name = (TextView) view.findViewById(R.id.select_item);
        // 时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(((long)attendance.getAttendanceTime() * 1000));
        // 状态
        String attendanceType = attendance.getAttendanceType() == 0 ? "上班" : "下班";
        String title = time + " " + attendanceType;
        name.setText(title);
        return view;
    }
}
