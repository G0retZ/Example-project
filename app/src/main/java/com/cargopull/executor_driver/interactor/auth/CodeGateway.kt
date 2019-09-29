package com.cargopull.executor_driver.interactor.auth

import io.reactivex.Completable

/**
 * Гейтвей запроса звонка с кодом.
 */
interface CodeGateway {

    /**
     * Запрашивает у системы СМС с кодом на номер телефона.
     *
     * @param phoneNumber [String] номер телефона
     * @return [Completable] результат запроса
     */
    fun sendMeCode(phoneNumber: String): Completable
}
