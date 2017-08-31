package com.werb.eventbuskotlin

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.werb.eventbus.EventBus
import com.werb.eventbuskotlin.meizhi.Meizhi


/** Created by wanbo <werbhelius@gmail.com> on 2017/8/30. */

class LoginDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(type: String, message: String): LoginDialogFragment {
            val loginFragment = LoginDialogFragment()
            val args = Bundle()
            args.putString("message",message)
            args.putString("type",type)
            loginFragment.arguments = args
            return loginFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments.getString("message")
        val type = arguments.getString("type")
        return AlertDialog.Builder(context)
                .setTitle("EventBus-Kotlin")
                .setMessage(message)
                .setPositiveButton("login", {_,_ ->
                    if (type == "login") {
                        EventBus.post(LoginEvent(true))
                    }else if (type == "request"){
                        val _meizhi = Gson().fromJson<Meizhi>(message, Meizhi::class.java)
                        EventBus.post(LoginEvent(true).apply {
                            meizhis = _meizhi
                        }, "request load data")
                    }
                })
                .create()
    }

}