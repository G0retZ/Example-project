package com.cargopull.executor_driver.application

import com.cargopull.executor_driver.R
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer
import com.cargopull.executor_driver.backend.web.ApiService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.Observer
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

class FcmService : FirebaseMessagingService() {

    lateinit var fcmObserver: Observer<Map<String, String>>
    lateinit var apiService: ApiService
    private var disposable = Disposables.empty()
    lateinit var ringTonePlayer: RingTonePlayer
    lateinit var shakeItPlayer: ShakeItPlayer

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        disposable = apiService.sendFcmInstanceID(s)
                .subscribeOn(Schedulers.io())
                .subscribe({ }, { it.printStackTrace() })
    }

    override fun onCreate() {
        super.onCreate()
        (application as MainApplication).appComponent.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        ringTonePlayer.playRingTone(R.raw.general_notify)
        shakeItPlayer.shakeIt(R.raw.general_notify_vibro)
        remoteMessage?.let {
            val dataMap = it.data
            it.notification?.let { not ->
                not.title?.let { title -> dataMap["title"] = title }
                not.body?.let { body -> dataMap["body"] = body }
            }
            fcmObserver.onNext(dataMap)
        }
    }
}
