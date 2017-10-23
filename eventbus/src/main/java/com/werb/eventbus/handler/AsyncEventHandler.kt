package com.werb.eventbus.handler

import com.werb.eventbus.IEvent
import com.werb.eventbus.Subscription
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Executors

/**
 * 异步线程处理事件
 * HandlerThread 和普通 Thread 的不同之处在于，普通 Thread 在 run 方法中执行一个耗时任务
 * HandlerThread 在内部创建了消息队列，外界需要听过 Handler 的消息方式来通知 HandlerThread 执行一个具体的任务
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/22.
 */

internal class AsyncEventHandler: EventHandler {

    private val bgExecutor = Executors.newCachedThreadPool()


    override fun handleEvent(subscription: Subscription, event: IEvent) {
        bgExecutor.execute {
            try {
                subscription.targetMethod.invoke(subscription.subscriber.get(), event)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }
    }
}