package com.cargopull.executor_driver.interactor.auth

import com.cargopull.executor_driver.entity.Validator
import com.cargopull.executor_driver.interactor.DataReceiver
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Юзкейс запроса звонка с кодом.
 */
interface CodeUseCase {

    /**
     * Валидирует номер телефона, и запрашивает на него СМС или звонок с кодом.
     *
     * @return [Single] результат валидации или запроса с таймаутом до следующей попытки.
     */
    fun sendMeCode(): Single<Int>
}

class CodeUseCaseImpl(private val smsGateway: CodeGateway,
                      @Suppress("unused") private val callGateway: CodeGateway,
                      private val phoneNumberReceiver: DataReceiver<String>,
                      private val phoneNumberValidator: Validator<String>) : CodeUseCase {

    override fun sendMeCode(): Single<Int> {
        return phoneNumberReceiver.get()
                .firstOrError()
                .map { phoneNumber ->
                    phoneNumberValidator.validate(phoneNumber)
                    phoneNumber
                }.flatMap { phoneNumber ->
                    smsGateway.sendMeCode(phoneNumber)
                            .observeOn(Schedulers.single())
                            .toSingle { 30 }
                }
    }
}
