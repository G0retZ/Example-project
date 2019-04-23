package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
import com.cargopull.executor_driver.entity.OrderOfferDecisionException
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * Юзкейс принятия заказов. Принимает или отвергает последний полученный заказ.
 */
interface OrderConfirmationUseCase {

    /**
     * Получает таймаут для решения исполнителя по принятию пришедших заказов, из расчета один таймаут
     * на один заказ.
     *
     * @return [<] результат - успех с ID заказа и таймаутом либо ошибка с текстом.
     */
    val orderDecisionTimeout: Flowable<Pair<Long, Long>>

    /**
     * Передает решение исполнителя по принятию заказа.
     *
     * @param confirmed согласие исполнителя на прием заказа.
     * @return [<] результат - успех либо ошибка с текстом.
     */
    fun sendDecision(confirmed: Boolean): Single<String>
}

class OrderConfirmationUseCaseImpl<D>
constructor(
        private val orderUseCase: OrderUseCase,
        private val orderConfirmationGateway: OrderConfirmationGateway<D?>,
        private val orderDecisionUseCase: OrderDecisionUseCase?,
        private val ordersUseCase: OrdersUseCase?,
        private val updateExecutorStateUseCase: DataUpdateUseCase<ExecutorState>?,
        private val updateUseCase: DataUpdateUseCase<D>?) : OrderConfirmationUseCase {

    override val orderDecisionTimeout: Flowable<Pair<Long, Long>>
        get() = orderUseCase.orders
                .map { order -> Pair(order.id, order.timeout) }

    override fun sendDecision(confirmed: Boolean): Single<String> {
        return orderUseCase.orders
                .distinct()
                .flatMapSingle(object : Function<Order, SingleSource<Pair<ExecutorState?, D?>>> {
                    var orderDecisionMade: Boolean = false

                    @Throws(Exception::class)
                    override fun apply(order: Order): SingleSource<Pair<ExecutorState?, D?>> {
                        if (orderDecisionMade) {
                            throw OrderOfferDecisionException()
                        }
                        orderDecisionMade = true
                        return orderConfirmationGateway.sendDecision(order, confirmed)
                                .observeOn(Schedulers.single())
                                .doAfterSuccess { (_, _) ->
                                    ordersUseCase?.let {
                                        if (confirmed) {
                                            it.addOrder(order)
                                        } else {
                                            it.removeOrder(order)
                                        }
                                    }
                                }
                    }
                })
                .firstOrError()
                .doOnSuccess { (first, second) ->
                    orderDecisionUseCase?.setOrderOfferDecisionMade()
                    first?.let { updateExecutorStateUseCase?.updateWith(it) }
                    second?.let { updateUseCase?.updateWith(it) }
                }.map<String> { pair ->
                    if (pair.second is String) {
                        pair.second as String
                    } else {
                        pair.first?.data ?: ""
                    }
                }
    }
}
