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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.izikode.izilib.chocopie.annotation.Ignore;
import com.izikode.izilib.chocopie.annotation.InstanceRestoration;
import com.izikode.izilib.chocopie.annotation.Restore;
import com.izikode.izilib.chocopie.delegate.ReparativeDelegates;
import com.izikode.izilib.chocopie.utility.Reflector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@InstanceRestoration(policy = InstanceRestoration.Policy.IGNORE_ALL_FIELDS)
public abstract class ReparativeActivity<A> extends LoaderActivity implements ReparativeDelegates<A> {

    @Nullable
    protected abstract HashMap<String, Object> recollectFailedMapReady();

    private void saveData(@NonNull ReparativeDelegates.Container writeOnlyContainer) {
        InstanceRestoration restorationAnnotation = this.getClass().getAnnotation(InstanceRestoration.class);
        Field[] fields = null;

        if (restorationAnnotation.policy() == InstanceRestoration.Policy.IGNORE_ALL_FIELDS) {
            fields = Reflector.getAnnotatedFields(this, Restore.class);
        } else {
            fields = Reflector.getNonAnnotatedFields(this, Ignore.class);
        }

        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {

                    Object object = Reflector.readValue(this, field);
                    writeOnlyContainer.set(field.getName(), object);

                } catch (Reflector.ReflectorException ignored) {}
            }

            HashMap<String, Object> saveFailedMap = writeOnlyContainer.getUnboxable();
            if (saveFailedMap.size() > 0) {
                onSaveFailedMapReady(saveFailedMap);
            }

            writeOnlyContainer.flush();
        }
    }

    @Nullable
    private A getRestoredInstance(@Nullable ReparativeDelegates.Container readOnlyContainer) {
        HashMap<String, Object> saveFailedMap = recollectFailedMapReady();

        if ((readOnlyContainer == null || readOnlyContainer.size() <= 0) && (saveFailedMap == null || saveFailedMap.size() <= 0)) {
            return null;
        }

        A restoredInstance = null;

        try {
            restoredInstance = (A) this.getClass().newInstance();
        } catch (Exception ignored) {
            return null;
        }

        if (readOnlyContainer != null && readOnlyContainer.size() > 0) {
            for (String key : readOnlyContainer.keySet()) {
                Object value = readOnlyContainer.get(key);

                if (value != null) {
                    Field field = null;

                    try {

                        field = restoredInstance.getClass().getDeclaredField(key);

                    } catch (NoSuchFieldException ignored) {
                        continue;
                    }

                    try {

                        Reflector.setValue(restoredInstance, field, value);

                    } catch (Reflector.ReflectorException ignored) {}
                }
            }
        }

        if (saveFailedMap != null && saveFailedMap.size() > 0) {
            for (Map.Entry<String, Object> entry : saveFailedMap.entrySet()) {
                Object value = entry.getValue();

                if (value == null) {
                    continue;
                }

                Field field = null;

                try {

                    field = restoredInstance.getClass().getDeclaredField(entry.getKey());

                } catch (NoSuchFieldException ignored) {
                    continue;
                }

                try {

                    Reflector.setValue(restoredInstance, field, value);

                } catch (Reflector.ReflectorException ignored) {}
            }
        }

        return restoredInstance;
    }

    private void handleSave(@NonNull Bundle bundle) {
        ReparativeDelegates.Container container = ReparativeDelegates.Container.write(bundle);

        onPreSaveInstance(container);
        saveData(container);
    }

    private void handleRestore(@Nullable Bundle bundle) {
        ReparativeDelegates.Container container = null;

        if (bundle != null) {
            container = ReparativeDelegates.Container.read(bundle);
            onPreRestoreInstance(container);
        }

        A restoredInstance = getRestoredInstance(container);

        if (restoredInstance != null) {
            onRestoredInstanceReady(restoredInstance);
        }
    }

    @Deprecated
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        handleSave(outState);
    }

    @Deprecated
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        handleSave(outState);
    }

    @Deprecated
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        handleRestore(savedInstanceState);
    }

    @Deprecated
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        handleRestore(savedInstanceState);
    }
}
