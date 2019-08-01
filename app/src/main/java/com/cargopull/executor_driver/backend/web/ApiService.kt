package com.cargopull.executor_driver.backend.web

import com.cargopull.executor_driver.backend.web.incoming.*
import com.cargopull.executor_driver.backend.web.outgoing.ApiLogin
import com.cargopull.executor_driver.backend.web.outgoing.ApiOptionItems
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision
import com.cargopull.executor_driver.entity.ExecutorState
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

/**
 * Общение с сервером API.
 */
interface ApiService {

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
