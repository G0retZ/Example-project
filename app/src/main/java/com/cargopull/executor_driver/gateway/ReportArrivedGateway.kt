package com.cargopull.executor_driver.gateway

import com.cargopull.executor_driver.backend.web.ApiService
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.entity.RoutePoint
import com.cargopull.executor_driver.interactor.CommonGatewaySingle
import com.google.gson.JsonParseException
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*

class ReportArrivedGateway
constructor(private val apiService: ApiService,
            private val dataMapper: Mapper<ApiSimpleResult<List<ApiRoutePoint>>, Pair<ExecutorState, List<RoutePoint>?>>
) : CommonGatewaySingle<Pair<ExecutorState, List<RoutePoint>?>> {

    /**
     * Запрашивает сервер завершить заказ.
     *
     * @return [Single] результат - данные либо ошибка на сервере.
     */
    override val data: Single<Pair<ExecutorState, List<RoutePoint>?>>
        get() = apiService.reportArrived(
                Collections.singletonMap("status", "DRIVER_ARRIVED")
        ).subscribeOn(Schedulers.io())
                .map(dataMapper::map)
                .onErrorResumeNext { throwable ->
                    if (throwable is JsonParseException) {
                        Single.error(
                            DataMappingException(
                                "Mapping error: wrong format of data!",
                                throwable
                            )
                        )
                    } else {
                        Single.error(throwable)
                    }
                }
}
