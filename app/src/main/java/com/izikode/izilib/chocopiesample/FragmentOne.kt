package com.izikode.izilib.chocopiesample

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.izikode.izilib.chocopie.annotation.Restore
import com.izikode.izilib.chocopie.fragment.YumFragment
import com.izikode.izilib.chocopie.utility.Retainable

/**
 * Created by UserOne on 03/11/2017.
 */

class FragmentOne : YumFragment() {
    override fun getContentView(): Int = R.layout.fragment_one

    @Restore
    lateinit var text: TextView

    lateinit var hi: Button
    lateinit var tab: Button

    @Restore
    var counter: Int? = null

    lateinit var task: AsyncTask<Void?, Int?, Void?>
    lateinit var taskRetainable: Retainable<AsyncTask<Void?, Int?, Void?>>

    override fun create() {
        taskRetainable = object: Retainable<AsyncTask<Void?, Int?, Void?>>() {
            override fun preSubmerged(value: AsyncTask<Void?, Int?, Void?>) {
                Log.d("Possessive", "preSubmerged")
            }

            override fun postSurfaced(value: AsyncTask<Void?, Int?, Void?>) {
                Log.d("Possessive", "postSurfaced: " + value)
                task = value
            }
        }

        hi.setOnClickListener {
            text.text = "Nice " + counter + " meet you."
            counter = (counter!! + 1)

            if (task.status != AsyncTask.Status.RUNNING) {
                task.execute()
            }
        }

        tab.setOnClickListener {
            parent.addFragment(TabFragment())
        }
    }

    override fun initialize() {
        task = @SuppressLint("StaticFieldLeak") object: AsyncTask<Void?, Int?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                for (index in 1 .. 100) {
                    if (isCancelled) {
                        break
                    }

                    Thread.sleep(1000)
                    publishProgress(index)
                }

                return null
            }

            override fun onProgressUpdate(vararg values: Int?) {
                Log.d("PossesiveTask", "index: " + values[0])
            }
        }

        taskRetainable.value = task

        text.text = "Hello there."
        counter = 2;
    }

    override fun restore(savedInstance: YumFragment) {
        val instance = savedInstance as FragmentOne

        text.text = instance.text.text
        counter = instance.counter
    }

    override fun beforeDismissing() {

    }
}
