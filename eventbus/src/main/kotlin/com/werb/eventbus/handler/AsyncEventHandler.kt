package com.werb.eventbus.handler

import android.os.Handler
import android.os.HandlerThread
import com.werb.eventbus.IEvent
import com.werb.eventbus.Subscription

/**
 * 异步线程处理事件
 * HandlerThread 和普通 Thread 的不同之处在于，普通 Thread 在 run 方法中执行一个耗时任务
 * HandlerThread 在内部创建了消息队列，外界需要听过 Handler 的消息方式来通知 HandlerThread 执行一个具体的任务
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/22.
 */

internal class AsyncEventHandler: EventHandler {

    private var dispatcherThread: DispatcherThread
    private val postEventHandler = PostEventHandler()

    init {
        dispatcherThread = DispatcherThread("AsyncEventHandler")
        dispatcherThread.start()
    }

    override fun handleEvent(subscription: Subscription) {
        dispatcherThread.post(Runnable {
            postEventHandler.handleEvent(subscription)
        })
    }

    private inner class DispatcherThread(threadName: String): HandlerThread(threadName) {

        private var asyncHandler: Handler? = null

        @Synchronized override fun start() {
            super.start()
            asyncHandler = Handler(this.looper)
        }

        fun post(runnable: Runnable){
            asyncHandler?.post(runnable) ?: throw NullPointerException(" asyncHandler == null, must call start() first")
        }

    }
}