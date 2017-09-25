package com.werb.eventbus

import com.werb.eventbus.handler.AsyncEventHandler
import com.werb.eventbus.handler.EventHandler
import com.werb.eventbus.handler.MainEventHandler
import com.werb.eventbus.handler.PostEventHandler
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 事件分发器，用于处理事件
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/28.
 */

internal object EventDispatcher {

    /** POST 线程即事件发出的线程 */
    private val postHandler = PostEventHandler()
    /** MAIN 线程 UI 线程 */
    private val mainHandler = MainEventHandler()
    /** BACKGROUND 线程，ExecutorService 线程池 */
    private val asyncHandler = AsyncEventHandler()

    fun dispatcherEvent(event: IEvent, list: CopyOnWriteArrayList<Subscription>) {
        list.forEach {
            val weekSubscriber = it.subscriber.get()
            val _this = it
            weekSubscriber?.let {
                val eventHandler = getEventHandler(_this.threadMode)
                eventHandler.handleEvent(_this, event)
            }
        }
    }

    private fun getEventHandler(mode: ThreadMode): EventHandler {
        return when (mode) {
            ThreadMode.POST -> postHandler
            ThreadMode.BACKGROUND -> asyncHandler
            ThreadMode.MAIN -> mainHandler
        }
    }

}