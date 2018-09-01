package com.izikode.izilib.chocopiesample

import android.widget.Button
import com.izikode.izilib.chocopie.fragment.YumFragment

/**
 * Created by UserOne on 02/03/2018.
 */

class FragmentA : YumFragment() {

    override fun getContentView(): Int = R.layout.fragment_a

    lateinit var b: Button
    lateinit var c: Button

    override fun create() {
        b.setOnClickListener {
            parent?.addFragment(FragmentB())
        }

        c.setOnClickListener {
            parent?.addFragment(FragmentC())
        }
    }

    override fun initialize() {

    }

    override fun restore(savedInstance: YumFragment) {

    }

}