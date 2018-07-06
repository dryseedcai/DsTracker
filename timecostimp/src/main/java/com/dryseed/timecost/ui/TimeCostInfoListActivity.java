package com.dryseed.timecost.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.dryseed.timecost.entity.TimeCostInfo;
import com.dryseed.timecost.entity.TimeCostLogInfo;
import com.dryseed.timecost.utils.CanaryLogUtils;
import com.dryseed.timecostimpl.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author caiminming
 */
public class TimeCostInfoListActivity extends Activity {
    private static final String TAG = "TimeCostInfoList";

    private RecyclerView mRecyclerView;
    private TimeCostInfoListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time_cost_info_list_layout);

        initViews();
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewAdapter = new TimeCostInfoListAdapter(this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadBlocks.load(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadBlocks.forgetActivity();
    }

    static class LoadBlocks implements Runnable {

        static final List<LoadBlocks> inFlight = new ArrayList<>();
        static final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
        private TimeCostInfoListActivity activityOrNull;
        private final Handler mainHandler;

        LoadBlocks(TimeCostInfoListActivity activity) {
            this.activityOrNull = activity;
            mainHandler = new Handler(Looper.getMainLooper());
        }

        static void load(TimeCostInfoListActivity activity) {
            LoadBlocks loadBlocks = new LoadBlocks(activity);
            inFlight.add(loadBlocks);
            backgroundExecutor.execute(loadBlocks);
        }

        static void forgetActivity() {
            for (LoadBlocks loadBlocks : inFlight) {
                loadBlocks.activityOrNull = null;
            }
            inFlight.clear();
        }

        @Override
        public void run() {
            final List<TimeCostLogInfo> timeCostLogInfoList = new ArrayList<>();
            File[] files = CanaryLogUtils.getLogFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        TimeCostLogInfo timeCostLogInfo = TimeCostLogInfo.parse(file);
                        if (null != timeCostLogInfo) {
                            timeCostLogInfoList.add(timeCostLogInfo);
                        }
                    } catch (Exception e) {
                        // Probably blockFile corrupts or format changes, just delete it.
                        file.delete();
                        Log.e(TAG, "Could not read block log file, deleted :" + file, e);
                    }
                }
                Collections.sort(timeCostLogInfoList, new Comparator<TimeCostInfo>() {
                    @Override
                    public int compare(TimeCostInfo lhs, TimeCostInfo rhs) {
                        return Long.valueOf(rhs.getTimeCost()).compareTo(lhs.getTimeCost());
                    }
                });
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    inFlight.remove(LoadBlocks.this);
                    if (activityOrNull != null) {
                        activityOrNull.mRecyclerViewAdapter.setData(timeCostLogInfoList);
                        Log.d(TAG, "load block entries: " + timeCostLogInfoList.size());
                    }
                }
            });
        }
    }
}
