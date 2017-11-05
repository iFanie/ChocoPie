package com.izikode.izilib.chocopie.adapter;

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
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.izikode.izilib.chocopie.fragment.YumTabFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YumTabAdapter extends FragmentPagerAdapter {

    private Context adapterContext;
    private List<Class<? extends YumTabFragment>> adapterData;

    public YumTabAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        adapterContext = context;
        adapterData = new ArrayList<>();
    }

    @Override
    public YumTabFragment getItem(int position) {
        try {

            return adapterData.get(position).newInstance();

        } catch (InstantiationException ignored) {
            return null;
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        try {

            YumTabFragment instance = adapterData.get(position).newInstance();

            int titleResource = instance.getTitleResource();
            String title = adapterContext.getResources().getString(titleResource);

            int iconResource = instance.getIconResource();

            SpannableStringBuilder spannableStringBuilder = null;
            if (iconResource != 0) {
                spannableStringBuilder = new SpannableStringBuilder("   " + title);
                Drawable icon = adapterContext.getResources().getDrawable(iconResource);

                icon.setBounds(5, 5, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(icon, DynamicDrawableSpan.ALIGN_BASELINE);

                spannableStringBuilder.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableStringBuilder = new SpannableStringBuilder(title);
            }

            return spannableStringBuilder;

        } catch (InstantiationException ignored) {
            return null;
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    public TabLayout.Tab getTab(TabLayout layout, int position) {
        TabLayout.Tab tab = layout.newTab();
        tab.setText(getPageTitle(position));
        return tab;
    }

    public int getCount() {
        return adapterData.size();
    }

    public void addData(List<Class<? extends YumTabFragment>> appendingData) {
        adapterData.addAll(appendingData);
        notifyDataSetChanged();
    }

    public void addData(Class<? extends YumTabFragment>... appendingData) {
        addData(Arrays.asList(appendingData));
    }

    public void setData(List<Class<? extends YumTabFragment>> newData) {
        adapterData.clear();
        addData(newData);
    }

    public void setData(Class<? extends YumTabFragment>... newData) {
        setData(Arrays.asList(newData));
    }

    public ArrayList<Class<? extends YumTabFragment>> getData() {
        return new ArrayList<>(adapterData);
    }

    public int getOffscreenPageLimit() {
        int limit = adapterData.size() / 2;
        return limit >= 3 ? limit : 3;
    }
}
