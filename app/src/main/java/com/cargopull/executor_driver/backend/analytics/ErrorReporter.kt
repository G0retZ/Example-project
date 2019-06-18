package com.cargopull.executor_driver.backend.analytics

import android.util.Log
import com.cargopull.executor_driver.BuildConfig
import com.cargopull.executor_driver.backend.web.AuthorizationException
import com.cargopull.executor_driver.backend.web.ConnectionClosedException
import com.cargopull.executor_driver.backend.web.NoNetworkException
import com.cargopull.executor_driver.backend.web.ServerResponseException
import com.cargopull.executor_driver.entity.OrderOfferDecisionException
import com.cargopull.executor_driver.entity.OrderOfferExpiredException
import com.crashlytics.android.Crashlytics

/**
 * Отправитель отчетов об ошибках
 */
interface ErrorReporter {

    /**
     * Отправить отчет об ошибке.
     *
     * @param throwable - ошибка
     */
    fun reportError(throwable: Throwable)
}

class ErrorReporterImpl : ErrorReporter {

    override fun reportError(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.w(javaClass.simpleName, throwable)
        }
        // Игнорируем ошибки доступа и сетевые ошибки
        return when (throwable) {
            is SecurityException -> Unit
            is NoNetworkException -> Unit
            is AuthorizationException -> Unit
            is ServerResponseException -> Unit
            is ConnectionClosedException -> Unit
            is OrderOfferExpiredException -> Unit
            is OrderOfferDecisionException -> Unit
            else -> Crashlytics.logException(throwable)
        }
    }
}
