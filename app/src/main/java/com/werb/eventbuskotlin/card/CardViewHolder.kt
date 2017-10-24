package com.werb.eventbuskotlin.card

import android.content.Intent
import android.view.View
import com.werb.eventbuskotlin.CardDetailActivity
import com.werb.library.MoreViewHolder
import kotlinx.android.synthetic.main.item_view_card.*

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/31. */

class CardViewHolder(containerView: View) : MoreViewHolder<Card>(containerView) {

    override fun bindData(data: Card, payloads: List<Any>) {
        name.text = "EventBus-Kotlin-" + layoutPosition.toString()


        containerView.setOnClickListener {
            val intent = Intent()
            intent.setClass(containerView.context, CardDetailActivity::class.java)
            intent.putExtra("position", layoutPosition)
            containerView.context.startActivity(intent)
        }

    }


}