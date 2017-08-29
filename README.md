# EventBusKotlin

用 Kotlin 简单实现了一个 EventBus

#### 注册和取消注册
```kotlin
EventBus.register(this)
EventBus.unRegister(this)
```
#### 发送事件
```kotlin
EventBus.post(XXXEvent())
```
#### 利用注解实现事件订阅执行方法

* tag 用于细化区分事件
* priority 同一类事件执行优先级
* mode 方法执行线程

```kotlin
@Subscriber(tag = DEFAULT_TAG, priority = 200, mode = ThreadMode.MAIN)
    private fun change2(event: ToastEvent){
        textView2.text = "今天星期五"
        textView2.setTextColor(resources.getColor(R.color.colorAccent))

        println("myActivity - 200")
    }
```
