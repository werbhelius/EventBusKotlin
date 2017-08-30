package com.werb.eventbus

/**
 * 事件接受方法的注解类
 * [mode] 事件执行线程 默认在发出实现的 POST 线程执行
 * [tag] 事件标记 tag 用于事件区分
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/16.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Subscriber(val tag: String = DEFAULT_TAG, val mode: ThreadMode = ThreadMode.POST)