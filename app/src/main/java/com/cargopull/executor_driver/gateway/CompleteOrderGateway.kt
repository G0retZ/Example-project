package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiOrderCostDetails
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.OrderCostDetails
import com.cargopull.executor_driver.interactor.CommonGatewaySingle
import com.google.gson.JsonParseException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class CompleteOrderGateway @Inject
constructor(private val apiService: ApiService,
            private val dataMapper: Mapper<ApiSimpleResult<ApiOrderCostDetails>, Pair<ExecutorState, OrderCostDetails?>>) : CommonGatewaySingle<Pair<ExecutorState, OrderCostDetails?>> {


    /**
     * Запрашивает сервер завершить заказ.
     *
     * @return [Single] результат - данные либо ошибка на сервере.
     */
    override val data: Single<Pair<ExecutorState, OrderCostDetails?>>
        get() = apiService.completeOrder(
                Collections.singletonMap("status", "COMPLETE_ORDER")
        ).subscribeOn(Schedulers.io())
                .map(dataMapper::map)
                .onErrorResumeNext { throwable ->
                    if (throwable is JsonParseException) {
                        Single.error(DataMappingException("Ошибка маппинга: неверный формат данных!", throwable))
                    } else {
                        Single.error(throwable)
                    }
                }
}
