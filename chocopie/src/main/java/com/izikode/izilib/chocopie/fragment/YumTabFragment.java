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

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

public abstract class YumTabFragment extends YumFragment {

    @StringRes
    public abstract int getTitleResource();

    @DrawableRes
    public int getIconResource() {
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (tag.getIndex() == null) {
            Fragment parent = getParentFragment();

            if (parent instanceof YumFragment) {
                tag.setIndex(((YumFragment) parent).tag.getIndex());
            }
        }
    }
}
