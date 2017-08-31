package com.werb.eventbuskotlin.meizhi

import android.support.v7.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.werb.eventbuskotlin.R
import com.werb.library.MoreViewHolder
import com.werb.library.MoreViewType

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/31. */

class MeizhiViewType: MoreViewType<Result>(R.layout.item_view_meizhi, Result::class) {

    private lateinit var imageView: AppCompatImageView

    override fun initView(holder: MoreViewHolder) {
        imageView = holder.findViewOften(R.id.image)
    }

    override fun bindData(data: Result, holder: MoreViewHolder) {
        Glide.with(holder.itemView.context).load(data.url).into(imageView)
    }


}