package com.dryseed.timecost.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dryseed.timecost.entity.TimeCostLogInfo;
import com.dryseed.timecostimpl.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

public class TimeCostInfoListAdapter extends RecyclerView.Adapter<TimeCostInfoListAdapter.TimeCostInfoListHolder> {

    private List<TimeCostLogInfo> mData = new ArrayList<>();
    private final WeakReference<Activity> mActivityWeakReference;

    public TimeCostInfoListAdapter(Activity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    public void setData(List<TimeCostLogInfo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public TimeCostInfoListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_time_cost_info_list_item, parent, false);
        TimeCostInfoListHolder holder = new TimeCostInfoListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TimeCostInfoListHolder holder, int position) {
        TimeCostLogInfo timeCostInfo = mData.get(position);
        holder.contentTV.setText(String.format("%s blocked %d ms", timeCostInfo.getName(), timeCostInfo.getTimeCost()));

        String timeStr = DateUtils.formatDateTime(mActivityWeakReference.get(), timeCostInfo.getStartMilliTime(), FORMAT_SHOW_TIME | FORMAT_SHOW_DATE);
        holder.timeTV.setText(timeStr);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class TimeCostInfoListHolder extends RecyclerView.ViewHolder {
        public TextView contentTV;
        public TextView timeTV;

        public TimeCostInfoListHolder(View itemView) {
            super(itemView);
            contentTV = itemView.findViewById(R.id.content);
            timeTV = itemView.findViewById(R.id.time);
        }

    }
}
