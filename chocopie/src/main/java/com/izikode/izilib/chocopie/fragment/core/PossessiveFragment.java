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

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.izikode.izilib.chocopie.fragment.PersistentFragment;
import com.izikode.izilib.chocopie.utility.Reflector;
import com.izikode.izilib.chocopie.utility.Retainable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PossessiveFragment<F> extends ReparativeFragment<F> {

    public interface ContainerRequest {
        void persistentContainerReady();
    }

    protected PersistentFragment.Container persistentContainer;

    public void setPersistentContainer(PersistentFragment.Container persistentContainer) {
        this.persistentContainer = persistentContainer;
    }

    private List<Field> retainables;

    @CallSuper
    @Override
    protected void init() {
        super.init();

        retainables = new ArrayList<>();
        retainables.addAll(Reflector.getDeclaredFieldsByType(this, Retainable.class));
    }

    @Override
    public void onSaveFailedMapReady(@NonNull final HashMap<String, Object> saveFailedMap) {
        if (persistentContainer != null) {
            buildSaveFailedMap(saveFailedMap);
        } else {
            getParent().requestPersistentContainer(this, new ContainerRequest() {

                @Override
                public void persistentContainerReady() {
                    buildSaveFailedMap(saveFailedMap);
                }
            });
        }
    }

    private void buildSaveFailedMap(@NonNull HashMap<String, Object> saveFailedMap) {
        for (Map.Entry<String, Object> entry : saveFailedMap.entrySet()) {
            Object data = entry.getValue();

            if (data instanceof Field) {
                persistentContainer.holdUnboxable((Field) data);
            } else {
                persistentContainer.holdUnboxable(entry.getKey(), data);
            }
        }
    }

    @Nullable
    @Override
    protected HashMap<String, Object> recollectFailedMapReady() {
        if (persistentContainer == null) {
            persistentContainer = getParent().getPersistentContainer(this);
        }

        return persistentContainer != null ? persistentContainer.getUnboxables() : null;
    }

    public void submergeRetainables() {
        if (isTopmost()) {
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

                } catch (Reflector.ReflectorException ignored) {
                }
            }
        }
    }

    public void surfaceRetainables() {
        if (isTopmost()) {
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

                } catch (Reflector.ReflectorException ignored) {
                }
            }

            persistentContainer.clearRetainables();
        }
    }
}
