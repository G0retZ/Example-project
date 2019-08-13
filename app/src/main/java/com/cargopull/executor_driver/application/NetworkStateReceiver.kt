package com.cargopull.executor_driver.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.cargopull.executor_driver.backend.analytics.ErrorReporter
import com.cargopull.executor_driver.interactor.CommonGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

class NetworkStateReceiver(private val connectivityManager: ConnectivityManager?,
                           private val errorReporter: ErrorReporter) : BroadcastReceiver(), CommonGateway<Boolean> {
    private val networkState = BehaviorSubject.create<Boolean>()

    private val isConnectedToInternet: Boolean
        get() {
            return try {
                connectivityManager?.let {
                    it.activeNetworkInfo?.isConnected ?: false
                    // && In etAddress.getByName(AppConfigKt.SERVER_NAME).isReachable(2000);
                } ?: false
            } catch (e: Exception) {
                errorReporter.reportError(e)
                false
            }

        }

    override val data: Flowable<Boolean>
        get() = networkState.toFlowable(BackpressureStrategy.BUFFER)

    override fun onReceive(context: Context, intent: Intent) {
        val connectedToInternet = isConnectedToInternet
        networkState.onNext(connectedToInternet)
        Log.d(NetworkStateReceiver::class.java.name, if (connectedToInternet) "CONNECTED!" else "DISCONNECTED!")
    }
}
