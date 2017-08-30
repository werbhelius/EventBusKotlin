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

    private val postHandler = PostEventHandler()
    private val mainHandler = MainEventHandler()
    private val asyncHandler = AsyncEventHandler()

    fun dispatcherEvent(list: CopyOnWriteArrayList<Subscription>) {
        list.forEach {
            val eventHandler = getEventHandler(it.threadMode)
            eventHandler.handleEvent(it)
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