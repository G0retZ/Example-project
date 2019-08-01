package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
import com.cargopull.executor_driver.interactor.OrderConfirmationGateway
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class OrderConfirmationGatewayImpl
constructor(private val apiService: ApiService,
            private val errorMapper: Mapper<Throwable, Throwable>) : OrderConfirmationGateway<Void> {

    override fun sendDecision(order: Order, accepted: Boolean): Single<Pair<ExecutorState?, Void?>> {
        return apiService.acceptOrderOffer(ApiOrderDecision(order.id, accepted))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext { e -> Single.error(errorMapper.map(e)) }
                .map {
                    try {
                        val state = it.status?.let(ExecutorState::valueOf)
                        state?.let{ it1 ->
                            it1.data = it.message
                            if (it1 == ExecutorState.CLIENT_ORDER_CONFIRMATION) {
                                it1.customerTimer = 600_000
                            }
                        }
                        Pair(state, null)
                    } catch (e: Exception) {
                        throw DataMappingException("Ошибка маппинга: неверный формат статуса!", e)
                    }
                }
    }
}
