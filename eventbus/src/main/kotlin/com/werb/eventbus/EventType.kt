package com.werb.eventbus

/**
 * 确定事件执行函数的唯一性，通过事件类型 IEvent 和 tag 来保证唯一性
 * Created by wanbo <werbhelius@gmail.com> on 2017/8/19.
 * */

internal class EventType(private var eventClass: Class<IEvent>, private var tag: String) {

    override fun equals(other: Any?): Boolean {
        // 比较内存引用地址，相同则返回 true
        if (this === other) {
            return true
        }

        // 判断是否为空，是否属于同一中类型
        if (other == null || (other.javaClass.name !== this.javaClass.name)) {
            return false
        }

        // 能执行到这里，说明 obj 和 this 同类且非 null
        val eventType = other as EventType
        val tagJudge = tag == eventType.tag
        val eventJudge = eventClass.name == eventType.eventClass.name

        return tagJudge && eventJudge
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = hash * 31 + eventClass.hashCode()
        hash = hash * 31 + tag.hashCode()
        return hash
    }
}