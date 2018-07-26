package com.dryseed.timecost.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dryseed.timecost.TimeCostCanary;
import com.dryseed.timecost.TimeCostConfig;
import com.dryseed.timecost.constants.TimeCostConstant;
import com.dryseed.timecost.entity.TimeCostInfo;
import com.dryseed.timecost.entity.TimeCostLogInfo;
import com.dryseed.timecost.utils.CanaryLogUtils;
import com.dryseed.timecost.utils.DebugLog;
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
    private Button mDelBtn;
    private Button mSortBtn;
    private int mSortTypeIndex = TimeCostCanary.get().getConfig().getSortType();

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

        mDelBtn = findViewById(R.id.del_btn);
        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CanaryLogUtils.deleteAll();
                        if (null != mRecyclerViewAdapter) {
                            List<TimeCostLogInfo> list = Collections.emptyList();
                            mRecyclerViewAdapter.setData(list);
                        }
                    }
                };
                new AlertDialog.Builder(TimeCostInfoListActivity.this)
                        .setTitle(getString(R.string.time_cost_canary_delete))
                        .setMessage(getString(R.string.time_cost_canary_delete_all_dialog_content))
                        .setPositiveButton(getString(R.string.time_cost_canary_yes), okListener)
                        .setNegativeButton(getString(R.string.time_cost_canary_no), null)
                        .show();
            }
        });

        mSortBtn = findViewById(R.id.sort_btn);
        mSortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sortType = (++mSortTypeIndex) % 3;
                if (sortType == TimeCostConstant.CONFIG_SORT_TYPE_START_TIME) {
                    mSortBtn.setText("SORT_START_TIME");
                } else if (sortType == TimeCostConstant.CONFIG_SORT_TYPE_TIME_COST) {
                    mSortBtn.setText("SORT_TIME_COST");
                } else if (sortType == TimeCostConstant.CONFIG_SORT_TYPE_THREAD_TIME_COST) {
                    mSortBtn.setText("SORT_THREAD_TIME_COST");
                }
                TimeCostCanary.get().getConfig().setSortType(sortType);
                LoadBlocks.load(TimeCostInfoListActivity.this);
            }
        });

        ((TextView) findViewById(R.id.title)).setText(getString(R.string.time_cost_canary_block_list_title, getPackageName()));
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
                        DebugLog.e(TAG, "Could not read block log file, deleted :" + file);
                    }
                }
                Collections.sort(timeCostLogInfoList, new Comparator<TimeCostInfo>() {
                    @Override
                    public int compare(TimeCostInfo lhs, TimeCostInfo rhs) {
                        int sortType = TimeCostCanary.get().getConfig().getSortType();
                        if (sortType == TimeCostConstant.CONFIG_SORT_TYPE_START_TIME) {
                            return Long.valueOf(rhs.getStartMilliTime()).compareTo(lhs.getStartMilliTime());
                        } else if (sortType == TimeCostConstant.CONFIG_SORT_TYPE_TIME_COST) {
                            return Long.valueOf(rhs.getTimeCost()).compareTo(lhs.getTimeCost());
                        } else if (sortType == TimeCostConstant.CONFIG_SORT_TYPE_THREAD_TIME_COST) {
                            return Long.valueOf(rhs.getThreadTimeCost()).compareTo(lhs.getThreadTimeCost());
                        } else {
                            return Long.valueOf(rhs.getStartMilliTime()).compareTo(lhs.getStartMilliTime());
                        }
                    }
                });
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    inFlight.remove(LoadBlocks.this);
                    if (activityOrNull != null) {
                        activityOrNull.mRecyclerViewAdapter.setData(timeCostLogInfoList);
                        DebugLog.d(TAG, "load block entries: " + timeCostLogInfoList.size());
                    }
                }
            });
        }
    }
}
