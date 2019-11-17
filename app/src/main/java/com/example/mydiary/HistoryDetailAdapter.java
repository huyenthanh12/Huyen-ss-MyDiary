package com.example.mydiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mydiary.Model.HistoryDetail;
import com.example.mydiary.R;

import java.util.ArrayList;

public class HistoryDetailAdapter extends ArrayAdapter<HistoryDetail> {
    private ArrayList<HistoryDetail> list;
    private Context context;
    private int resource;
    public HistoryDetailAdapter(@NonNull Context context, int resource, @NonNull ArrayList<HistoryDetail> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_detail_component,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.date_modified);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.origin_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HistoryDetail detail = list.get(position);
        viewHolder.tvDate.setText(detail.getDateEditted().toString());
        viewHolder.tvContent.setText(detail.getContent());
        return convertView;
    }

    public class ViewHolder {
        TextView tvDate,tvContent;
    }
}
