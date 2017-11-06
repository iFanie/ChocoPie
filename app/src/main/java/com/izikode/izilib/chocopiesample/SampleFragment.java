package com.izikode.izilib.chocopiesample;

import android.support.annotation.NonNull;

import com.izikode.izilib.chocopie.fragment.YumFragment;

/**
 * Created by UserOne on 06/11/2017.
 */

public class SampleFragment extends YumFragment {

    @Override
    public int getContentView() {
        return R.layout.fragment_sample;
    }

    @Override
    public void create() {}

    @Override
    public void initialize() {}

    @Override
    public void restore(@NonNull YumFragment savedInstance) {}
}
