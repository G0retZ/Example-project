package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PreOrderBookingGateway
constructor(private val apiService: ApiService) : OrderConfirmationGateway<String> {

    override fun sendDecision(order: Order, accepted: Boolean): Single<Pair<ExecutorState?, String?>> {
        return apiService.sendPreOrderDecision(ApiOrderDecision(order.id, accepted))
                .subscribeOn(Schedulers.io())
                .map { apiSimpleResult ->
                    if ("200" == apiSimpleResult.code) {
                        Pair(null, apiSimpleResult.message)
                    } else {
                        throw OrderConfirmationFailedException(apiSimpleResult.message ?: "")
                    }
                }
    }
}
