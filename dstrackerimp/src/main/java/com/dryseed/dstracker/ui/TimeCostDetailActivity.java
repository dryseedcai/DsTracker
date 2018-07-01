package com.dryseed.dstracker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * @author caiminming
 */
public class TimeCostDetailActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "TimeCostDetailActivity onCreate", Toast.LENGTH_SHORT).show();
    }
}
