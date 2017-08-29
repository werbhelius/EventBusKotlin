package com.werb.eventbuskotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.werb.eventbus.DEFAULT_TAG
import com.werb.eventbus.EventBus
import com.werb.eventbus.Subscriber
import com.werb.eventbus.ThreadMode
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.content, MyFragment(), MyFragment::class.java.name).commitAllowingStateLoss()

        button.setOnClickListener {
            EventBus.post(ToastEvent(), "fragment")
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.unRegister(this)
    }

    @Subscriber(mode = ThreadMode.BACKGROUND)
    private fun toast(event: ToastEvent){
        run()
    }

    private fun run() {
        val forecastJsonStr = URL(" https://demo.duodian.com/api/v1/topics?type=latest").readText()
        println(forecastJsonStr)
    }
}
