package com.izikode.izilib.chocopiesample

import com.izikode.izilib.chocopie.fragment.YumFragment
import com.izikode.izilib.chocopie.fragment.YumTabFragment

class FragmentTwo : YumTabFragment() {
    override fun getContentView(): Int = R.layout.fragment_two
    override fun getTitleResource(): Int = R.string.title_two

    override fun create() {
    }

    override fun initialize() {
    }

    override fun restore(savedInstance: YumFragment) {
    }
}