package com.izikode.izilib.chocopie.activity.core;

/*
 * Copyright 2017 Fanie Veizis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.izikode.izilib.chocopie.utility.UniqueTag;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    public BaseActivity() {
        super();
        tag = new UniqueTag(this);
    }

    public final UniqueTag tag;
    public FragmentManager fragmentManager;

    @CallSuper
    protected void init() {
        fragmentManager = getSupportFragmentManager();
    }

    @Deprecated
    @Override
    public android.app.FragmentManager getFragmentManager() {
        return super.getFragmentManager();
    }

    protected List<Fragment> getActiveFragments() {
        return fragmentManager.getFragments();
    }
}
