package com.changui.mimochallengeandroid.data.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import io.reactivex.functions.BiFunction


class ReceiverLiveData<T>(private val context: Context,
                          private val filter: IntentFilter,
                          private val mapFunc: BiFunction<Context, Intent, T>
) : MutableLiveData<T>() {

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(mBroadcastReceiver)
    }

    override fun onActive() {
        super.onActive()
        value = mapFunc.apply(context, Intent()) // some (like Functional Interfaces and java.util.function) are still restricted to APIs 24+.
        context.registerReceiver(mBroadcastReceiver, filter)
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //value = context?.let { intent?.let { it1 -> mapFunc.apply(it, it1) } }
            value = mapFunc.apply(context, intent)
        }
    }

}