package com.werb.eventbuskotlin

import com.werb.eventbus.IEvent
import com.werb.eventbuskotlin.meizhi.Meizhi

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/30. */

class LoginEvent(val login:Boolean): IEvent {
    var meizhis: Meizhi? = null
}