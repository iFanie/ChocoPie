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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.izikode.izilib.chocopie.activity.core.LoaderActivity;
import com.izikode.izilib.chocopie.delegate.LoaderDelegates;
import com.izikode.izilib.chocopie.fragment.core.LoaderFragment;

import java.lang.reflect.Field;

public class YumLoader {

    public static class ParsingException extends Exception {
        public ParsingException() {
            super();
        }

        public ParsingException(String message) {
            super(message);
        }
    }

    private static class Target {

        @NonNull
        public static Target view(View view) {
            return new Target(view, null);
        }

        @NonNull
        public static Target context(Context context) {
            return new Target(null, context);
        }

        public final View view;
        public final Context context;

        private Target(View targetView, Context targetContext) {
            view = targetView;
            context = targetContext;
        }
    }

    @Nullable
    private static Target getTarget(@NonNull Object object) {
        if (object instanceof View) {
            return Target.view((View) object);
        }

        if (object instanceof Context) {
            return Target.context((Context) object);
        }

        for (String possibleField : new String[] { "rootView", "mRootView", "view", "mView", "context", "mContext" }) {
            try {

                Object possibleResult = Reflector.getFieldValue(object, possibleField);

                if (possibleResult instanceof View) {
                    return Target.view((View) possibleResult);
                } else if (possibleResult instanceof Context) {
                    return Target.context((Context) possibleResult);
                }

            } catch (Exception ignored) {}
        }

        for (String possibleMethod : new String[] { "getView", "getRootView", "getContext" }) {
            try {

                Object possibleResult = Reflector.getMethodExecutionValue(object, possibleMethod);

                if (possibleResult instanceof View) {
                    return Target.view((View) possibleResult);
                } else if (possibleResult instanceof Context) {
                    return Target.context((Context) possibleResult);
                }

            } catch (Exception ignored) {}
        }

        return null;
    }

    public static int parse(@NonNull Object parseTarget, @Nullable LoaderDelegates namingConvention) throws ParsingException {
        LoaderDelegates loaderDelegates = namingConvention != null ? namingConvention : LoaderDelegates.DEFAULT_LOADER_DELEGATES;
        Target target = getTarget(parseTarget);

        if (target == null || (target.view == null && target.context == null)) {
            throw new ParsingException("Unable to get valid View or Context from given Object.");
        }

        View rootView = null;
        Context rootContext = null;

        if (target.view != null) {
            rootView = target.view;
            rootContext = rootView.getContext();
        } else {
            rootView = ((Activity) target.context).getWindow().getDecorView().findViewById(android.R.id.content);
            rootContext = target.context;
        }

        if (rootView == null) {
            throw new ParsingException("Unable to get root View from given Object.");
        }

        int loadedViews = 0;
        for (Field field : Reflector.getDeclaredFieldsByType(parseTarget, View.class)) {
            String derivedStringId = String.format(loaderDelegates.getViewNamingFormat(),
                    field.getDeclaringClass().getSimpleName(),
                    field.getName(),
                    field.getType().getClass().getSimpleName()
            );

            String stringId = loaderDelegates.getFinalViewStringId(derivedStringId);

            int id = rootContext.getResources().getIdentifier(
                    stringId, "id",
                    rootContext.getApplicationContext().getPackageName());

            if (id != 0) {
                try {

                    Object value = field.getType().cast(rootView.findViewById(id));

                    if (value != null) {
                        Reflector.setValue(parseTarget, field, value);
                        loadedViews++;
                    }

                } catch (Reflector.ReflectorException ignored) {}
            }
        }

        return loadedViews;
    }

    public static int parse(@NonNull Object parseTarget) throws ParsingException {
        return parse(parseTarget, null);
    }

    public static int parse(@NonNull LoaderActivity parseTarget) throws ParsingException {
        return parse(parseTarget, parseTarget);
    }

    public static int parse(@NonNull LoaderFragment parseTarget) throws ParsingException {
        return parse(parseTarget, parseTarget);
    }
}