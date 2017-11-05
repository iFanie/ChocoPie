package com.izikode.izilib.chocopie.fragment.core;

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

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import com.izikode.izilib.chocopie.activity.YumActivity;
import com.izikode.izilib.chocopie.utility.UniqueTag;

public abstract class BaseFragment extends Fragment {

    private View rootView;

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    protected boolean initialized;

    public boolean isInitialized() {
        return initialized;
    }

    public YumActivity getParent() {
        Activity activity = getActivity();
        if (activity instanceof YumActivity) {
            return (YumActivity) activity;
        }

        return null;
    }

    public View findViewById(int viewId) {
        if (rootView != null) {
            return rootView.findViewById(viewId);
        }

        return null;
    }

    public BaseFragment() {
        super();
        tag = new UniqueTag(this);
    }

    public final UniqueTag tag;

    @CallSuper
    protected void init() {}

    public boolean isStackable() {
        return true;
    }

    /* TODO check if should be moved to LifecycleFragment, to match Activity */
    public boolean isTopmost() {
        YumActivity parent = getParent();
        return parent != null && equals(parent.getTopmostFragment());
    }
}
