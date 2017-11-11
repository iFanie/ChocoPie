package com.izikode.izilib.chocopiesample

import com.izikode.izilib.chocopie.activity.YumActivity

class MainActivity : YumActivity() {
    override fun getContentView(): Int = R.layout.activity_main
    override fun getFragmentContainer(): Int? = R.id.fragment_root

    override fun create() {

    }

    override fun initialize() {
        addFragment(MainFragment())
    }

    override fun restore(savedInstance: YumActivity) {

    }
}
