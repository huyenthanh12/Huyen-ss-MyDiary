package com.example.mydiary;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.Model.DiaryDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DiaryDetailAdapter extends RecyclerView.Adapter<DiaryDetailAdapter.ViewHolder> {
    private ArrayList<DiaryDetail> list;
    private Activity activity;
    public DiaryDetailAdapter(ArrayList<DiaryDetail> list,Activity activity) {
        this.list = list;
        this.activity = activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.diary_detail_component, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DiaryDetail myListData = list.get(position);
        holder.background.getBackground().clearColorFilter();
        int color = myListData.getColor();
        holder.background.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        holder.tv_time.setText(getTimeToShow(myListData.getDateCreate()));
        holder.tv_miniContent.setText(myListData.getContent());
        holder.tv_dateCount.setText(DiaryDetail.countDate(myListData.getDateCreate()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getTimeToShow(Date date) {
        String time = "";
        Calendar itemCal = Calendar.getInstance();
        itemCal.setTime(date);
        time += itemCal.get(Calendar.HOUR) + ":" + itemCal.get(Calendar.MINUTE);
        if (itemCal.get(Calendar.AM_PM) == Calendar.AM) {
            time += "\nA.M";
        } else {
            time += "\nP.M";
        }
        return time;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_time,tv_dateCount,tv_miniContent;
        public LinearLayout background;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_time = (TextView) itemView.findViewById(R.id.item_date_hour);
            this.tv_dateCount = (TextView) itemView.findViewById(R.id.detail_datecount);
            this.tv_miniContent = (TextView) itemView.findViewById(R.id.detail_content);
            this.background =  itemView.findViewById(R.id.item);
        }
    }
}
