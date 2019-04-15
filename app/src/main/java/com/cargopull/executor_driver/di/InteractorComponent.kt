package com.cargopull.executor_driver.di

import com.cargopull.executor_driver.entity.*
import com.cargopull.executor_driver.interactor.*
import com.cargopull.executor_driver.interactor.auth.*
import com.cargopull.executor_driver.interactor.map.HeatMapUseCase
import com.cargopull.executor_driver.interactor.map.HeatMapUseCaseImpl
import com.cargopull.executor_driver.interactor.services.ServicesUseCase
import com.cargopull.executor_driver.interactor.services.ServicesUseCaseImpl
import com.cargopull.executor_driver.interactor.vehicle.*
import com.cargopull.executor_driver.utils.TimeUtils

class InteractorComponent(
        private val timeUtils: TimeUtils,
        private val repositoryComponent: RepositoryComponent) {

    val callToClientUseCase: CallToClientUseCase by lazy {
        CallToClientUseCaseImpl(repositoryComponent.callToClientGateway)
    }
    val cancelledOrderMessageUseCase: NotificationMessageUseCase by lazy {
        NotificationMessageUseCaseImpl(repositoryComponent.cancelledOrderMessageGateway)
    }
    val reportProblemUseCase: ReportProblemUseCase
        get() {
            return ReportProblemUseCaseImpl(repositoryComponent.reportProblemGateway)
        }
    val confirmOrderPaymentUseCase: NextExecutorStateUseCase by lazy {
        NextExecutorStateUseCaseImpl(
                repositoryComponent.confirmOrderPaymentGateway,
                executorStateUseCaseImpl,
                null
        )
    }
    val currentCostPollingUseCase: CurrentCostPollingUseCase by lazy {
        CurrentCostPollingUseCaseImpl(repositoryComponent.currentCostPollingGateway)
    }
    val executorBalanceUseCase: ExecutorBalanceUseCase by lazy {
        ExecutorBalanceUseCaseImpl(repositoryComponent.executorBalanceGateway)
    }
    val executorStateNotOnlineUseCase: ExecutorStateNotOnlineUseCase by lazy {
        ExecutorStateNotOnlineUseCaseImpl(
                repositoryComponent.executorStateSwitchGateway,
                executorStateUseCase,
                ExecutorState.ONLINE
        )
    }
    val executorStateExitUseCase: ExecutorStateNotOnlineUseCase by lazy {
        ExecutorStateNotOnlineUseCaseImpl(
                repositoryComponent.executorStateSwitchGateway,
                executorStateUseCase,
                ExecutorState.BLOCKED,
                ExecutorState.SHIFT_CLOSED,
                ExecutorState.SHIFT_OPENED,
                ExecutorState.ONLINE,
                ExecutorState.DRIVER_ORDER_CONFIRMATION,
                ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION,
                ExecutorState.CLIENT_ORDER_CONFIRMATION,
                ExecutorState.MOVING_TO_CLIENT,
                ExecutorState.WAITING_FOR_CLIENT,
                ExecutorState.ORDER_FULFILLMENT,
                ExecutorState.PAYMENT_CONFIRMATION
        )
    }
    val executorStateUseCase: ExecutorStateUseCase by lazy {
        executorStateUseCaseImpl
    }
    val geoLocationUseCase: GeoLocationUseCase by lazy {
        GeoLocationUseCaseImpl(
                repositoryComponent.geoLocationGateway,
                repositoryComponent.geoTrackingGateway,
                executorStateUseCase
        )
    }
    val missedOrderUseCase: NotificationMessageUseCase by lazy {
        NotificationMessageUseCaseImpl(repositoryComponent.missedOrderGateway)
    }
    val movingToClientUseCase: MovingToClientUseCase by lazy {
        MovingToClientUseCaseImpl(repositoryComponent.movingToClientGateway)
    }
    val orderConfirmationUseCase: OrderConfirmationUseCase by lazy {
        OrderConfirmationUseCaseImpl(
                orderUseCase,
                repositoryComponent.orderConfirmationGateway,
                null,
                RemoveOrdersUseCaseImpl(
                        preOrdersSetUseCase
                )
        )
    }
    val preOrderBookingUseCase: OrderConfirmationUseCase by lazy {
        OrderConfirmationUseCaseImpl(
                preOrderUseCase,
                repositoryComponent.preOrderConfirmationGateway,
                preOrderUseCaseImpl,
                preOrdersSetUseCase
        )
    }
    val orderCostDetailsUseCase: DataReceiver<OrderCostDetails> by lazy {
        orderCostDetailsSharer
    }
    val orderCurrentCostUseCase: OrderCurrentCostUseCase by lazy {
        OrderCurrentCostUseCaseImpl(
                orderUseCase,
                repositoryComponent.orderCurrentCostGateway
        )
    }
    val orderFulfillmentTimeUseCase: OrderFulfillmentTimeUseCase by lazy {
        OrderFulfillmentTimeUseCaseImpl(orderUseCase, timeUtils)
    }
    val orderRouteUseCase: OrderRouteUseCase by lazy {
        OrderRouteUseCaseImpl(
                orderUseCase,
                repositoryComponent.orderRouteGateway
        )
    }
    val completeOrderUseCase: NextExecutorStateUseCase by lazy {
        NextExecutorStateUseCaseImpl(
                repositoryComponent.completeOrderGateway,
                executorStateUseCaseImpl,
                orderCostDetailsSharer
        )
    }
    val orderUseCase: OrderUseCase by lazy {
        OrderUseCaseImpl(repositoryComponent.orderGateway)
    }
    val preOrderUseCase: OrderUseCase by lazy {
        preOrderUseCaseImpl
    }
    val serverConnectionUseCase: ServerConnectionUseCase by lazy {
        ServerConnectionUseCaseImpl(repositoryComponent.serverConnectionGateway)
    }
    val serverTimeUseCase: ServerTimeUseCase by lazy {
        ServerTimeUseCaseImpl(
                repositoryComponent.serverTimeGateway,
                timeUtils
        )
    }
    val updateMessageUseCase: UpdateMessageUseCase by lazy {
        UpdateMessageUseCaseImpl(repositoryComponent.updateMessageGateway)
    }
    val waitingForClientUseCase: WaitingForClientUseCase by lazy {
        WaitingForClientUseCaseImpl(repositoryComponent.waitingForClientGateway)
    }
    val loginUseCase: LoginUseCase by lazy {
        LoginUseCaseImpl(
                repositoryComponent.loginStorage,
                LoginValidator()
        )
    }
    val preOrdersSetUseCase: OrdersUseCase by lazy {
        OrdersUseCaseImpl(
                repositoryComponent.preOrdersSetGateway,
                ChangedOrderUseCaseImpl(repositoryComponent.changedOrderGateway),
                CancelledOrderUseCaseImpl(repositoryComponent.cancelledOrderGateway)
        )
    }
    val passwordUseCase: PasswordUseCase by lazy {
        PasswordUseCaseImpl(
                repositoryComponent.passwordGateway,
                repositoryComponent.loginReceiver,
                PasswordValidator()
        )
    }
    val smsUseCase: SmsUseCase by lazy {
        SmsUseCaseImpl(
                repositoryComponent.smsGateway,
                repositoryComponent.loginReceiver,
                PhoneNumberValidator()
        )
    }
    val heatMapUseCase: HeatMapUseCase by lazy {
        HeatMapUseCaseImpl(repositoryComponent.heatMapGateway)
    }
    val servicesUseCase: ServicesUseCase by lazy {
        ServicesUseCaseImpl(repositoryComponent.servicesGateway)
    }
    val selectedVehicleUseCase: SelectedVehicleUseCase by lazy {
        SelectedVehicleUseCaseImpl(vehicleChoiceSharer)
    }
    val upcomingPreOrderMessagesUseCase: NotificationMessageUseCase by lazy {
        NotificationMessageUseCaseImpl(repositoryComponent.upcomingPreOrderMessagesGateway)
    }
    val vehicleChoiceUseCase: VehicleChoiceUseCase by lazy {
        VehicleChoiceUseCaseImpl(
                repositoryComponent.vehiclesAndOptionsGateway,
                vehicleChoiceSharer
        )
    }
    val currentVehicleChoiceUseCase: VehicleChoiceUseCase by lazy {
        VehicleChoiceUseCaseImpl(
                repositoryComponent.selectedVehiclesAndOptionsGateway,
                vehicleChoiceSharer
        )
    }
    val vehicleOptionsUseCase: VehicleOptionsUseCase by lazy {
        VehicleOptionsUseCaseImpl(
                repositoryComponent.vehicleOptionsGateway,
                vehicleChoiceSharer,
                repositoryComponent.lastUsedVehicleGateway,
                repositoryComponent.vehiclesAndOptionsGateway
        )
    }
    val currentVehicleOptionsUseCase: VehicleOptionsUseCase by lazy {
        VehicleOptionsUseCaseImpl(
                repositoryComponent.currentVehicleOptionsGateway,
                vehicleChoiceSharer,
                repositoryComponent.lastUsedVehicleGateway,
                repositoryComponent.selectedVehiclesAndOptionsGateway
        )
    }
    val vehiclesAndOptionsUseCase: VehiclesAndOptionsUseCase by lazy {
        VehiclesAndOptionsUseCaseImpl(
                repositoryComponent.vehiclesAndOptionsGateway,
                vehicleChoiceSharer,
                repositoryComponent.lastUsedVehicleGateway
        )
    }
    val selectedVehiclesAndOptionsUseCase: VehiclesAndOptionsUseCase by lazy {
        VehiclesAndOptionsUseCaseImpl(
                repositoryComponent.selectedVehiclesAndOptionsGateway,
                vehicleChoiceSharer,
                repositoryComponent.lastUsedVehicleGateway
        )
    }
    val selectedOrderUseCase: SelectedOrderUseCase by lazy {
        selectedOrderUseCaseImpl
    }
    val selectedPreOrderUseCase: OrderUseCase by lazy {
        selectedOrderUseCaseImpl
    }
    val selectedPreOrderConfirmationUseCase: OrderConfirmationUseCase by lazy {
        OrderConfirmationUseCaseImpl(
                selectedPreOrderUseCase,
                repositoryComponent.preOrderProcessGateway,
                null,
                RemoveOrdersUseCaseImpl(
                        preOrdersSetUseCase
                )
        )
    }
    val upcomingPreOrderUseCase: UpcomingPreOrderUseCaseImpl by lazy {
        UpcomingPreOrderUseCaseImpl(
                repositoryComponent.upcomingPreOrderGateway,
                preOrdersSetUseCase
        )
    }
    val upcomingPreOrderConfirmationUseCase: OrderConfirmationUseCase by lazy {
        OrderConfirmationUseCaseImpl(
                upcomingPreOrderUseCase,
                repositoryComponent.preOrderProcessGateway,
                null,
                RemoveOrdersUseCaseImpl(
                        preOrdersSetUseCase
                )
        )
    }

    private val vehicleChoiceSharer: MemoryDataSharer<Vehicle> = VehicleChoiceSharer()
    private val executorStateUseCaseImpl: ExecutorStateUseCaseImpl by lazy {
        ExecutorStateUseCaseImpl(repositoryComponent.executorStateGateway)
    }
    private val preOrderUseCaseImpl: OrderUseCaseImpl by lazy {
        OrderUseCaseImpl(repositoryComponent.preOrderGateway)
    }
    private val selectedOrderUseCaseImpl: SelectedOrderUseCaseImpl by lazy {
        SelectedOrderUseCaseImpl(preOrdersSetUseCase)
    }
    private val orderCostDetailsSharer: MemoryDataSharer<OrderCostDetails> by lazy {
        OrderCostDetailsUseCase()
    }
}
