package com.werb.eventbuskotlin.meizhi

import android.view.View
import com.bumptech.glide.Glide
import com.werb.library.MoreViewHolder
import kotlinx.android.synthetic.main.item_view_meizhi.*

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/31. */

class MeizhiViewHolder(containerView: View) : MoreViewHolder<Result>(containerView) {

    override fun bindData(data: Result, payloads: List<Any>) {
        Glide.with(containerView.context).load(data.url).into(image)
    }


}