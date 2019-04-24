package com.cargopull.executor_driver.interactor

import com.cargopull.executor_driver.entity.OrderCostDetails
import com.cargopull.executor_driver.utils.EmptyEmitter
import io.reactivex.BackpressureStrategy
import io.reactivex.Emitter
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

/**
 * Юзкейс детального расчета заказа. Слушает детальный расчет заказа из гейтвея.
 */
interface OrderCostDetailsUseCase {

    /**
     * Запрашивает детальный расчет выполняемого заказа.
     *
     * @return [<] результат запроса.
     */
    val orderCostDetails: Flowable<OrderCostDetails>
}

class OrderCostDetailsUseCaseImpl(private val orderCostDetailsGateway: CommonGateway<OrderCostDetails>) : OrderCostDetailsUseCase, DataUpdateUseCase<OrderCostDetails>  {

    private var emitter: Emitter<OrderCostDetails> = EmptyEmitter<OrderCostDetails>()

    override val orderCostDetails: Flowable<OrderCostDetails> by lazy {
        Flowable.merge(
                Flowable.create<OrderCostDetails>({ emitter -> this.emitter = emitter }, BackpressureStrategy.BUFFER),
                orderCostDetailsGateway.data.observeOn(Schedulers.single())
                        .doOnComplete{ emitter.onComplete()}
        ).replay(1).refCount()
    }

    override fun updateWith(data: OrderCostDetails) {
        emitter.onNext(data)
    }
}
