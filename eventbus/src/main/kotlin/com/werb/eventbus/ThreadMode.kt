package com.werb.eventbus

/**
 * 事件处理所在的线程
 * [MAIN] 主线程 UI 线程
 * [POST] 事件发出所在的线程
 * [BACKGROUND] 子线程
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/16.
 */
enum class ThreadMode { MAIN, POST, BACKGROUND }