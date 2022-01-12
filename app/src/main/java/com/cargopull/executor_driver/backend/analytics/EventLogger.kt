package com.cargopull.executor_driver.backend.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.cargopull.executor_driver.BuildConfig
import com.cargopull.executor_driver.backend.settings.AppSettingsService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Отправитель отчетов о событиях
 */
interface EventLogger {

    /**
     * Отправить отчет о событии.
     *
     * @param event - имя события
     * @param params - параметры события
     */
    fun reportEvent(event: String, params: MutableMap<String, String>)
}

class EventLoggerImpl(private val appSettings: AppSettingsService, context: Context) : EventLogger {
    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun reportEvent(event: String, params: MutableMap<String, String>) {
        appSettings.getData("authorizationLogin")
            ?.let { params[FirebaseAnalytics.Param.CHARACTER] = it }
            ?: FirebaseCrashlytics.getInstance()
                .recordException(RuntimeException("Не удалось выснить номер телефона водителя"))
        val bundle = Bundle()
        params.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        if (BuildConfig.DEBUG) {
            Log.d(javaClass.simpleName, "Sending log: $event; $bundle")
        }
        mFirebaseAnalytics.logEvent(event, bundle)
    }
}
