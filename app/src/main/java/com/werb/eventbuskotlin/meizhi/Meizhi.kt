package com.werb.eventbuskotlin.meizhi

/** Created by wanbo <werbhelius@gmail.com> on 2017/8/31. */

data class Meizhi(val error: Boolean, val results: ArrayList<Result>)

data class Result(val url: String)