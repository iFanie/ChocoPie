package com.izikode.izilib.chocopiesample

import android.support.v4.view.ViewPager
import com.izikode.izilib.chocopie.adapter.YumTabAdapter
import com.izikode.izilib.chocopie.annotation.Restore
import com.izikode.izilib.chocopie.fragment.YumFragment

/**
 * Created by UserOne on 05/11/2017.
 */

class FragmentTwo : YumFragment() {
    override fun getContentView(): Int = R.layout.fragment_two

    @Restore
    lateinit var pager: ViewPager

    lateinit var adapter: YumTabAdapter

    override fun create() {
        adapter = YumTabAdapter(this.context, parent.supportFragmentManager)
        adapter.setData(TabFragment::class.java, TabFragment::class.java)

        pager.adapter = adapter
    }

    override fun initialize() {

    }

    override fun restore(savedInstance: YumFragment) {
        val instance = savedInstance as FragmentTwo

        pager.currentItem = instance.pager.currentItem
    }
}