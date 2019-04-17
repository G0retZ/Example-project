package com.cargopull.executor_driver.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.cargopull.executor_driver.presentation.NextExecutorStateViewModel
import com.cargopull.executor_driver.presentation.NextExecutorStateViewModelImpl
import com.cargopull.executor_driver.presentation.ViewModelFactory
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModelImpl
import com.cargopull.executor_driver.presentation.balance.BalanceViewModel
import com.cargopull.executor_driver.presentation.balance.BalanceViewModelImpl
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewModel
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewModelImpl
import com.cargopull.executor_driver.presentation.calltooperator.CallToOperatorViewModel
import com.cargopull.executor_driver.presentation.calltooperator.CallToOperatorViewModelImpl
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewModel
import com.cargopull.executor_driver.presentation.cancelledorder.CancelledOrderViewModelImpl
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModelImpl
import com.cargopull.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewModel
import com.cargopull.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewModelImpl
import com.cargopull.executor_driver.presentation.code.CodeViewModel
import com.cargopull.executor_driver.presentation.code.CodeViewModelImpl
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModel
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModelImpl
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModel
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModelImpl
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModelImpl
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModel
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModelImpl
import com.cargopull.executor_driver.presentation.geolocationstate.GeoLocationStateViewModel
import com.cargopull.executor_driver.presentation.geolocationstate.GeoLocationStateViewModelImpl
import com.cargopull.executor_driver.presentation.map.MapViewModel
import com.cargopull.executor_driver.presentation.map.MapViewModelImpl
import com.cargopull.executor_driver.presentation.menu.MenuViewModel
import com.cargopull.executor_driver.presentation.menu.MenuViewModelImpl
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModel
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModelImpl
import com.cargopull.executor_driver.presentation.movingtoclienttimer.MovingToClientTimerViewModel
import com.cargopull.executor_driver.presentation.movingtoclienttimer.MovingToClientTimerViewModelImpl
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModel
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModelImpl
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModel
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModel
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModelImpl
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModel
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModelImpl
import com.cargopull.executor_driver.presentation.order.OrderViewModel
import com.cargopull.executor_driver.presentation.order.OrderViewModelImpl
import com.cargopull.executor_driver.presentation.orderconfirmation.*
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewModel
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewModelImpl
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModel
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModelImpl
import com.cargopull.executor_driver.presentation.ordershistoryheader.OrdersHistoryHeaderViewModel
import com.cargopull.executor_driver.presentation.ordershistoryheader.OrdersHistoryHeaderViewModelImpl
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewModel
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewModelImpl
import com.cargopull.executor_driver.presentation.phone.PhoneViewModel
import com.cargopull.executor_driver.presentation.phone.PhoneViewModelImpl
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModel
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModelImpl
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListItemsMapper
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModel
import com.cargopull.executor_driver.presentation.preorderslist.PreOrdersListViewModelImpl
import com.cargopull.executor_driver.presentation.reportproblem.ReportProblemViewModel
import com.cargopull.executor_driver.presentation.reportproblem.ReportProblemViewModelImpl
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModel
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModelImpl
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModelImpl
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModel
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModelImpl
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModel
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewModel
import com.cargopull.executor_driver.presentation.upcomingpreorder.UpcomingPreOrderViewModelImpl
import com.cargopull.executor_driver.presentation.upcomingpreordermessage.UpcomingPreOrderMessageViewModel
import com.cargopull.executor_driver.presentation.upcomingpreordermessage.UpcomingPreOrderMessageViewModelImpl
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModel
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModelImpl
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModel
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModelImpl
import com.cargopull.executor_driver.utils.TimeUtils

class PresentationComponent(
        private val backendComponent: BackendComponent,
        private val timeUtils: TimeUtils) {
    private val interactorComponent: InteractorComponent by lazy {
        InteractorComponent(timeUtils, repositoryComponent)
    }
    private val repositoryComponent: RepositoryComponent by lazy {
        RepositoryComponent(backendComponent)
    }
    val announcementViewModel: AnnouncementViewModel by lazy {
        AnnouncementViewModelImpl(
                repositoryComponent.announcementsGateway
        )
    }
    val balanceViewModel: BalanceViewModel by lazy {
        BalanceViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.executorBalanceUseCase
        )
    }
    val currentCostPollingViewModel: CurrentCostPollingViewModel by lazy {
        CurrentCostPollingViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.currentCostPollingUseCase
        )
    }
    val executorStateViewModel: ExecutorStateViewModel by lazy {
        ExecutorStateViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.executorStateUseCase
        )
    }
    val geoLocationViewModel: GeoLocationViewModel by lazy {
        GeoLocationViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.geoLocationUseCase
        )
    }
    val missedOrderViewModel: MissedOrderViewModel by lazy {
        MissedOrderViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.missedOrderUseCase
        )
    }
    val orderCostDetailsViewModel: OrderCostDetailsViewModel by lazy {
        OrderCostDetailsViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.orderCostDetailsUseCase
        )
    }
    val cancelledOrderViewModel: CancelledOrderViewModel by lazy {
        CancelledOrderViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.cancelledOrderMessageUseCase
        )
    }
    val orderViewModel: OrderViewModel by lazy {
        OrderViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.orderUseCase
        )
    }
    val orderRouteViewModel: OrderRouteViewModel by lazy {
        OrderRouteViewModelImpl(
                interactorComponent.orderRouteUseCase
        )
    }
    val preOrderViewModel: PreOrderViewModel by lazy {
        PreOrderViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.preOrderUseCase
        )
    }
    val preOrdersListViewModel: PreOrdersListViewModel by lazy {
        PreOrdersListViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.preOrdersSetUseCase,
                interactorComponent.selectedOrderUseCase,
                PreOrdersListItemsMapper()
        )
    }
    val serverConnectionViewModel: ServerConnectionViewModel by lazy {
        ServerConnectionViewModelImpl(
                interactorComponent.serverConnectionUseCase
        )
    }
    val serverTimeViewModel: ServerTimeViewModel by lazy {
        ServerTimeViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.serverTimeUseCase
        )
    }
    val upcomingPreOrderMessagesViewModel: UpcomingPreOrderMessageViewModel by lazy {
        UpcomingPreOrderMessageViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.upcomingPreOrderMessagesUseCase
        )
    }
    val updateMessageViewModel: UpdateMessageViewModel by lazy {
        UpdateMessageViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.updateMessageUseCase
        )
    }
    val upcomingPreOrderAvailabilityViewModel: UpcomingPreOrderViewModel by lazy {
        UpcomingPreOrderViewModelImpl(
                backendComponent.errorReporter,
                interactorComponent.upcomingPreOrderUseCase
        )
    }

    fun getCallToClientViewModel(fragment: Fragment?): CallToClientViewModel {
        return getViewModelInstance(
                fragment,
                CallToClientViewModelImpl::class.java,
                CallToClientViewModelImpl(
                        interactorComponent.callToClientUseCase
                )
        )
    }

    fun getCallToOperatorViewModel(fragment: Fragment?): CallToOperatorViewModel {
        return getViewModelInstance(
                fragment,
                CallToOperatorViewModelImpl::class.java,
                CallToOperatorViewModelImpl()
        )
    }

    fun getCancelOrderViewModel(fragment: Fragment?): ReportProblemViewModel {
        return getViewModelInstance(
                fragment,
                ReportProblemViewModelImpl::class.java,
                ReportProblemViewModelImpl(backendComponent.errorReporter,
                        interactorComponent.reportProblemUseCase
                )
        )
    }

    fun getChooseVehicleViewModel(fragment: Fragment?): ChooseVehicleViewModel {
        return getViewModelInstance(
                fragment,
                ChooseVehicleViewModelImpl::class.java,
                ChooseVehicleViewModelImpl(backendComponent.errorReporter,
                        interactorComponent.vehicleChoiceUseCase
                )
        )
    }

    fun getSelectedChooseVehicleViewModel(fragment: Fragment?): ChooseVehicleViewModel {
        return getViewModelInstance(
                fragment,
                ChooseVehicleViewModelImpl::class.java,
                ChooseVehicleViewModelImpl(backendComponent.errorReporter,
                        interactorComponent.vehicleChoiceUseCase
                )
        )
    }

    fun getCurrentChooseVehicleViewModel(fragment: Fragment?): ChooseVehicleViewModel {
        return getViewModelInstance(
                fragment,
                ChooseVehicleViewModelImpl::class.java,
                ChooseVehicleViewModelImpl(backendComponent.errorReporter,
                        interactorComponent.currentVehicleChoiceUseCase
                )
        )
    }

    fun getClientOrderConfirmationTimeViewModel(
            fragment: Fragment?): ClientOrderConfirmationTimeViewModel {
        return getViewModelInstance(
                fragment,
                ClientOrderConfirmationTimeViewModelImpl::class.java,
                ClientOrderConfirmationTimeViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateUseCase
                )
        )
    }

    fun getCodeViewModel(fragment: Fragment?): CodeViewModel {
        return getViewModelInstance(
                fragment,
                CodeViewModelImpl::class.java,
                CodeViewModelImpl(
                        interactorComponent.passwordUseCase,
                        timeUtils,
                        backendComponent.eventLogger
                )
        )
    }

    fun getCodeHeaderViewModel(fragment: Fragment?): CodeHeaderViewModel {
        return getViewModelInstance(
                fragment,
                CodeHeaderViewModelImpl::class.java,
                CodeHeaderViewModelImpl(
                        repositoryComponent.loginReceiver
                )
        )
    }

    fun getConfirmOrderPaymentViewModel(fragment: Fragment?): NextExecutorStateViewModel {
        return getViewModelInstance(
                fragment,
                NextExecutorStateViewModelImpl::class.java,
                NextExecutorStateViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.confirmOrderPaymentUseCase
                )
        )
    }

    fun getMapViewModel(fragment: Fragment?): MapViewModel {
        return getViewModelInstance(
                fragment,
                MapViewModelImpl::class.java,
                MapViewModelImpl(
                        interactorComponent.heatMapUseCase
                )
        )
    }

    fun getReportArrivedViewModel(fragment: Fragment?): NextExecutorStateViewModel {
        return getViewModelInstance(
                fragment,
                NextExecutorStateViewModelImpl::class.java,
                NextExecutorStateViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.reportArrivedUseCase
                )
        )
    }

    fun getNextRoutePointViewModel(fragment: Fragment?): NextRoutePointViewModel {
        return getViewModelInstance(
                fragment,
                NextRoutePointViewModelImpl::class.java,
                NextRoutePointViewModelImpl(
                        interactorComponent.orderRouteUseCase
                )
        )
    }

    fun getCompleteOrderViewModel(fragment: Fragment?): NextExecutorStateViewModel {
        return getViewModelInstance(
                fragment,
                NextExecutorStateViewModelImpl::class.java,
                NextExecutorStateViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.completeOrderUseCase
                )
        )
    }

    fun getOnlineButtonViewModel(fragment: Fragment?): OnlineButtonViewModel {
        return getViewModelInstance(
                fragment,
                OnlineButtonViewModelImpl::class.java,
                OnlineButtonViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.vehiclesAndOptionsUseCase
                )
        )
    }

    fun getSelectedOnlineButtonViewModel(fragment: Fragment?): OnlineButtonViewModel {
        return getViewModelInstance(
                fragment,
                OnlineButtonViewModelImpl::class.java,
                OnlineButtonViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.selectedVehiclesAndOptionsUseCase
                )
        )
    }

    fun getOnlineSwitchViewModel(fragment: Fragment?): OnlineSwitchViewModel {
        return getViewModelInstance(
                fragment,
                OnlineSwitchViewModelImpl::class.java,
                OnlineSwitchViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateNotOnlineUseCase,
                        interactorComponent.executorStateUseCase
                )
        )
    }

    fun getExitOnlineSwitchViewModel(fragment: Fragment?): OnlineSwitchViewModel {
        return getViewModelInstance(
                fragment,
                OnlineSwitchViewModelImpl::class.java,
                OnlineSwitchViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateExitUseCase,
                        interactorComponent.executorStateUseCase
                )
        )
    }

    fun getPOrderViewModel(fragment: Fragment?): OrderViewModel {
        return getViewModelInstance(
                fragment,
                OrderViewModelImpl::class.java,
                OrderViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.preOrderUseCase
                )
        )
    }

    fun getRushOrderConfirmationViewModel(fragment: Fragment?): OrderConfirmationViewModel {
        return getViewModelInstance(
                fragment,
                RushOrderConfirmationViewModel::class.java,
                RushOrderConfirmationViewModel(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateUseCase,
                        interactorComponent.orderConfirmationUseCase,
                        timeUtils,
                        backendComponent.eventLogger
                )
        )
    }

    fun getPreOrderConfirmationViewModel(fragment: Fragment?): OrderConfirmationViewModel {
        return getViewModelInstance(
                fragment,
                PreOrderConfirmationViewModel::class.java,
                PreOrderConfirmationViewModel(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateUseCase,
                        interactorComponent.orderConfirmationUseCase,
                        timeUtils,
                        backendComponent.eventLogger
                )
        )
    }

    fun getPreOrderBookingViewModel(fragment: Fragment?): OrderConfirmationViewModel {
        return getViewModelInstance(
                fragment,
                PreOrderBookingViewModel::class.java,
                PreOrderBookingViewModel(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateUseCase,
                        interactorComponent.preOrderBookingUseCase,
                        timeUtils, null
                )
        )
    }

    fun getOrderCostViewModel(fragment: Fragment?): OrderCostViewModel {
        return getViewModelInstance(
                fragment,
                OrderCostViewModelImpl::class.java,
                OrderCostViewModelImpl(backendComponent.errorReporter,
                        interactorComponent.orderCurrentCostUseCase
                )
        )
    }

    fun getOrderTimeViewModel(fragment: Fragment?): OrderTimeViewModel {
        return getViewModelInstance(
                fragment,
                OrderTimeViewModelImpl::class.java,
                OrderTimeViewModelImpl(
                        interactorComponent.orderFulfillmentTimeUseCase
                )
        )
    }

    fun getPhoneViewModel(fragment: Fragment?): PhoneViewModel {
        return getViewModelInstance(
                fragment,
                PhoneViewModelImpl::class.java,
                PhoneViewModelImpl(
                        interactorComponent.loginUseCase
                )
        )
    }

    fun getSelectedVehicleViewModel(fragment: Fragment?): SelectedVehicleViewModel {
        return getViewModelInstance(
                fragment,
                SelectedVehicleViewModelImpl::class.java,
                SelectedVehicleViewModelImpl(
                        interactorComponent.selectedVehicleUseCase
                )
        )
    }

    fun getSmsButtonViewModel(fragment: Fragment?): SmsButtonViewModel {
        return getViewModelInstance(
                fragment,
                SmsButtonViewModelImpl::class.java,
                SmsButtonViewModelImpl(
                        interactorComponent.smsUseCase
                )
        )
    }

    fun getVehicleOptionsViewModel(fragment: Fragment?): VehicleOptionsViewModel {
        return getViewModelInstance(
                fragment,
                VehicleOptionsViewModelImpl::class.java,
                VehicleOptionsViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.vehicleOptionsUseCase,
                        interactorComponent.servicesUseCase)
        )
    }

    fun getSelectedVehicleOptionsViewModel(fragment: Fragment?): VehicleOptionsViewModel {
        return getViewModelInstance(
                fragment,
                VehicleOptionsViewModelImpl::class.java,
                VehicleOptionsViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.currentVehicleOptionsUseCase,
                        interactorComponent.servicesUseCase)
        )
    }

    fun getStartOrderViewModel(fragment: Fragment?): NextExecutorStateViewModel {
        return getViewModelInstance(
                fragment,
                NextExecutorStateViewModelImpl::class.java,
                NextExecutorStateViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.startOrderUseCase
                )
        )
    }

    fun getSelectedPreOrderViewModel(fragment: Fragment?): OrderViewModel {
        return getViewModelInstance(
                fragment,
                OrderViewModelImpl::class.java,
                OrderViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.selectedPreOrderUseCase
                )
        )
    }

    fun getSelectedPreOrderConfirmationViewModel(
            fragment: Fragment?): OrderConfirmationViewModel {
        return getViewModelInstance(
                fragment,
                SelectedPreOrderConfirmationViewModel::class.java,
                SelectedPreOrderConfirmationViewModel(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateUseCase,
                        interactorComponent.selectedPreOrderConfirmationUseCase,
                        timeUtils, null
                )
        )
    }

    fun getUpcomingPreOrderViewModel(fragment: Fragment?): OrderViewModel {
        return getViewModelInstance(
                fragment,
                OrderViewModelImpl::class.java,
                OrderViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.upcomingPreOrderUseCase
                )
        )
    }

    fun getUpcomingPreOrderConfirmationViewModel(
            fragment: Fragment?): OrderConfirmationViewModel {
        return getViewModelInstance(
                fragment,
                UpcomingPreOrderConfirmationViewModel::class.java,
                UpcomingPreOrderConfirmationViewModel(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateUseCase,
                        interactorComponent.upcomingPreOrderConfirmationUseCase,
                        timeUtils, null
                )
        )
    }

    fun getGeoLocationStateViewModel(
            appCompatActivity: AppCompatActivity?): GeoLocationStateViewModel {
        return getViewModelInstance(
                appCompatActivity,
                GeoLocationStateViewModelImpl::class.java,
                GeoLocationStateViewModelImpl(
                        backendComponent.eventLogger,
                        backendComponent.locationManager,
                        timeUtils,
                        repositoryComponent.geoLocationStateGateway
                )
        )
    }

    fun getGeoLocationStateViewModel(
            fragment: Fragment?): GeoLocationStateViewModel {
        return getViewModelInstance(
                fragment,
                GeoLocationStateViewModelImpl::class.java,
                GeoLocationStateViewModelImpl(
                        backendComponent.eventLogger,
                        backendComponent.locationManager,
                        timeUtils,
                        repositoryComponent.geoLocationStateGateway
                )
        )
    }

    fun getMovingToClientTimerViewModel(fragment: Fragment?): MovingToClientTimerViewModel {
        return getViewModelInstance(
                fragment,
                MovingToClientTimerViewModelImpl::class.java,
                MovingToClientTimerViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.orderUseCase,
                        timeUtils
                )
        )
    }

    fun getOrdersHistoryHeaderViewModel(fragment: Fragment?,
                                        offset: Int): OrdersHistoryHeaderViewModel {
        return getViewModelInstance(
                fragment,
                OrdersHistoryHeaderViewModelImpl::class.java,
                OrdersHistoryHeaderViewModelImpl(
                        offset,
                        backendComponent.errorReporter,
                        timeUtils,
                        repositoryComponent.ordersHistorySummaryGateway
                )
        )
    }

    fun getMenuViewModel(fragment: Fragment?): MenuViewModel {
        return getViewModelInstance(
                fragment,
                MenuViewModelImpl::class.java,
                MenuViewModelImpl(
                        backendComponent.errorReporter,
                        interactorComponent.executorStateUseCase
                )
        )
    }

    private fun <V : ViewModel> getViewModelInstance(
            fragment: Fragment?,
            vClass: Class<V>,
            viewModel: V): V {
        if (fragment == null) {
            throw NullPointerException("Фрагмент не должен быть null")
        }
        return ViewModelProviders.of(
                fragment,
                ViewModelFactory(viewModel)
        ).get(vClass)
    }

    private fun <V : ViewModel> getViewModelInstance(
            appCompatActivity: AppCompatActivity?,
            vClass: Class<V>,
            viewModel: V): V {
        if (appCompatActivity == null) {
            throw NullPointerException("Активити не должно быть null")
        }
        return ViewModelProviders.of(
                appCompatActivity,
                ViewModelFactory(viewModel)
        ).get(vClass)
    }
}
