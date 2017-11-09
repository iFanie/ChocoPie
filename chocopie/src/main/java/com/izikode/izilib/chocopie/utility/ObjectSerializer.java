package com.izikode.izilib.chocopie.utility;

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
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSerializer {

    public static boolean isBoxable(@Nullable Object value) {
        if (value == null || value instanceof Serializable) {
            return true;
        }

        for (Class unboxable : new Class[] { Activity.class, Fragment.class, View.class, Context.class }) {
            if (unboxable.isAssignableFrom(value.getClass())) {
                return false;
            }
        }

        try {
            return box(value) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Nullable
    public static Serializable box(@Nullable Object value) {
        if (value == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = null;

        try {

            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(value);
            objectOutput.flush();

            byte[] yourBytes = byteArrayOutputStream.toByteArray();
            return (Serializable) yourBytes;

        } catch (IOException ignored) {} finally {
            try { byteArrayOutputStream.close(); } catch (IOException ignored) {}
        }

        return null;
    }

    @Nullable
    public static Object unbox(@Nullable Serializable value) {
        if (value == null) {
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((byte[]) value);
        ObjectInput objectInput = null;

        try {

            objectInput = new ObjectInputStream(byteArrayInputStream);
            return objectInput.readObject();

        } catch (IOException | ClassNotFoundException ignored) {} finally {
            try {

                if (objectInput != null) {
                    objectInput.close();
                }

            } catch (IOException ignored) {}
        }

        return null;
    }
}
