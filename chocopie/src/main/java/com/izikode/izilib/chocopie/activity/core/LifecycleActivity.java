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

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.izikode.izilib.chocopie.delegate.LifecycleDelegates;

public abstract class LifecycleActivity<A> extends ContainerActivity<A> implements LifecycleDelegates<A> {
    private static final String INITIALIZED_KEY = "InitializedKey";

    private Boolean initialized;

    private void handleCreation(@Nullable Bundle bundle) {
        setContentView(getContentView());
        init();

        loadViews();
        initContainer();

        create();

        if ((initialized == null || !initialized) && bundle == null) {
            initialize();
            initialized = true;
        }
    }

    @Deprecated
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        handleCreation(savedInstanceState);
    }

    @Deprecated
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleCreation(savedInstanceState);
    }

    @Deprecated
    @Override
    protected void onStart() {
        beforeAppearing();
        super.onStart();
    }

    @Deprecated
    @Override
    protected void onResume() {
        super.onResume();
        afterAppearing();

        if (initialized != null && initialized) {
            surfaceRetainables();
        }
    }

    @Deprecated
    @Override
    protected void onPause() {
        submergeRetainables();
        beforeHiding();
        super.onPause();
    }

    @Deprecated
    @Override
    protected void onStop() {
        super.onStop();
        afterHiding();
    }

    @Deprecated
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Deprecated
    @Override
    protected void onDestroy() {
        beforeDismissing();
        super.onDestroy();
        dismiss();
    }

    @CallSuper
    @Override
    public void onPreSaveInstance(@NonNull Container writeOnlyContainer) {
        super.onPreSaveInstance(writeOnlyContainer);
        writeOnlyContainer.set(INITIALIZED_KEY, initialized);
    }

    @CallSuper
    @Override
    public void onPreRestoreInstance(@NonNull Container readOnlyContainer) {
        super.onPreRestoreInstance(readOnlyContainer);
        initialized = (Boolean) readOnlyContainer.get(INITIALIZED_KEY);
    }

    @CallSuper
    @Override
    public void onRestoredInstanceReady(@NonNull A restoredInstance) {
        restore(restoredInstance);
        persistentContainer.clearUnboxables();
    }

    @CallSuper
    @Override
    public void onBackPressed() {
        popFragment();
        super.onBackPressed();
        resumeTopmost();
    }
}
