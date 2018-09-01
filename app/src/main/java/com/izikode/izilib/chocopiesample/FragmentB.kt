package com.izikode.izilib.chocopiesample

import android.widget.Button
import com.izikode.izilib.chocopie.fragment.YumFragment

/**
 * Created by UserOne on 02/03/2018.
 */

class FragmentB : YumFragment() {

    override fun getContentView(): Int = R.layout.fragment_b
    override fun isStackable(): Boolean = false

    lateinit var c: Button

    override fun create() {
        c.setOnClickListener {
            parent?.addFragment(FragmentC())
        }
    }

    override fun initialize() {

    }

    override fun restore(savedInstance: YumFragment) {

    }

}