package com.example.mikanattendance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikanattendance.R;
import com.example.mikanattendance.entity.Choice;

import org.w3c.dom.Text;

import java.util.List;

public class SelectAdapter extends ArrayAdapter<Choice> {
    private int resource;
    private List<Choice> choices;
    private LayoutInflater inflater;

    public SelectAdapter(Context context, int resource, List<Choice> choices) {
        super(context, resource, choices);

        this.resource = resource;
        this.choices = choices;
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
        Choice choice = choices.get(position);
        // 设置图像
        ImageView thumbnail = (ImageView) view.findViewById(R.id.select_thumbnail);
        thumbnail.setImageBitmap(choice.getThumbnail());
        // 设置标题
        TextView name = (TextView) view.findViewById(R.id.select_item);
        name.setText(choice.getName());

        return view;
    }
}
