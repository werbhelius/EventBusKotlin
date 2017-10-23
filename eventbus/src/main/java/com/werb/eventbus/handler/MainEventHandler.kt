package com.werb.eventbus.handler

import android.os.Handler
import com.werb.eventbus.IEvent
import com.werb.eventbus.Subscription
import android.os.Looper
import java.lang.reflect.InvocationTargetException

/**
 * 回调 UI 线程处理事件
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/22.
 * */

internal class MainEventHandler: EventHandler {

    private val handler = Handler(Looper.getMainLooper())

    override fun handleEvent(subscription: Subscription, event: IEvent) {
        handler.post({
            try {
                subscription.targetMethod.invoke(subscription.subscriber.get(), event)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        })
    }

}