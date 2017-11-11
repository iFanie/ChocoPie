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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.izikode.izilib.chocopie.fragment.PersistentFragment;
import com.izikode.izilib.chocopie.fragment.core.PossessiveFragment;
import com.izikode.izilib.chocopie.utility.Reflector;
import com.izikode.izilib.chocopie.utility.Retainable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PossessiveActivity<A> extends ReparativeActivity<A> {

    private PersistentFragment persistentFragment;

    protected PersistentFragment.Container persistentContainer;
    private HashMap<PossessiveFragment, PossessiveFragment.ContainerRequest> pendingContainers;

    private List<Field> retainables;

    public void requestPersistentContainer(PossessiveFragment fragment, PossessiveFragment.ContainerRequest containerRequest) {
        if (persistentFragment != null) {
            fragment.setPersistentContainer(persistentFragment.get(fragment));

            if (containerRequest != null) {
                containerRequest.persistentContainerReady();
            }
        } else {
            pendingContainers.put(fragment, containerRequest);
        }
    }

    public void requestPersistentContainer(PossessiveFragment fragment) {
        requestPersistentContainer(fragment, null);
    }

    public PersistentFragment.Container getPersistentContainer(PossessiveFragment fragment) {
        if (persistentFragment != null) {
            return persistentFragment.get(fragment);
        }

        return null;
    }

    @CallSuper
    @Override
    protected void init() {
        super.init();

        pendingContainers = new HashMap<>();

        retainables = new ArrayList<>();
        retainables.addAll(Reflector.getDeclaredFieldsByType(this, Retainable.class));

        persistentFragment = (PersistentFragment)
                fragmentManager.findFragmentByTag(PersistentFragment.IDENTIFIER);

        if (persistentFragment == null) {
            persistentFragment = new PersistentFragment();
            fragmentManager.beginTransaction().add(persistentFragment, PersistentFragment.IDENTIFIER).commit();
        }
    }

    @Override
    public void onSaveFailedMapReady(@NonNull HashMap<String, Object> saveFailedMap) {
        for (Map.Entry<String, Object> entry : saveFailedMap.entrySet()) {
            Object data = entry.getValue();

            if (persistentContainer != null) {
                if (data instanceof Field) {
                    persistentContainer.holdUnboxable((Field) data);
                } else {
                    persistentContainer.holdUnboxable(entry.getKey(), data);
                }
            }
        }
    }

    @Nullable
    @Override
    protected HashMap<String, Object> recollectFailedMapReady() {
        return persistentContainer != null ? persistentContainer.getUnboxables() : null;
    }

    protected void submergeRetainables() {
        for (Field field : retainables) {
            try {

                Retainable retainable = (Retainable) Reflector.readValue(this, field);

                if (retainable != null) {
                    Object value = retainable.getValue();

                    if (value != null && persistentContainer != null) {
                        retainable.preSubmerged(value);
                        persistentContainer.holdRetainable(field.getName(), value);
                    }
                }

            } catch (Reflector.ReflectorException ignored) {}
        }
    }

    protected void surfaceRetainables() {
        for (Field field : retainables) {
            try {

                Retainable retainable = (Retainable) Reflector.readValue(this, field);

                if (retainable != null) {
                    Object value = persistentContainer.getRetainable(field.getName());

                    if (value != null) {
                        retainable.setValue(value);
                        retainable.postSurfaced(value);
                    }
                }

            } catch (Reflector.ReflectorException ignored) {}
        }

        persistentContainer.clearRetainables();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof PersistentFragment) {
            persistentContainer = ((PersistentFragment) fragment).get();

            if (pendingContainers != null && pendingContainers.size() > 0) {
                for (Map.Entry<PossessiveFragment, PossessiveFragment.ContainerRequest> entry : pendingContainers.entrySet()) {
                    PossessiveFragment pendingFragment = entry.getKey();
                    PossessiveFragment.ContainerRequest containerRequest = entry.getValue();

                    pendingFragment.setPersistentContainer(((PersistentFragment) fragment).get(pendingFragment));

                    if (containerRequest != null) {
                        containerRequest.persistentContainerReady();
                    }
                }

                pendingContainers.clear();
            }
        }
    }
}
