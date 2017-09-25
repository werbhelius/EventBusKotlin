package com.werb.eventbus

import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 订阅对象中事件执行方法(@Subscriber)的操作
 * 查找方法，移除方法
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/21.
 */

internal class SubscriberMethodHunter(private val subscriberMap: MutableMap<EventType, CopyOnWriteArrayList<Subscription>>) {

    /** 查找对象中有注解标记的执行方法 */
    @Synchronized fun findSubscribeMethods(subscriber: Any) {
        var clazz: Class<*>? = subscriber.javaClass
        while (clazz != null && !isSystemClass(clazz.name)) {
            val allMethods = clazz.declaredMethods
            for (method in allMethods) {
                val annotation = method.getAnnotation(Subscriber::class.java)
                if (annotation != null) {
                    // 获取方法参数
                    val paramsTypeClass = method.parameterTypes
                    // 订阅函数只支持一个参数
                    if (paramsTypeClass != null && paramsTypeClass.size == 1) {
                        val paramsEvent = paramsTypeClass[0]
                        if (isImplementIEvent(paramsEvent)) {
                            method.isAccessible = true
                            @Suppress("UNCHECKED_CAST")
                            val eventType = EventType(paramsEvent as Class<IEvent>, annotation.tag)
                            val subscription = Subscription(WeakReference(subscriber), method, annotation.mode)
                            subscribe(eventType, subscription)
                        }
                    }
                }
            }
            clazz = clazz.superclass
        }
    }

    /** 根据 EventType 来确定一对多的订阅关系 */
    @Synchronized private fun subscribe(type: EventType, subscription: Subscription) {
        var subscriptionLists: CopyOnWriteArrayList<Subscription>? = getMatchEventType(type)
        if (subscriptionLists == null) {
            subscriptionLists = CopyOnWriteArrayList()
        }

        if (subscriptionLists.contains(subscription)) {
            return
        }

        subscriptionLists.add(subscription)
        // 将事件类型key和订阅者信息存储到map中
        subscriberMap.put(type, subscriptionLists)
    }

    /** 移除全部订阅事件 */
    @Synchronized fun removeSubscribeMethods(subscriber: Any) {
        val iterator = subscriberMap.values.iterator()
        while (iterator.hasNext()) {
            val subscriptions: MutableList<Subscription>? = iterator.next()
            subscriptions?.let {
                val subIterator = subscriptions.iterator()
                while (subIterator.hasNext()) {
                    val subscription = subIterator.next()
                    // 获取引用
                    val cacheObject = subscription.subscriber.get()
                    cacheObject?.let {
                        if (isSameObject(cacheObject, subscriber)) {
                            Log.d("", "### 移除订阅 " + subscriber.javaClass.name)
                            subscriptions.remove(subscription)
                        }
                    }
                }
            }

            // 如果针对某个Event的订阅者数量为空了,那么需要从map中清除
            if (subscriptions == null || subscriptions.isEmpty()) {
                iterator.remove()
            }
        }
    }

    /** 判断是否有已存在的 EventType */
    internal fun getMatchEventType(type: EventType): CopyOnWriteArrayList<Subscription>? {
        val keys = subscriberMap.keys
        return keys.firstOrNull { it == type }?.let { subscriberMap[it] }
    }

    /** 判断是否是同一个对象 */
    private fun isSameObject(subOne: Any, subTwo: Any): Boolean = subOne == subTwo && subOne === subTwo

    /** 判断是否是系统类 */
    private fun isSystemClass(name: String): Boolean {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.") || name.startsWith("kotlin.")
    }

    /** 判断参数是否集成 IEvent */
    private fun isImplementIEvent(clazz: Class<*>): Boolean = IEvent::class.java.isAssignableFrom(clazz)
}