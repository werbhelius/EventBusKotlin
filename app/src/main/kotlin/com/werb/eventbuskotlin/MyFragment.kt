package com.werb.eventbuskotlin

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.werb.eventbus.EventBus
import com.werb.eventbus.Subscriber
import com.werb.eventbus.ThreadMode
import com.werb.eventbuskotlin.card.Card
import com.werb.eventbuskotlin.card.CardViewType
import com.werb.eventbuskotlin.meizhi.MeizhiViewType
import com.werb.library.MoreAdapter
import kotlinx.android.synthetic.main.my_fragment.*
import java.net.URL

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/29. */

class MyFragment : Fragment() {

    private val adapter = MoreAdapter()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.my_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        // 注册 EventBus
        EventBus.register(this)

        val text = "MyFragment 中的列表使用 <a href=\"https://github.com/Werb/MoreType\">MoreType</a> 构建 <br> 这是我尝试用 Kotlin 写的一个用于构建" +
                " RecyclerView 列表的第三方库 "
        info.text = Html.fromHtml(text)
        info.movementMethod = LinkMovementMethod.getInstance()
        adapter.register(CardViewType())
                .register(MeizhiViewType())
                .attachTo(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 注销 EventBus
        EventBus.unRegister(this)
    }

    private fun buildData() {
        val list = mutableListOf<Card>()
        for (i in 1..30) {
            list.add(Card())
        }
        adapter.loadData(list)
    }

    /** 符合 LoginEvent 且 tag = DEFAULT_TAG 时执行此方法，执行线程为 POST 线程*/
    @Subscriber
    private fun login(event: LoginEvent) {
        if (event.login) {
            info.visibility = View.GONE
            list.layoutManager = LinearLayoutManager(context)
            buildData()
        } else {
            adapter.removeAllData()
            info.visibility = View.VISIBLE
        }
    }

    /** 符合 CardDeleteEvent 且 tag = delete 时执行此方法，执行线程为 POST 线程*/
    @Subscriber(tag = "delete")
    private fun delete(event: CardDeleteEvent) {
        val position = event.position
        if (position != -1) {
            adapter.removeData(position)
            Snackbar.make(fragment_layout, "Card $position delete by tag \" delete \"", 3000)
                    .setAction("ok", {})
                    .show()
        }
    }

    /** 符合 CardDeleteEvent 且 tag = not delete 时执行此方法，执行线程为 POST 线程*/
    @Subscriber(tag = "not delete")
    private fun back(event: CardDeleteEvent) {
        Snackbar.make(fragment_layout, "Card not delete by tag \" not delete \"", 3000)
                .setAction("ok", {})
                .show()
    }

    /** 符合 RequestMeizhiEvent 且 tag = not delete 时执行此方法，执行线程为 BACKGROUND 线程*/
    @Subscriber(mode = ThreadMode.BACKGROUND)
    private fun request(event: RequestMeizhiEvent){
        val data = URL("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1").readText()
        LoginDialogFragment.newInstance("request", data).show(fragmentManager, "LoginDialogFragment")
    }

    /** 符合 LoginEvent 且 tag = request load data 时执行此方法，执行线程为 POST 线程*/
    @Subscriber(tag = "request load data")
    private fun loadRequest(event: LoginEvent){
        list.layoutManager = GridLayoutManager(context, 2)
        event.meizhis?.let {
            info.visibility = View.GONE
            adapter.loadData(it.results)
        }
    }

    /** 符合 MainEvent 且 tag = DEFAULT_TAG 时执行此方法，执行线程为 MAIN UI 线程，此 Event 由子线程发出，操作 UI 需要在 MAIN 线程中*/
    @Subscriber(mode = ThreadMode.MAIN)
    private fun main(event: MainEvent) {
        Toast.makeText(context, "UI 操作必须在 main 线程中操作", Toast.LENGTH_LONG).show()
        info.setTextColor(resources.getColor(R.color.colorAccent))
    }

}