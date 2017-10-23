# EventBusKotlin

用 Kotlin 简单实现了一个 EventBus

[如何自己实现一个 EventBus](https://github.com/Werb/EventBusKotlin/wiki/%E5%A6%82%E4%BD%95%E8%87%AA%E5%B7%B1%E5%AE%9E%E7%8E%B0%E4%B8%80%E4%B8%AA-EventBus)

[![Download](https://api.bintray.com/packages/werbhelius/maven/eventbuskotlin/images/download.svg) ](https://bintray.com/werbhelius/maven/eventbuskotlin/_latestVersion)

## Dependency
```gradle
compile 'com.werb.eventbuskotlin:eventbuskotlin:0.1.8'
```
or
```gradle
implementation 'com.werb.eventbuskotlin:eventbuskotlin:0.1.8'
```


#### 注册和取消注册
```kotlin
EventBus.register(this)
EventBus.unRegister(this)
```
#### 实现事件 Event 实体类（实现 IEvent 接口）
```kotlin
class XXXXEvent: IEvent
```
#### 发送事件
```kotlin
EventBus.post(XXXEvent())
```
#### 利用注解实现事件订阅执行方法

* tag 用于细化区分事件
* mode 方法执行线程

```kotlin
@Subscriber(tag = DEFAULT_TAG, mode = ThreadMode.MAIN)
    private fun change2(event: XXXEvent){
        textView2.text = "今天星期五"
        textView2.setTextColor(resources.getColor(R.color.colorAccent))

        println("myActivity - 200")
    }
```
