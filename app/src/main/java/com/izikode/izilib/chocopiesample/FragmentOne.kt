package com.izikode.izilib.chocopiesample

import android.widget.Button
import android.widget.TextView
import com.izikode.izilib.chocopie.annotation.Restore
import com.izikode.izilib.chocopie.fragment.YumFragment
import com.izikode.izilib.chocopie.fragment.YumTabFragment

class FragmentOne : YumTabFragment() {
    override fun getContentView(): Int = R.layout.fragment_one
    override fun getTitleResource(): Int = R.string.title_one

    @Restore
    var label: TextView? = null

    lateinit var change: Button

    override fun create() {
        change.setOnClickListener {
            label?.text = "hello again"
        }
    }

    override fun initialize() {
        label?.text = "hello world"
    }

    override fun restore(savedInstance: YumFragment) {
        val instance = savedInstance as FragmentOne

        if (instance.label != null) {
            label?.text = instance.label?.text
        }
    }
}