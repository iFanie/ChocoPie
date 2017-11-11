package com.izikode.izilib.chocopiesample

import android.support.v4.view.ViewPager
import com.izikode.izilib.chocopie.adapter.YumTabAdapter
import com.izikode.izilib.chocopie.annotation.Restore
import com.izikode.izilib.chocopie.fragment.YumFragment

class MainFragment : YumFragment() {
    override fun getContentView(): Int = R.layout.fragment_main

    @Restore
    lateinit var pager: ViewPager

    lateinit var adapter: YumTabAdapter

    override fun create() {
        adapter = YumTabAdapter(context, childFragmentManager, tag)
        adapter.setData(FragmentOne::class.java, FragmentTwo::class.java)

        pager.adapter = adapter
    }

    override fun initialize() {

    }

    override fun restore(savedInstance: YumFragment) {
        val instance = savedInstance as MainFragment
        pager.currentItem = instance.pager.currentItem
    }
}