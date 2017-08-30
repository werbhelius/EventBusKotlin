package com.werb.eventbus

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 单例 EventBus 注册，
 * 发布事件，取消注册
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/18.
 * */

object EventBus {

    private val executorService: ExecutorService = Executors.newCachedThreadPool()
    private val subscriberMap = ConcurrentHashMap<EventType, CopyOnWriteArrayList<Subscription>>()
    private val methodHunter = SubscriberMethodHunter(subscriberMap)

    /** 注册订阅对象 */
    fun register(subscriber: Any) {
        executorService.execute {
            methodHunter.findSubscribeMethods(subscriber)
        }
    }

    /** 注销订阅对象 */
    fun unRegister(subscriber: Any) {
        executorService.execute {
            methodHunter.removeSubscribeMethods(subscriber)
        }
    }

    fun post(event: IEvent) {
        post(event, DEFAULT_TAG)
    }

    fun post(event: IEvent, tag: String) {
        val eventType = EventType(event.javaClass, tag)
        val list = methodHunter.getMatchEventType(eventType)
        list?.let {
            EventDispatcher.dispatcherEvent(event, it)
        }
    }

}