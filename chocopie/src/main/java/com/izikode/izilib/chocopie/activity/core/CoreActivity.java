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

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.izikode.izilib.chocopie.fragment.core.LifecycleFragment;

import java.util.Locale;

public abstract class CoreActivity<A> extends LifecycleActivity<A> {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LifecycleFragment topmost = getTopmostFragment();

        if (topmost != null) {
            topmost.onCreateOptionsMenu(menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LifecycleFragment topmost = getTopmostFragment();
        if (topmost != null) {
            topmost.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public Locale getDefaultSystemLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            return getResources().getConfiguration().locale;
        }
    }

    public String getDefaultSystemLanguageCode() {
        return getDefaultSystemLocale().getLanguage();
    }

    public String getVersionCode() {
        try {

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return String.format("%1$s (%2$s)", packageInfo.versionName, packageInfo.versionCode);

        } catch (PackageManager.NameNotFoundException ignored) {
            return "-";
        }
    }
}
