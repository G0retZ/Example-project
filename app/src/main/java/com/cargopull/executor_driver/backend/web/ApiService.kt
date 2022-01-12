package com.cargopull.executor_driver.backend.web

import com.cargopull.executor_driver.backend.web.incoming.*
import com.cargopull.executor_driver.backend.web.outgoing.ApiLogin
import com.cargopull.executor_driver.backend.web.outgoing.ApiOptionItems
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
import com.cargopull.executor_driver.entity.ExecutorState
import com.cargopull.executor_driver.interactor.CommonGateway
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

/**
 * Общение с сервером API.
 */
interface ApiService {

    //  Запрос тепловой карты.
    @get:GET("api/public/v1/init")
    val initialState: Single<ApiInitialState>

    //  Запрос тепловой карты.
    @get:GET("api/public/v1/geoMap")
    val heatMap: Single<String>

    //  Запрос ТС текущего исполнителя.
    @get:GET("api/public/v1/mobile/car")
    val optionsForOnline: Single<ApiOptionsForOnline>

    //  Запрос услуг текущего исполнителя.
    @get:GET("api/public/v1/mobile/car/carSearchRequest")
    val myServices: Single<List<ApiServiceItem>>

    //  Запрос выбранных услуг текущего исполнителя.
    @get:GET("api/public/v1/mobile/car/carSearchRequest/assigned")
    val mySelectedServices: Single<String>

    //  Запрос опций выбранного ТС и текущего исполнителя.
    @get:GET("api/public/v1/mobile/car/active")
    val selectedOptionsForOnline: Single<ApiOptionsForOnline>

    //  Запрос списка проблем для сообщения.
    @get:GET("api/public/v1/mobile/order/current/reportProblem/reasons")
    val reportProblems: Single<List<ApiProblem>>

    //  Запрос СМС с кодом.
    @GET("api/public/v1/login/password/sms")
    fun sendMeCode(@Query("login") phoneNumber: String): Completable

    //  Авторизация.
    @POST("api/public/v1/login")
    fun authorize(@Body apiLogin: ApiLogin): Completable

    //  Запрос занятия выборанной ТС с перечисленными опциями ТС и исполнителя.
    @PUT("api/public/v1/mobile/car/{carId}/vehicleOptionItem")
    fun occupyCarWithOptions(
            @Path("carId") carId: Long,
            @Body apiOptionItems: ApiOptionItems
    ): Completable

    //  Запрос закрепления выбора услуг исполнителя.
    @PUT("api/public/v1/mobile/car/carSearchRequest")
    fun setMyServices(
            @Query("ids") servicesIds: String
    ): Completable

    //  Запрос установки опций выбранного ТС и текущего исполнителя.
    @PUT("api/public/v1/mobile/car/vehicleOptionItem")
    fun setSelectedVehicleOptions(
            @Body apiOptionItems: ApiOptionItems
    ): Completable

    //  Запрос принятия или отказа от срочного заказа.
    @POST("api/public/v1/mobile/order/accept")
    fun acceptOrderOffer(
            @Body decision: ApiOrderDecision
    ): Single<ApiSimpleResult<Void>>

    //  Запрос бронирования или отказа от предварительного заказа.
    @POST("api/public/v1/mobile/order/assign")
    fun sendPreOrderDecision(
            @Body decision: ApiOrderDecision
    ): Single<ApiSimpleResult<String>>

    //  Запрос отказа от бронирования или выезда на забронированный предварительный заказ.
    @POST("api/public/v1/mobile/order/preliminary/process")
    fun sendPreOrderProcess(
            @Body decision: ApiOrderDecision
    ): Single<ApiSimpleResult<ApiOrder>>

    //  Запрос отправки FCM токена.
    @PUT("api/public/v1/mobile/fireBase/registrationToken")
    fun sendFcmInstanceID(
            @Query("registrationToken") carId: String
    ): Completable

    //  Запрос истории заказов.
    @GET("api/public/v1/orderHistoryDetalization/forPeriod")
    fun getOrdersHistory(
            @Query("dateFrom") fromDate: Long,
            @Query("dateTo") toDate: Long
    ): Single<Map<String, ApiOrdersSummary>>

    //  Действия над заказом.
    //  ключ: status
    //  Значения: CALL_TO_CLIENT
    @POST("api/public/v1/mobile/order/current")
    fun callToClient(
            @Body params: Map<String, String>
    ): Completable

    //  Действия над заказом.
    //  ключ: status
    //  Значения: DRIVER_ARRIVED
    @POST("api/public/v1/mobile/order/current")
    fun reportArrived(
            @Body params: Map<String, String>
    ): Single<ApiSimpleResult<List<ApiRoutePoint>>>

    //  Действия над заказом.
    //  ключ: status
    //  Значения: START_ORDER
    @POST("api/public/v1/mobile/order/current")
    fun startOrder(
            @Body params: Map<String, String>
    ): Single<ApiSimpleResult<ApiOrder>>

    //  Действия над заказом.
    //  ключ: status
    //  Значения: COMPLETE_ORDER
    @POST("api/public/v1/mobile/order/current")
    fun completeOrder(
            @Body params: Map<String, String>
    ): Single<ApiSimpleResult<ApiOrderCostDetails>>

    //  Действия над заказом.
    // ключ: status
    // Значения: COMPLETE_PAYMENT_CONFIRMATION
    @POST("api/public/v1/mobile/order/current")
    fun completeOrderPayment(
            @Body params: Map<String, String>
    ): Single<ApiSimpleResult<Void>>

    //  Запрос смены статуса заказа.
    @POST("api/public/v1/mobile/order/current/routePoint/{id}/next")
    fun makeRoutePointNext(
            @Path("id") routePointId: Long
    ): Single<ApiSimpleResult<List<ApiRoutePoint>>>

    //  Запрос смены статуса заказа.
    @POST("api/public/v1/mobile/order/current/routePoint/{id}/complete")
    fun completeRoutePoint(
            @Path("id") routePointId: Long
    ): Single<ApiSimpleResult<List<ApiRoutePoint>>>

    //  Сообщите о выбранной проблеме.
    @POST("api/public/v1/mobile/order/current/reportProblem")
    fun reportProblem(
            @Body apiProblem: ApiProblem
    ): Completable

    //  Переключить свой статус.
    @POST("api/public/v1/mobile/driver/status")
    fun switchStatus(
            @Body executorState: ExecutorState
    ): Completable
}

class ApiConnectionWrapper(private val apiService: ApiService, private val gateway: CommonGateway<Boolean>) : ApiService {

    override val initialState: Single<ApiInitialState>
        get() = wrapCall(apiService.initialState)

    override val heatMap: Single<String>
        get() = wrapCall(apiService.heatMap)

    override val optionsForOnline: Single<ApiOptionsForOnline>
        get() = wrapCall(apiService.optionsForOnline)

    override val myServices: Single<List<ApiServiceItem>>
        get() = wrapCall(apiService.myServices)

    override val mySelectedServices: Single<String>
        get() = wrapCall(apiService.mySelectedServices)

    override val selectedOptionsForOnline: Single<ApiOptionsForOnline>
        get() = wrapCall(apiService.selectedOptionsForOnline)

    override val reportProblems: Single<List<ApiProblem>>
        get() = wrapCall(apiService.reportProblems)

    override fun sendMeCode(phoneNumber: String): Completable =
            wrapCall(apiService.sendMeCode(phoneNumber))

    override fun authorize(apiLogin: ApiLogin): Completable =
            wrapCall(apiService.authorize(apiLogin))

    override fun occupyCarWithOptions(carId: Long, apiOptionItems: ApiOptionItems): Completable =
            wrapCall(apiService.occupyCarWithOptions(carId, apiOptionItems))

    override fun setMyServices(servicesIds: String): Completable =
            wrapCall(apiService.setMyServices(servicesIds))

    override fun setSelectedVehicleOptions(apiOptionItems: ApiOptionItems): Completable =
            wrapCall(apiService.setSelectedVehicleOptions(apiOptionItems))

    override fun acceptOrderOffer(decision: ApiOrderDecision): Single<ApiSimpleResult<Void>> =
            wrapCall(apiService.acceptOrderOffer(decision))

    override fun sendPreOrderDecision(decision: ApiOrderDecision): Single<ApiSimpleResult<String>> =
            wrapCall(apiService.sendPreOrderDecision(decision))

    override fun sendPreOrderProcess(decision: ApiOrderDecision): Single<ApiSimpleResult<ApiOrder>> =
            wrapCall(apiService.sendPreOrderProcess(decision))

    override fun sendFcmInstanceID(carId: String): Completable =
            wrapCall(apiService.sendFcmInstanceID(carId))

    override fun getOrdersHistory(fromDate: Long, toDate: Long): Single<Map<String, ApiOrdersSummary>> =
            wrapCall(apiService.getOrdersHistory(fromDate, toDate))

    override fun callToClient(params: Map<String, String>): Completable =
            wrapCall(apiService.callToClient(params))

    override fun reportArrived(params: Map<String, String>): Single<ApiSimpleResult<List<ApiRoutePoint>>> =
            wrapCall(apiService.reportArrived(params))

    override fun startOrder(params: Map<String, String>): Single<ApiSimpleResult<ApiOrder>> =
            wrapCall(apiService.startOrder(params))

    override fun completeOrder(params: Map<String, String>): Single<ApiSimpleResult<ApiOrderCostDetails>> =
            wrapCall(apiService.completeOrder(params))

    override fun completeOrderPayment(params: Map<String, String>): Single<ApiSimpleResult<Void>> =
            wrapCall(apiService.completeOrderPayment(params))

    override fun makeRoutePointNext(routePointId: Long): Single<ApiSimpleResult<List<ApiRoutePoint>>> =
            wrapCall(apiService.makeRoutePointNext(routePointId))

    override fun completeRoutePoint(routePointId: Long): Single<ApiSimpleResult<List<ApiRoutePoint>>> =
            wrapCall(apiService.completeRoutePoint(routePointId))

    override fun reportProblem(apiProblem: ApiProblem): Completable =
            wrapCall(apiService.reportProblem(apiProblem))

    override fun switchStatus(executorState: ExecutorState): Completable =
            wrapCall(apiService.switchStatus(executorState))

    private fun wrapCall(source: Completable): Completable = gateway.data
            .firstOrError().flatMapCompletable {
                if (it) source else Completable.error(NoNetworkException())
            }

    private fun <T> wrapCall(source: Single<T>): Single<T> = gateway.data
            .firstOrError().flatMap {
                if (it) source else Single.error<T>(NoNetworkException())
            }
}