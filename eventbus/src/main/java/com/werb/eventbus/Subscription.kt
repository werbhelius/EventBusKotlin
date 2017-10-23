package com.werb.eventbus

import java.lang.ref.WeakReference
import java.lang.reflect.Method

/**
 * 事件订阅者，包含执行方法，执行类型
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/18.
 * */

internal class Subscription(val subscriber: WeakReference<Any>,
                            val targetMethod: Method,
                            val threadMode: ThreadMode) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || (other::class !== this::class)) {
            return false
        }

        val subscription = other as Subscription
        val judgeSubscriber = this.subscriber.get() === subscription.subscriber.get()
        val judgeMethod = this.targetMethod.name == subscription.targetMethod.name
        return judgeSubscriber && judgeMethod
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = hash * 31 + subscriber.hashCode()
        hash = hash * 31 + targetMethod.hashCode()
        hash = hash * 31 + threadMode.hashCode()
        return hash
    }

}