package com.werb.eventbuskotlin

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
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


        supportFragmentManager.beginTransaction().add(R.id.content_layout, MyFragment(), MyFragment::class.java.name).commitAllowingStateLoss()

        login.setOnClickListener {
            LoginFragment().show(supportFragmentManager, "LoginFragment")
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

    @Subscriber
    private fun login(event: LoginEvent) {
        if (event.login) {
            login.text = "已登录"
            Handler().postDelayed({
                login.visibility = View.GONE
            }, 200)
        } else {
            login.text = "点击登录"
            login.visibility = View.VISIBLE
        }
    }

}
