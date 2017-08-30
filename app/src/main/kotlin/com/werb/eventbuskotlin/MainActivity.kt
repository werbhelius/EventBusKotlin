package com.werb.eventbuskotlin

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
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
            EventBus.post(ToastEvent())
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

    @Subscriber()
    private fun toast(event: ToastEvent){
        Toast.makeText(this, "lalallalalalal", Toast.LENGTH_SHORT).show()
        println("myActivity - 101-" + Thread.currentThread().name)
    }

    private fun run() {
        val forecastJsonStr = URL(" https://demo.duodian.com/api/v1/topics?type=latest").readText()
        println(forecastJsonStr)
    }
}
