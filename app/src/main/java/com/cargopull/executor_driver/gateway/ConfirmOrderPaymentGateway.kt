package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.interactor.CommonGatewaySingle
import com.google.gson.JsonParseException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

class ConfirmOrderPaymentGateway
constructor(private val apiService: ApiService,
            private val dataMapper: Mapper<ApiSimpleResult<Void>, Pair<ExecutorState, Void?>>
) : CommonGatewaySingle<Pair<ExecutorState, Void?>> {

    /**
     * Запрашивает сервер завершить заказ.
     *
     * @return [Single] результат - данные либо ошибка на сервере.
     */
    override val data: Single<Pair<ExecutorState, Void?>>
        get() = apiService.completeOrderPayment(
                Collections.singletonMap("status", "COMPLETE_PAYMENT_CONFIRMATION")
        ).subscribeOn(Schedulers.io())
                .map(dataMapper::map)
                .onErrorResumeNext { throwable ->
                    if (throwable is JsonParseException) {
                        Single.error(
                            DataMappingException(
                                "Mapping error: wrong data format!",
                                throwable
                            )
                        )
                    } else {
                        Single.error(throwable)
                    }
                }
}
