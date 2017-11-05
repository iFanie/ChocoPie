package com.izikode.izilib.chocopie.fragment;

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
import android.support.annotation.Nullable;
import android.view.View;

import com.izikode.izilib.chocopie.activity.core.PossessiveActivity;
import com.izikode.izilib.chocopie.fragment.core.BaseFragment;
import com.izikode.izilib.chocopie.fragment.core.PossessiveFragment;
import com.izikode.izilib.chocopie.utility.Reflector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PersistentFragment extends BaseFragment {
    public static final String IDENTIFIER = PersistentFragment.class.getCanonicalName();

    public static class Container {
        private PersistentFragment main;
        private Object instance;
        private String tag;

        public Container(PersistentFragment main, PossessiveActivity activity, PossessiveFragment fragment) {
            this.main = main;
            this.instance = fragment != null ? fragment : activity;
            this.tag = fragment != null ? fragment.tag.read() : null;
        }

        public HashMap<String, Object> getUnboxables() {
            if (tag == null) {
                return main.activityUnboxables;
            } else {
                if (main.fragmentUnboxables.containsKey(tag)) {
                    return main.fragmentUnboxables.get(tag);
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    main.fragmentUnboxables.put(tag, map);

                    return map;
                }
            }
        }

        public void clearUnboxables() {
            getUnboxables().clear();
        }

        private Object processUnboxable(Object object) {
            if (object instanceof View) {
                try {

                    Method internalClone = Reflector.locateMethodInSuperclass(object, Object.class, "internalClone");
                    Object cloneObject = Reflector.getValue(object, internalClone);

                    if (cloneObject != null) {
                        Field mContext = Reflector.locateFieldInSuperclass(cloneObject, View.class, "mContext");
                        Reflector.setValue(cloneObject, mContext, null);

                        return cloneObject;
                    }

                } catch (Reflector.ReflectorException ignored) {} catch (NoSuchMethodException ignored) {
                } catch (IllegalAccessException ignored) {} catch (NoSuchFieldException ignored) {}
            }

            return object;
        }

        public void holdUnboxable(String field, Object object) {
            getUnboxables().put(field, processUnboxable(object));
        }

        public void holdUnboxable(Field field) {
            try {

                Object object = Reflector.readValue(instance, field);
                holdUnboxable(field.getName(), object);

            } catch (Reflector.ReflectorException ignored) {}
        }

        public Object getUnboxable(String field) {
            return getUnboxables().get(field);
        }

        public HashMap<String, Object> getRetainables() {
            if (tag == null) {
                return main.activityRetainables;
            } else {
                if (main.fragmentRetainables.containsKey(tag)) {
                    return main.fragmentRetainables.get(tag);
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    main.fragmentRetainables.put(tag, map);

                    return map;
                }
            }
        }

        public void clearRetainables() {
            getRetainables().clear();
        }

        public void holdRetainable(String field, Object value) {
            getRetainables().put(field, value);
        }

        public Object getRetainable(String field) {
            return getRetainables().get(field);
        }

        public void wipe() {
            clearRetainables();
            clearUnboxables();
        }
    }

    private HashMap<String, Object> activityRetainables;
    private HashMap<String, Object> activityUnboxables;

    private HashMap<String, HashMap<String, Object>> fragmentRetainables;
    private HashMap<String, HashMap<String, Object>> fragmentUnboxables;

    public PersistentFragment() {
        super();

        activityRetainables = new HashMap<>();
        activityUnboxables = new HashMap<>();

        fragmentRetainables = new HashMap<>();
        fragmentUnboxables = new HashMap<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Container get() {
        return new Container(this, getParent(), null);
    }

    public Container get(PossessiveFragment fragment) {
        return new Container(this, null, fragment);
    }
}
