package com.werb.eventbus.handler

import com.werb.eventbus.IEvent
import com.werb.eventbus.Subscription

/**
 * 事件方法执行模式接口
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/22. */

internal interface EventHandler {

    fun handleEvent(subscription: Subscription, event: IEvent)

}