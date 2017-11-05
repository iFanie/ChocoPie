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

import android.support.annotation.NonNull;

import java.util.UUID;

public class UniqueTag {

    public UniqueTag(@NonNull Object source) {
        originator = UUID.nameUUIDFromBytes(source.getClass().getCanonicalName().getBytes()).toString();
    }

    private final String originator;
    private Integer index;

    public String getOriginator() {
        return originator;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        if (this.index == null) {
            this.index = index;
        }
    }

    public String read() {
        return (index != null ? index : "") + "_" + originator;
    }
}
