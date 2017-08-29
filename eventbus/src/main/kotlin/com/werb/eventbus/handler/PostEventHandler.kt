package com.werb.eventbus.handler

import com.werb.eventbus.Subscription
import java.lang.reflect.InvocationTargetException


/**
 * 回调 Post 线程处理事件
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/22.
 * */

internal class PostEventHandler: EventHandler {

    override fun handleEvent(subscription: Subscription) {
        try {
            subscription.targetMethod.invoke(subscription.subscriber.get(), subscription.eventType.event)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}