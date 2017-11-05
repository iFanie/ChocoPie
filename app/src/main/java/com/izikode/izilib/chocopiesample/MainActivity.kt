package com.izikode.izilib.chocopiesample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.izikode.izilib.chocopie.activity.YumActivity
import com.izikode.izilib.chocopie.annotation.Restore
import com.izikode.izilib.chocopie.utility.Retainable

class MainActivity : YumActivity() {
    override fun getContentView(): Int = R.layout.activity_main
    override fun getFragmentContainer(): Int? = R.id.fragment_root

    lateinit var start: Button
    lateinit var stop: Button
    lateinit var next: Button

    @Restore
    lateinit var label: TextView

    @Restore
    var counter: Int? = null

    lateinit var task: AsyncTask<Void?, Int?, Void?>
    private lateinit var taskRetainable: Retainable<AsyncTask<Void?, Int?, Void?>>

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

        start.setOnClickListener {
            if (task.status != AsyncTask.Status.RUNNING && task.status != AsyncTask.Status.FINISHED) {
                task.execute();
            }
        }

        stop.setOnClickListener {
            if (task.status == AsyncTask.Status.RUNNING) {
                task.cancel(true);
            }
        }

        next.setOnClickListener {
            startActivity(Intent(this, FragmentActivity::class.java))
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
                counter = values[0]
                label.text = ("" + values[0])
            }
        }

        taskRetainable.value = task

        label.text = "0"
        counter = 0
    }

    override fun restore(savedInstance: YumActivity) {
        val instance = savedInstance as MainActivity

        label.text = instance.label.text
        counter = instance.counter
    }
}
