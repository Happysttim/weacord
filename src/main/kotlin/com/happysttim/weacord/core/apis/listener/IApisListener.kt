package com.happysttim.weacord.core.apis.listener

interface IApisListener<out T> {
    fun onTask(message: @UnsafeVariance T?)
}