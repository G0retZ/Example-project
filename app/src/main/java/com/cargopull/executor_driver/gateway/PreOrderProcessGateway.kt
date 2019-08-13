package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PreOrderProcessGateway
constructor(private val apiService: ApiService,
            private val dataMapper: Mapper<ApiSimpleResult<ApiOrder>, Pair<ExecutorState, Order?>>
) : OrderConfirmationGateway<Order> {

    override fun sendDecision(order: Order, accepted: Boolean): Single<Pair<ExecutorState?, Order?>> {
        return apiService.sendPreOrderProcess(ApiOrderDecision(order.id, accepted))
                .subscribeOn(Schedulers.io())
                .map { apiSimpleResult ->
                    if ("200" == apiSimpleResult.code) {
                        dataMapper.map(apiSimpleResult)
                    } else {
                        throw OrderConfirmationFailedException(apiSimpleResult.message ?: "")
                    }
                }
    }
}
