package com.izikode.izilib.chocopiesample

import com.izikode.izilib.chocopie.fragment.YumFragment
import com.izikode.izilib.chocopie.fragment.YumTabFragment

/**
 * Created by UserOne on 05/11/2017.
 */

class TabFragment : YumTabFragment() {
    override fun getContentView(): Int = R.layout.tab_fragment
    override fun getTitleResource(): Int = R.string.tab_title
    override fun getBackgroundColor(): Int? = R.color.colorPrimary

    override fun create() {

    }

    override fun initialize() {

    }

    override fun restore(savedInstance: YumFragment) {

    }
}