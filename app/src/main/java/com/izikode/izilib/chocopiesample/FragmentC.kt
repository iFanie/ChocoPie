package com.izikode.izilib.chocopiesample

import android.widget.Button
import com.izikode.izilib.chocopie.fragment.YumFragment

/**
 * Created by UserOne on 02/03/2018.
 */

class FragmentC : YumFragment() {

    override fun getContentView(): Int = R.layout.fragment_c
    override fun isStackable(): Boolean = false

    lateinit var b: Button

    override fun create() {
        b.setOnClickListener {
            parent?.addFragment(FragmentB())
        }
    }

    override fun initialize() {

    }

    override fun restore(savedInstance: YumFragment) {

    }

}