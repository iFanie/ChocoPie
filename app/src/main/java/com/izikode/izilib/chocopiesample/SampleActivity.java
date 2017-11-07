package com.izikode.izilib.chocopiesample;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.izikode.izilib.chocopie.activity.YumActivity;
import com.izikode.izilib.chocopie.utility.Retainable;

/**
 * Created by UserOne on 06/11/2017.
 */

public class SampleActivity extends YumActivity {

    @Override
    public int getContentView() {
        return R.layout.activity_sample;
    }

    private AsyncTask<Void, Void, Void> importantTask;
    private Retainable<AsyncTask<Void, Void, Void>> taskRetainable;

    @Override
    public void create() {
        taskRetainable = new Retainable<AsyncTask<Void, Void, Void>>() {

            @Override
            public void preSubmerged(@NonNull AsyncTask<Void, Void, Void> value) {
                // Do something, like hiding UI
            }

            @Override
            public void postSurfaced(@NonNull AsyncTask<Void, Void, Void> value) {
                // Do something, like regaining the object reference
                // and showing the UI again
                importantTask = value;
            }
        };
    }

    @Override
    public void initialize() {
        importantTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }
        };

        taskRetainable.setValue(importantTask);
    }

    @Override
    public void restore(@NonNull YumActivity savedInstance) {}

    @Nullable
    @Override
    public Integer getFragmentContainer() {
        return null;
    }
}
