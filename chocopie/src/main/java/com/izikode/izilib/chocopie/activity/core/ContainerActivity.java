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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.izikode.izilib.chocopie.delegate.ContainerDelegates;
import com.izikode.izilib.chocopie.fragment.core.LifecycleFragment;
import com.izikode.izilib.chocopie.utility.Parameterized;

public abstract class ContainerActivity<A> extends PossessiveActivity<A> implements ContainerDelegates {
    private static final String FRAGMENT_STACK_COUNT_KEY = "FragmentStackCountKey";
    private static final String FRAGMENT_ABSOLUTE_INDEX_KEY = "FragmentAbsoluteIndexKey";

    private View fragmentContainer;

    private Integer fragmentStackCount;
    private Integer fragmentAbsoluteIndex;

    @CallSuper
    @Override
    protected void init() {
        super.init();

        fragmentStackCount = 0;
        fragmentAbsoluteIndex = 0;
    }

    protected void initContainer() {
        Integer containerId = getFragmentContainer();
        if (containerId != null) {
            fragmentContainer = findViewById(containerId);
        }
    }

    @CallSuper
    @Override
    public void onPreSaveInstance(@NonNull Container writeOnlyContainer) {
        writeOnlyContainer.set(FRAGMENT_STACK_COUNT_KEY, fragmentStackCount);
        writeOnlyContainer.set(FRAGMENT_ABSOLUTE_INDEX_KEY, fragmentAbsoluteIndex);
    }

    @CallSuper
    @Override
    public void onPreRestoreInstance(@NonNull Container readOnlyContainer) {
        Integer savedStackCount = (Integer) readOnlyContainer.get(FRAGMENT_STACK_COUNT_KEY);
        if (savedStackCount != null) {
            fragmentStackCount = savedStackCount;
        }

        Integer savedAbsoluteIndex = (Integer) readOnlyContainer.get(FRAGMENT_ABSOLUTE_INDEX_KEY);
        if (savedAbsoluteIndex != null) {
            fragmentAbsoluteIndex = savedAbsoluteIndex;
        }
    }

    public LifecycleFragment getTopmostFragment() {
        if (fragmentContainer != null) {
            Fragment fragment = fragmentManager.findFragmentById(fragmentContainer.getId());

            if (fragment != null && fragment instanceof LifecycleFragment) {
                return (LifecycleFragment) fragment;
            }
        }

        return null;
    }

    public void clearFragments() {
        if (fragmentContainer != null) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }

            persistentContainer.wipe();
            fragmentStackCount = 0;
        }
    }

    public void addFragment(@NonNull LifecycleFragment fragment) {
        boolean addToBackStack = false;

        LifecycleFragment topmost = getTopmostFragment();
        if (topmost != null && topmost.isStackable() && fragmentStackCount > 0) {
            addToBackStack = true;
        } else if (topmost != null && !topmost.isStackable()) {
            getPersistentContainer(topmost).wipe();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (topmost != null && topmost.isStackable()) {
            transaction.setCustomAnimations(fragment.getEnterAnimation(), fragment.getExitAnimation(),
                    topmost.getEnterAnimation(), topmost.getExitAnimation());
        } else {
            transaction.setCustomAnimations(fragment.getEnterAnimation(), fragment.getExitAnimation());
        }

        fragment.tag.setIndex(fragmentAbsoluteIndex);
        fragment.setPersistentContainer(getPersistentContainer(fragment));

        transaction.add(fragmentContainer.getId(), fragment, fragment.tag.read());

        if (addToBackStack) {
            transaction.addToBackStack(topmost.tag.read());
        }

        if (fragment.isStackable()) {
            fragmentStackCount = (fragmentStackCount >= 0 ? fragmentStackCount : 0) + 1;
        }

        transaction.commit();

        if (topmost != null) {
            topmost.onPause();
        }

        fragmentAbsoluteIndex++;
    }

    public void addFragment(Parameterized fragment) {
        try {

            addFragment(fragment.getFragment());

        } catch (IllegalAccessException ignored) {} catch (InstantiationException ignored) {}
    }

    public void addSoleFragment(LifecycleFragment fragment) {
        clearFragments();
        addFragment(fragment);
    }

    public void addSoleFragment(Parameterized fragment) {
        try {

            addSoleFragment(fragment.getFragment());

        } catch (IllegalAccessException ignored) {} catch (InstantiationException ignored) {}
    }

    protected void popFragment() {
        LifecycleFragment pop = getTopmostFragment();

        if (pop != null) {
            pop.beforeDismissing();

            getPersistentContainer(pop).wipe();
            fragmentStackCount--;
        }
    }

    protected void resumeTopmost() {
        LifecycleFragment top = getTopmostFragment();
        if (top != null) {
            if (top.isStackable()) {
                top.onResume();
            }
        }
    }
}
