package com.werb.eventbuskotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.werb.eventbus.DEFAULT_TAG
import com.werb.eventbus.EventBus
import com.werb.eventbus.Subscriber
import com.werb.eventbus.ThreadMode
import kotlinx.android.synthetic.main.my_fragment.*

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/29. */

class MyFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.my_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        EventBus.register(this)

        textView.text = "今天星期一"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.unRegister(this)
    }

    @Subscriber()
    private fun change(event: ToastEvent){
        textView.text = "今天星期天"
        textView.setTextColor(resources.getColor(R.color.colorPrimary))

        println("myFragment - 100-" + Thread.currentThread().name)
    }

    @Subscriber(mode = ThreadMode.MAIN)
    private fun change2(event: ToastEvent){
        textView2.text = "今天星期五"
        textView2.setTextColor(resources.getColor(R.color.colorAccent))
        println("myFragment - 200-" + Thread.currentThread().name)
    }

}