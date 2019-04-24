package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.Order
import io.reactivex.Single

/**
 * Гейтвей подтверждения заказа.
 */
interface OrderConfirmationGateway<D> {

    /**
     * Передает решение исполнителя по принятию заказа.
     *
     * @param order заказа, к которому относится это решение.
     * @param accepted согласие исполнителя на прием заказа.
     * @return [<] результат - успех с данными, либо ошибка.
     */
    fun sendDecision(order: Order, accepted: Boolean): Single<Pair<ExecutorState?, D?>>
}
