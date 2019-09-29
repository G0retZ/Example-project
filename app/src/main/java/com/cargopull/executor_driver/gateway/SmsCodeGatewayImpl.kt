package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.interactor.auth.CodeGateway
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class SmsCodeGatewayImpl(private val api: ApiService) : CodeGateway {

    override fun sendMeCode(phoneNumber: String): Completable {
        return api.sendMeCode(phoneNumber)
                .subscribeOn(Schedulers.io())
    }
}
