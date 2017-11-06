package com.izikode.izilib.chocopiesample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.izikode.izilib.chocopie.activity.YumActivity;
import com.izikode.izilib.chocopie.annotation.Restore;

/**
 * Created by UserOne on 06/11/2017.
 */

public class SampleActivity extends YumActivity {

    @Override
    public int getContentView() {
        return R.layout.activity_sample;
    }

    @Override
    public void create() {}

    @Override
    public void initialize() {}

    @Override
    public void restore(@NonNull YumActivity savedInstance) {}

    @Nullable
    @Override
    public Integer getFragmentContainer() {
        return null;
    }
}
