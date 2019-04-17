package com.cargopull.executor_driver.di

import com.cargopull.executor_driver.backend.web.TopicListener
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint
import com.cargopull.executor_driver.entity.*
import com.cargopull.executor_driver.gateway.*
import com.cargopull.executor_driver.interactor.*
import com.cargopull.executor_driver.interactor.auth.PasswordGateway
import com.cargopull.executor_driver.interactor.auth.SmsGateway
import com.cargopull.executor_driver.interactor.map.HeatMapGateway
import com.cargopull.executor_driver.interactor.services.ServicesGateway
import com.cargopull.executor_driver.interactor.vehicle.LastUsedVehicleGateway
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsGateway
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway
import java.util.*

class RepositoryComponent(private val backendComponent: BackendComponent) {

    private val loginGateway: LoginGateway by lazy {
        LoginGateway(backendComponent.appSettingsService)
    }
    private val personalTopicListener: TopicListener by lazy {
        backendComponent.personalTopicListener(loginGateway)
    }
    val loginReceiver: DataReceiver<String> by lazy {
        loginGateway
    }
    val loginStorage: DataUpdateAndResetUseCase<String> by lazy {
        loginGateway
    }
    val callToClientGateway: CallToClientGateway by lazy {
        CallToClientGatewayImpl(backendComponent.apiService)
    }
    val changedOrderGateway: CommonGateway<Order> by lazy {
        TopicGateway(
                personalTopicListener,
                ChangedOrderFilter(),
                OrderStompMapper(
                        OrderApiMapper(
                                VehicleOptionApiMapper(),
                                RoutePointApiMapper()
                        )
                )
        )
    }
    val cancelledOrderGateway: CommonGateway<Order> by lazy {
        TopicGateway(
                personalTopicListener,
                CancelledOrderFilter(),
                CancelledOrderApiMapper()
        )
    }
    val cancelledOrderMessageGateway: CommonGateway<String> by lazy {
        TopicGateway(
                personalTopicListener,
                CancelledOrderFilter(),
                MessagePayloadApiMapper()
        )
    }
    val reportProblemGateway: ReportProblemGateway by lazy {
        ReportProblemGatewayImpl(backendComponent.apiService, ProblemApiMapper())
    }
    val confirmOrderPaymentGateway: CommonGatewaySingle<Pair<ExecutorState, Void?>> by lazy {
        ConfirmOrderPaymentGateway(
                backendComponent.apiService,
                StateAndDataApiMapper(
                        MirrorMapper()
                )
        )
    }
    val currentCostPollingGateway: CurrentCostPollingGateway by lazy {
        CurrentCostPollingGatewayImpl(
                personalTopicListener,
                backendComponent.stompClient,
                CurrentCostPollingTimersApiMapper()
        )
    }
    val executorBalanceGateway: CommonGateway<ExecutorBalance> by lazy {
        TopicGateway(
                personalTopicListener,
                ExecutorBalanceFilter(),
                ExecutorBalanceApiMapper()
        )
    }
    val executorStateGateway: CommonGateway<ExecutorState> by lazy {
        TopicGateway(
                personalTopicListener,
                ExecutorStateFilter(),
                ExecutorStateApiMapper(MessagePayloadApiMapper())
        )
    }
    val executorStateSwitchGateway: ExecutorStateSwitchGateway by lazy {
        ExecutorStateSwitchGatewayImpl(backendComponent.apiService)
    }
    val geoLocationGateway: GeoLocationGateway by lazy {
        GeoLocationGatewayImpl(backendComponent.geolocationCenter)
    }
    val geoTrackingGateway: GeoTrackingGateway by lazy {
        GeoTrackingGatewayImpl(backendComponent.stompClient)
    }
    val missedOrderGateway: CommonGateway<String> by lazy {
        TopicGateway(
                personalTopicListener,
                MissedOrderFilter(),
                MessagePayloadApiMapper()
        )
    }
    val reportArrivedGateway: CommonGatewaySingle<Pair<ExecutorState, List<RoutePoint>?>> by lazy {
        val mapper = RoutePointApiMapper()
        ReportArrivedGateway(
                backendComponent.apiService,
                StateAndDataApiMapper(
                        object : Mapper<List<ApiRoutePoint>, List<RoutePoint>> {
                            override fun map(from: List<ApiRoutePoint>) = from.map(mapper::map)
                        }
                )
        )
    }
    val orderConfirmationGateway: OrderConfirmationGateway<Void?> by lazy {
        OrderConfirmationGatewayImpl(backendComponent.apiService, OrderConfirmationErrorMapper())
    }
    val preOrderBookingGateway: OrderConfirmationGateway<String?> by lazy {
        PreOrderBookingGateway(backendComponent.apiService)
    }
    val preOrderProcessGateway: OrderConfirmationGateway<Order?> by lazy {
        PreOrderProcessGateway(
                backendComponent.apiService,
                StateAndDataApiMapper(
                        OrderApiMapper(
                                VehicleOptionApiMapper(),
                                RoutePointApiMapper()
                        )
                )
        )
    }
    val orderCurrentCostGateway: CommonGateway<Long> by lazy {
        TopicGateway(
                personalTopicListener,
                OrderCurrentCostFilter(),
                OrderCurrentCostApiMapper()
        )
    }
    val orderGateway: CommonGateway<Order> by lazy {
        TopicGateway(
                personalTopicListener,
                OrderFilter(),
                OrderStompMapper(
                        OrderApiMapper(
                                VehicleOptionApiMapper(),
                                RoutePointApiMapper()
                        )
                )
        )
    }
    val preOrderGateway: CommonGateway<Order> by lazy {
        TopicGateway(
                personalTopicListener,
                PreOrderFilter(),
                OrderStompMapper(
                        OrderApiMapper(
                                VehicleOptionApiMapper(),
                                RoutePointApiMapper()
                        )
                )
        )
    }
    val orderRouteGateway: OrderRouteGateway by lazy {
        OrderRouteGatewayImpl(backendComponent.apiService)
    }
    val completeOrderGateway: CommonGatewaySingle<Pair<ExecutorState, OrderCostDetails?>> by lazy {
        CompleteOrderGateway(
                backendComponent.apiService,
                StateAndDataApiMapper(
                        OrderCostDetailsApiMapper()
                )
        )
    }
    val serverConnectionGateway: ServerConnectionGateway by lazy {
        ServerConnectionGatewayImpl(backendComponent.stompClient)
    }
    val serverTimeGateway: CommonGateway<Long> by lazy {
        TopicGateway(
                personalTopicListener,
                ServerTimeFilter(),
                ServerTimeApiMapper()
        )
    }
    val updateMessageGateway: CommonGateway<String> by lazy {
        TopicGateway(
                personalTopicListener,
                UpdateMessageFilter(),
                MessagePayloadApiMapper()
        )
    }
    val startOrderGateway: CommonGatewaySingle<Pair<ExecutorState, List<RoutePoint>?>> by lazy {
        val mapper = RoutePointApiMapper()
        StartOrderGateway(
                backendComponent.apiService,
                StateAndDataApiMapper(
                        object : Mapper<List<ApiRoutePoint>, List<RoutePoint>> {
                            override fun map(from: List<ApiRoutePoint>) = from.map(mapper::map)
                        }
                )
        )
    }
    val passwordGateway: PasswordGateway by lazy {
        PasswordGatewayImpl(backendComponent.apiService)
    }
    val smsGateway: SmsGateway by lazy {
        SmsGatewayImpl(backendComponent.apiService)
    }
    val heatMapGateway: HeatMapGateway by lazy {
        HeatMapGatewayImpl(backendComponent.apiService)
    }
    val servicesGateway: ServicesGateway by lazy {
        ServicesGatewayImpl(backendComponent.apiService, ServiceApiMapper())
    }
    val lastUsedVehicleGateway: LastUsedVehicleGateway by lazy {
        LastUsedVehicleGatewayImpl(backendComponent.appSettingsService)
    }
    val upcomingPreOrderMessagesGateway: CommonGateway<String> by lazy {
        TopicGateway(
                personalTopicListener,
                UpcomingPreOrderFilter(),
                MessagePayloadApiMapper()
        )
    }
    val vehicleOptionsGateway: VehicleOptionsGateway by lazy {
        VehicleOptionsGatewayImpl(backendComponent.apiService)
    }
    val currentVehicleOptionsGateway: VehicleOptionsGateway by lazy {
        CurrentVehicleOptionsGatewayImpl(backendComponent.apiService)
    }
    val vehiclesAndOptionsGateway: VehiclesAndOptionsGateway by lazy {
        VehiclesAndOptionsGatewayImpl(
                backendComponent.apiService,
                VehicleOptionApiMapper(),
                VehicleApiMapper(
                        VehicleOptionApiMapper()
                ),
                VehiclesAndOptionsErrorMapper()
        )
    }
    val selectedVehiclesAndOptionsGateway: VehiclesAndOptionsGateway by lazy {
        SelectedVehicleAndOptionsGatewayImpl(
                backendComponent.apiService,
                VehicleOptionApiMapper(),
                VehicleApiMapper(
                        VehicleOptionApiMapper()
                ),
                VehiclesAndOptionsErrorMapper()
        )
    }
    val preOrdersSetGateway: CommonGateway<Set<Order>> by lazy {
        TopicGateway(
                personalTopicListener,
                PreOrdersListFilter(),
                PreOrdersListApiMapper(
                        VehicleOptionApiMapper(),
                        RoutePointApiMapper()
                ),
                HashSet()
        )
    }
    val upcomingPreOrderGateway: CommonGateway<Order> by lazy {
        TopicGateway(
                personalTopicListener,
                UpcomingPreOrderFilter(),
                UpcomingPreOrderApiMapper()
        )
    }

    val geoLocationStateGateway: CommonGateway<Boolean> by lazy {
        GeoLocationAvailabilityGatewayImpl(backendComponent.geolocationCenter)
    }
    val announcementsGateway: CommonGateway<String> by lazy {
        FcmGateway(
                backendComponent.fcmSender,
                MessageFcmFilter(),
                MessageFcmMapper()
        )
    }
    val ordersHistorySummaryGateway: OrdersHistorySummaryGateway by lazy {
        OrdersHistorySummaryGatewayImpl(
                backendComponent.apiService,
                OrdersHistorySummaryApiMapper()
        )
    }
}
