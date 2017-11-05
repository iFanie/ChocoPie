package com.izikode.izilib.chocopiesample

import com.izikode.izilib.chocopie.activity.YumActivity

/**
 * Created by UserOne on 05/11/2017.
 */

class FragmentActivity : YumActivity() {
    override fun getFragmentContainer(): Int? = R.id.fragment_container
    override fun getContentView(): Int = R.layout.activity_fragment

    override fun create() {}

    override fun initialize() {
        addFragment(FragmentOne())
    }

    override fun restore(savedInstance: YumActivity) {}
}