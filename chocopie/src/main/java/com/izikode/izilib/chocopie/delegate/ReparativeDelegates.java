package com.izikode.izilib.chocopie.delegate;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.izikode.izilib.chocopie.utility.ObjectSerializer;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

public interface ReparativeDelegates<T> {

    class Container {
        private static final String MAP_KEY = Container.class.getCanonicalName() + ":InternalMapKey";

        public enum Mode {
            READ, WRITE
        }

        public static class ModeException extends Exception {
            public ModeException(@NonNull String message) {
                super(message);
            }
        }

        @NonNull
        public static Container read(@NonNull Bundle bundle) {
            return new Container(bundle, Mode.READ);
        }

        @NonNull
        public static Container write(@NonNull Bundle bundle) {
            return new Container(bundle, Mode.WRITE);
        }

        private final Bundle bundle;
        private final Mode mode;

        private final HashMap<String, String> map;
        private final HashMap<String, Object> unboxable;

        private Container(@NonNull Bundle bundle, @NonNull Mode mode) {
            this.bundle = bundle;
            this.mode = mode;

            if (mode == Mode.WRITE || !bundle.containsKey(MAP_KEY)) {
                map = new HashMap<>();
            } else {
                Serializable serializedMap = bundle.getSerializable(MAP_KEY);
                Object unboxedMap = ObjectSerializer.unbox(serializedMap);

                if (unboxedMap != null && unboxedMap instanceof HashMap) {
                    map = (HashMap<String, String>) unboxedMap;
                } else {
                    map = new HashMap<>();
                }
            }

            unboxable = (mode == Mode.WRITE) ? new HashMap<String, Object>() : null;
        }

        @Nullable
        private String toIso(Serializable serializable) {
            if (serializable != null) {
                try {
                    return new String((byte[]) serializable, "ISO-8859-1");
                } catch (UnsupportedEncodingException ignored) {}
            }

            return null;
        }

        @Nullable
        private Serializable fromIso(String string) {
            if (string != null) {
                try {
                    return (Serializable) string.getBytes("ISO-8859-1");
                } catch (UnsupportedEncodingException ignored) {}
            }

            return null;
        }

        @Nullable
        public Object get(@NonNull String key) {
            if (mode != Mode.READ) {
                throw new RuntimeException(new ModeException("Container is not in READ mode."));
            }

            Object firstPass = ObjectSerializer.unbox(fromIso(map.get(key)));

            if (firstPass == null) {
                return null;
            }

            if (!(firstPass instanceof byte[])) {
                return firstPass;
            }

            return ObjectSerializer.unbox((Serializable) firstPass);
        }

        @NonNull
        public Set<String> keySet() {
            if (mode != Mode.READ) {
                throw new RuntimeException(new ModeException("Container is not in READ mode."));
            }

            return map.keySet();
        }

        public int size() {
            if (mode != Mode.READ) {
                throw new RuntimeException(new ModeException("Container is not in READ mode."));
            }

            return map.size();
        }

        public void set(@NonNull String key, @Nullable Object value) {
            if (mode != Mode.WRITE) {
                throw new RuntimeException(new ModeException("Container is not in WRITE mode."));
            }

            if (!ObjectSerializer.isBoxable(value)) {
                unboxable.put(key, value);
            } else {
                Serializable serializedValue = ObjectSerializer.box(value);
                map.put(key, toIso(serializedValue));
            }
        }

        public void flush() {
            if (mode != Mode.WRITE) {
                throw new RuntimeException(new ModeException("Container is not in WRITE mode."));
            }

            Serializable serializedMap = ObjectSerializer.box(map);
            bundle.putSerializable(MAP_KEY, serializedMap);
        }

        public HashMap<String, Object> getUnboxable() {
            if (mode != Mode.WRITE) {
                throw new RuntimeException(new ModeException("Container is not in WRITE mode."));
            }

            return unboxable;
        }
    }

    void onPreSaveInstance(@NonNull Container writeOnlyContainer);
    void onSaveFailedMapReady(@NonNull HashMap<String, Object> saveFailedMap);

    void onPreRestoreInstance(@NonNull Container readOnlyContainer);
    void onRestoredInstanceReady(@NonNull T restoredInstance);
}
