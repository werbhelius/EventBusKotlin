# 如何自己实现一个 EventBus

## 什么是 EventBus
>**EventBus** 是一个基于**观察者模式**的事件发布／订阅框架，开发者可以通过极少的代码去实现组件，模块之间的通信，而不需要以层层传递接口的形式去单独构建通信桥梁。从而降低因多重回调导致的模块间强耦合，同时避免产生大量内部类。它拥有使用方便，性能高，接入成本低和支持多线程的优点，实乃模块解耦、代码重构必备良药。

Android 中 提供了 Handler 来进行组件间的通信，而 Handler 在使用上有很多不便，EventBus 的出现完美的解决了这些问题。

用了那么久 EventBus 所以我决定自己实现一个，正好最近项目开始使用 Kotlin 来写，所以本文中的代码和例子全部使用 Kotlin 来完成。

## EventBus 的原理
前面说了 EventBus 是基于**观察者模式**，核心是**事件**。通过事件的发布和订阅实现组件之间的通信，EventBus 默认是一个单例存在，在 Java 中还需要使用 Synchronized 来保证线程安全。通俗来讲，EventBus 通过注册将所有订阅事件的方法储存在集合中，当有事件发布的时候，根据某些规则，匹配出符合条件的方法，调用执行，从而实现组件间的通信。

发布的事件相当于被观察者，注册的对象相当于观察者，被观察者和观察者是一对多的关系。当被观察者状态发生变化，即发布事件的时候，观察者对象将会得到通知并作出响应，即执行对应的方法。

## EventBus 具体实现

EventBus 最终的目的，是在当有`事件`发生的时候，调用执行对应的方法，这里我们采用注解的方式来标记执行的方法，最终通过反射来调用。

### Subscriber

`Subscriber` 是一个注解类，我们通过 `@Subscriber`关键字来标记方法。`Subscriber` 在声明的时候，有两个可选参数，`tag: String` 和 `mode: ThreadMode`，并且这两个参数都有自己的默认值。

```kotlin
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION,AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Subscriber(val tag: String = DEFAULT_TAG, val mode: ThreadMode = ThreadMode.POST)
```

* @Retention(AnnotationRetention.RUNTIME) 声明在运行时使用注解
* AnnotationTarget.FUNCTION 声明注解时作用在方法上
* AnnotationTarget.PROPERTY_GETTER 声明属性存在 GET
* AnnotationTarget.PROPERTY_SETTER 声明属性存在 SET

#### tag
`tag` 的目的是为了当有相同事件发生的时候，细化区分不同的事件。

例如我们定义了一个 `SessionEvent` 用于账户登录和登出时的信息传递，这时候我们就可以定义 `login` 和 `logout` 两个 `tag` 来进行区分。当然你也完全可以定义两个 `LoginEvent` 和 `LogoutEvent`，这个字段的目的是为了可以更加方便灵活的操作代码。

`tag` 的默认值为 `DEFAULT_TAG`。

#### mode
`mode` 的目的是为了声明注解所标记的方法的执行环境。

`mode` 所代表的是 `ThreadMode` 类。这是一个枚举类，可选值为`MAIN`，`POST`， `BACKGROUND`，即响应 Event 事件时，方法执行所在的线程。

* MAIN 主线程 UI 线程
* POST 事件发出所在的线程
* BACKGROUND 子线程

`mode` 的默认值为 `POST` 。

#### IEvent

在这里，我定义了一个空的接口 `IEvent` ，我们通过 `EventBus` 发出的事件类需要实现这个借口，同时在通过注解定义事件执行方法的时候，需要讲我们接收的某个事件类作为方法的参数，有且只有一个参数，它就是我们的被观察者。

**一个完整的例子：**
```kotlin
/** EventBus 发送的事件类 这个类的目的用于用户登录登出的相关操作 */
class SessionEvent: IEvent
```

```kotlin
/** 登录成功时发送 Event */
EventBus.post(SessionEvent(), "login")
/** 登出成功时发送 Event */
EventBus.post(SessionEvent(), "logout")
```

```kotlin
/** 登录成功 默认在 POST 线程执行 */
@Subscriber(tag = "login")
private fun login(event: SessionEvent) {
    // do something
}

/** 登录成功 在 MAIN 线程执行，通常是一些 UI 上的操作 */
@Subscriber(tag = "logout", mode = ThreadMode.MAIN)
private fun logout(event: SessionEvent) {
    // do something
}
```
***

前面说了，我们需要将所有订阅事件的方法存储到一个集合中，当有`事件`发出的时候，我们通过某些规则，匹配出符合条件的方法，调用执行。所以，首先我们需要去定采用哪种集合来存储，存储时的规则是什么。

### MutableMap

这里我们采用 `MutableMap` 来存储事件执行的方法。`MutableMap` 在 Kotlin 中表示为**可变**的，结构如下。

`MutableMap<EventType, CopyOnWriteArrayList<Subscription>>`

`EventType` 是唯一key（下面会介绍），通过key找到对应的执行方法。
`CopyOnWriteArrayList<Subscription>>` 是一个线程安全的 List ，前面我们说过了被观察者和观察者是一对多的关系所以这里使用 List，`Subscription`事件执行方法的包装类（下面会介绍)。

#### EventType
`EventType` 类包含两个字段，`eventClass: Class<IEvent>` 和 `tag: String`。

* `eventClass: Class<IEvent>` 是我们通过 `EventBus` 发出的事件类

* `tag: String` 是我们发出事件类时所指定的 `tag`

```kotlin
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
```

通过这两个字段我们就可以确定一个唯一的key，准确的找到所对应的事件执行方法。

#### Subscription

`Subscription` 是事件执行方法的包装类，它包含 `subscriber: WeakReference<Any>`，`targetMethod: Method`，`threadMode: ThreadMode` 这三个字段。

* `subscriber: WeakReference<Any>` 这个就是我们的观察者，它可以是一个 `Activity` 或 `Fragment` 或 `Service`，我们注册了这个观察者到我们的 `EventBus` 中，当被观察者产生变化的时候，观察者调用执行对应的方法，并且这里使用弱引用来包装这个对象。
* `targetMethod: Method` 事件调用执行的具体方法。
* `threadMode: ThreadMode` 事件方法执行时所在的环境。

```kotlin
internal class Subscription(val subscriber: WeakReference<Any>,
                            val targetMethod: Method,
                            val threadMode: ThreadMode) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || (other::class !== this::class)) {
            return false
        }

        val subscription = other as Subscription
        val judgeSubscriber = this.subscriber.get() === subscription.subscriber.get()
        val judgeMethod = this.targetMethod.name == subscription.targetMethod.name
        return judgeSubscriber && judgeMethod
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = hash * 31 + subscriber.hashCode()
        hash = hash * 31 + targetMethod.hashCode()
        hash = hash * 31 + threadMode.hashCode()
        return hash
    }

}
```
***

好了，现在我们确定了 `EventBus` 的内部存储结构，接下来我们就要说一下最关键的部分，`EventBus` 是如何注册 `register` 和发送 `post` 事件的，先看一下 `EventBus` 的代码。

```kotlin
/** object 表示单例 */
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
```

* `executorService: ExecutorService` 是一个线程池，我们在注册和注销的时候，采用线程池，因为注册和注销是很频繁数量大的一个操作，同时单个任务处理的时间比较短，使用线程池可以很好的提高效率。
* `subscriberMap = ConcurrentHashMap<EventType, CopyOnWriteArrayList<Subscription>>()` 这个就是我们存储被观察者与观察者的集合。
* `methodHunter = SubscriberMethodHunter(subscriberMap)` 这个就是我们注册的核心类，下面会详细介绍。

### 注册 Register
```kotlin
/** 注册订阅对象 */
fun register(subscriber: Any) {
    executorService.execute {
        methodHunter.findSubscribeMethods(subscriber)
    }
}
```

* `subscriber: Any` 是我们的观察者对象，它可以是一个 `Activity` 或 `Fragment` 或 `Service`。

举个例子来说注册的流程，当我们注册一个 `Activity` 到 `EventBus` 中时，我们通过 `methodHunter.findSubscribeMethods(subscriber)` 方法，查找出当前 `Activity` 中被 `@Subscriber`关键字来标记方法，判断的规则如下：

1. 被 `@Subscriber` 关键字标记
2. 订阅函数只支持一个参数
3. 订阅函数的参数是否实现了 `IEvent` 接口

当确定是我们要找的方法之后，根据方法的参数 `IEvent` 和 `tag`，生成一个 `EventType` 实例作为当前方法的唯一key，根据观察者对象 `subscriber: Any` 和找到的事件执行方法 `method` 以及事件执行环境 `mode`，生成一个 `Subscription`。

当然我们现在还不能直接把它存储到我们的集合中，回顾一下我们的集合结构，`MutableMap<EventType, CopyOnWriteArrayList<Subscription>>`，因为要先判断集合中是否已存在一样的 key，当存在则把当前 `Subscription` 添加到 `CopyOnWriteArrayList<Subscription>>` 中，这也是我们重写 `EventType` 的 `equals()` 和 `hashCode()` 的原因，具体代码如下。

```kotlin
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

/** 判断是否有已存在的 EventType */
internal fun getMatchEventType(type: EventType): CopyOnWriteArrayList<Subscription>? {
    val keys = subscriberMap.keys
    return keys.firstOrNull { it == type }?.let { subscriberMap[it] }
}
```

### 发送事件 POST
到这一步，我们已经把观察者对象注册到了 `EventBus` 中，剩下的就是在需要的时候发送事件就可以了，所以接下来，我们来看一下如何发送事件和执行事件订阅的方法。

```kotlin
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
```

`post` 有两个方法，一个是使用默认的tag，一个是自己指定tag，这个方法很简单，我们根据发送事件时指定的 `IEvent` 和 `tag`，确定出 `EventType`，也就是我们集合中唯一的 key，通过 key 得到 value，value 就是 `CopyOnWriteArrayList<Subscription>`，也就是当前事件执行方法的集合，剩下的就是执行调用这些方法。

### 事件分发 EventDispatcher

前面说了，我们在实现事件执行方法的时候，会指定事件执行时的环境，`MAIN`，`POST`， `BACKGROUND`，默认环境是 `POST` 线程，这样可以减少线程切换时带来的开销。

```kotlin
internal object EventDispatcher {

    /** POST 线程即事件发出的线程 */
    private val postHandler = PostEventHandler()
    /** MAIN 线程 UI 线程 */
    private val mainHandler = MainEventHandler()
    /** BACKGROUND 线程，ExecutorService 线程池 */
    private val asyncHandler = AsyncEventHandler()

    fun dispatcherEvent(event: IEvent, list: CopyOnWriteArrayList<Subscription>) {
        list.forEach {
            val eventHandler = getEventHandler(it.threadMode)
            eventHandler.handleEvent(it, event)
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
```

正如上面的代码，我们依据 `threadMode` ，选择符合的 `EventHandler`，调用其 `handleEvent(subscription: Subscription, event: IEvent)` 去执行方法。

#### 事件执行 EventHandler 
```kotlin
internal interface EventHandler {
    fun handleEvent(subscription: Subscription, event: IEvent)
}
```

`EventHandler` 是一个接口，定义的事件执行时调用的方法 `handleEvent()`，我们需要依次实现三种环境的具体实现。

* PostEventHandler 

```kotlin
internal class PostEventHandler: EventHandler {

    override fun handleEvent(subscription: Subscription, event: IEvent) {
        try {
            subscription.targetMethod.invoke(subscription.subscriber.get(), event)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }
}
```

* MainEventHandler UI 线程
```kotlin
internal class MainEventHandler: EventHandler {

    private val handler = Handler(Looper.getMainLooper())

    override fun handleEvent(subscription: Subscription, event: IEvent) {
        handler.post({
            try {
                subscription.targetMethod.invoke(subscription.subscriber.get(), event)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        })
    }

}
```

* AsyncEventHandler  后台线程采用线程池实现
```kotlin
internal class AsyncEventHandler: EventHandler {

    private val bgExecutor = Executors.newCachedThreadPool()

    override fun handleEvent(subscription: Subscription, event: IEvent) {
        bgExecutor.execute {
            try {
                subscription.targetMethod.invoke(subscription.subscriber.get(), event)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }
    }
}
```
三种环境中，核心的执行方法都是一样的，采用反射来调用具体事件的执行方法，仅仅是方法所在的执行环境不同而已。

**到目前为止，我们的 EventBus 就完成了，通过注解的形式实现事件的执行方法，通过注册观察者对象，生成 key 和 value 建立关系存储到集合中，在事件发出的时候，查找出对应的事件方法集合，然后在指定的执行环境中调用。**
