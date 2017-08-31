package com.werb.eventbuskotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.werb.eventbus.EventBus
import kotlinx.android.synthetic.main.activity_card_detail.*

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/31. */

class CardDetailActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        val _position = intent.getIntExtra("position", -1)
        toolbar.title = "当前为第 $_position 个 Card"

        delete_card.setOnClickListener {
            EventBus.post(CardDeleteEvent().apply { position = _position }, "delete")
            finish()
        }

        back_card.setOnClickListener {
            EventBus.post(CardDeleteEvent(), "not delete")
            finish()
        }
    }

}