package com.werb.eventbuskotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.werb.eventbus.EventBus
import com.werb.eventbus.Subscriber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.title = getString(R.string.app_name)
        supportFragmentManager.beginTransaction().add(R.id.content_layout, MyFragment(), MyFragment::class.java.name).commitAllowingStateLoss()

        login.setOnClickListener {
            LoginDialogFragment.newInstance("login", "A Simple EventBus with Kotlin").show(supportFragmentManager, "LoginDialogFragment")
        }

        background.setOnClickListener {
            Log.d("eventbuskotlin-post", "RequestMeizhiEvent tag = \"DEFAULT_TAG\", Thread = " + Thread.currentThread().name)
            EventBus.post(RequestMeizhiEvent())
        }

        main.setOnClickListener {
            Thread{
                Log.d("eventbuskotlin-post", "MainEvent tag = \"DEFAULT_TAG\", Thread = " + Thread.currentThread().name)
                EventBus.post(MainEvent())
            }.start()
        }

    }

    override fun onStart() {
        super.onStart()
        // 注册 EventBus
        EventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        // 注销 EventBus
        EventBus.unRegister(this)
    }

    /** 符合 LoginEvent 且 tag = Default_TAG 时执行此方法，执行线程为 POST 线程*/
    @Subscriber
    private fun activityLogin(event: LoginEvent) {
        if (event.login) {
            login.text = "已登录"
            button_layout.visibility = View.GONE
            toolbar.inflateMenu(R.menu.toolbar)
            toolbar.setOnMenuItemClickListener {
                Log.d("eventbuskotlin-post", "LoginEvent tag = \"DEFAULT_TAG\", Thread = " + Thread.currentThread().name)
                EventBus.post(LoginEvent(false)); true
            }
        } else {
            login.text = "模拟登录(发送普通 Event)"
            button_layout.visibility = View.VISIBLE
            toolbar.menu.clear()
        }
        Log.d("eventbuskotlin-method-", "LoginEvent method =\"activityLogin\" tag = \"DEFAULT_TAG\", mode = POST , Thread = " + Thread.currentThread().name)
    }

    /** 符合 LoginEvent 且 tag = request load data 时执行此方法，执行线程为 POST 线程*/
    @Subscriber(tag = "request load data")
    private fun acriviryLoadRequest(event: LoginEvent) {
        login.text = "已登录"
        button_layout.visibility = View.GONE
        toolbar.inflateMenu(R.menu.toolbar)
        toolbar.setOnMenuItemClickListener {
            Log.d("eventbuskotlin-post", "LoginEvent tag = \"DEFAULT_TAG\", Thread = " + Thread.currentThread().name)
            EventBus.post(LoginEvent(false)); true
        }
        Log.d("eventbuskotlin-method", "LoginEvent method =\"acriviryLoadRequest\" tag = \"request load data\", mode = POST , Thread = " + Thread.currentThread().name)
    }

}
