package com.cargopull.executor_driver.interactor.auth

import com.cargopull.executor_driver.entity.Validator
import com.cargopull.executor_driver.interactor.DataReceiver
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * Юзкейс запроса звонка с кодом.
 */
interface CodeUseCase {

    /**
     * Валидирует номер телефона, и запрашивает на него СМС с кодом.
     *
     * @return [Completable] результат валидации или запроса
     */
    fun sendMeCode(): Completable
}

class CodeUseCaseImpl(private val gateway: CodeGateway,
                      private val phoneNumberReceiver: DataReceiver<String>,
                      private val phoneNumberValidator: Validator<String>) : CodeUseCase {

    override fun sendMeCode(): Completable {
        return phoneNumberReceiver.get()
                .firstOrError()
                .map { phoneNumber ->
                    phoneNumberValidator.validate(phoneNumber)
                    phoneNumber
                }.flatMapCompletable { phoneNumber ->
                    gateway.sendMeCode(phoneNumber)
                            .observeOn(Schedulers.single())
                }
    }
}
