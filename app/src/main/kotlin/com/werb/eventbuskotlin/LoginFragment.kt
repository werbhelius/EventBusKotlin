package com.werb.eventbuskotlin

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.werb.eventbus.EventBus


/** Created by wanbo <werbhelius@gmail.com> on 2017/8/30. */

class LoginFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle("EventBus-Kotlin")
                .setMessage("A Simple EventBus with Kotlin")
                .setPositiveButton("login", {_,_ ->
                    EventBus.post(LoginEvent(true))
                })
                .create()
    }

}