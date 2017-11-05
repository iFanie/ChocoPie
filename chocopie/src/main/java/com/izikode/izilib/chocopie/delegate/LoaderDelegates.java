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

import android.support.annotation.NonNull;

import java.util.Locale;

public interface LoaderDelegates {

    @NonNull
    String getViewNamingFormat();

    @NonNull
    String getFinalViewStringId(@NonNull String viewStringId);

    enum ViewNaming {
        CLASS(1), NAME(2), TYPE(3);

        public final String formatPart;

        ViewNaming(int partIndex) {
            formatPart = "%" + partIndex + "$s";
        }
    }

    LoaderDelegates DEFAULT_LOADER_DELEGATES = new LoaderDelegates() {

        @NonNull
        @Override
        public String getViewNamingFormat() {
            return ViewNaming.CLASS.formatPart + "_" + ViewNaming.NAME.formatPart;
        }

        @NonNull
        @Override
        public String getFinalViewStringId(@NonNull String viewStringId) {
            return viewStringId.toLowerCase(Locale.ENGLISH);
        }
    };
}
