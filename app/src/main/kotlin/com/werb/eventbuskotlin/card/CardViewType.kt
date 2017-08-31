package com.werb.eventbuskotlin.card

import android.content.Intent
import android.support.v7.widget.AppCompatTextView
import com.werb.eventbuskotlin.CardDetailActivity
import com.werb.eventbuskotlin.R
import com.werb.library.MoreViewHolder
import com.werb.library.MoreViewType

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/31. */

class CardViewType: MoreViewType<Card>(R.layout.item_view_card, Card::class) {

    private lateinit var title: AppCompatTextView

    override fun initView(holder: MoreViewHolder) {
        title = holder.findViewOften(R.id.title)
    }

    override fun bindData(data: Card, holder: MoreViewHolder) {

        title.text = "EventBus-Kotlin-" + holder.layoutPosition.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(holder.itemView.context, CardDetailActivity::class.java)
            intent.putExtra("position", holder.layoutPosition)
            holder.itemView.context.startActivity(intent)
        }
    }

}