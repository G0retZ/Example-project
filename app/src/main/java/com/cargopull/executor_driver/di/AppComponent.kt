package com.cargopull.executor_driver.di

import android.content.Context
import com.cargopull.executor_driver.application.*
import com.cargopull.executor_driver.backend.settings.AppSettingsService
import com.cargopull.executor_driver.utils.Consumer
import com.cargopull.executor_driver.utils.Releasable
import com.cargopull.executor_driver.utils.TimeUtils
import com.cargopull.executor_driver.utils.TimeUtilsImpl
import com.cargopull.executor_driver.view.*
import com.cargopull.executor_driver.view.auth.LoginFragment
import com.cargopull.executor_driver.view.auth.PasswordFragment

class AppComponent(private val appContext: Context, private val backendComponent: BackendComponent) : Releasable {

    private val timeUtils: TimeUtils by lazy {
        TimeUtilsImpl()
    }
    private val presentationComponent: PresentationComponent by lazy {
        PresentationComponent(backendComponent, timeUtils)
    }
    private val navigationMapper: NavigationMapper by lazy {
        NavigationMapperImpl(backendComponent.ringTonePlayer, backendComponent.shakeItPlayer)
    }

    fun inject(appSettingsServiceConsumer: Consumer<AppSettingsService>) {
        appSettingsServiceConsumer.accept(
                backendComponent.appSettingsService
        )
    }

    fun inject(mainApplication: MainApplication) {
        mainApplication.setServerConnectionViewModel(
                presentationComponent.serverConnectionViewModel
        )
        mainApplication.setBalanceViewModel(
                presentationComponent.balanceViewModel
        )
        mainApplication.setExecutorStateViewModel(
                presentationComponent.executorStateViewModel
        )
        mainApplication.setOrderViewModel(
                presentationComponent.orderViewModel
        )
        mainApplication.setOrderRouteViewModel(
                presentationComponent.orderRouteViewModel
        )
        mainApplication.setPreOrderViewModel(
                presentationComponent.preOrderViewModel
        )
        mainApplication.setUpcomingPreOrderViewModel(
                presentationComponent.upcomingPreOrderAvailabilityViewModel
        )
        mainApplication.setPreOrdersListViewModel(
                presentationComponent.preOrdersListViewModel
        )
        mainApplication.setOrderCostDetailsViewModel(
                presentationComponent.orderCostDetailsViewModel
        )
        mainApplication.setGeoLocationViewModel(
                presentationComponent.geoLocationViewModel
        )
        mainApplication.setMissedOrderViewModel(
                presentationComponent.missedOrderViewModel
        )
        mainApplication.setUpcomingPreOrderMessageViewModel(
                presentationComponent.upcomingPreOrderMessagesViewModel
        )
        mainApplication.setCancelledOrderViewModel(
                presentationComponent.cancelledOrderViewModel
        )
        mainApplication.setCurrentCostPollingViewModel(
                presentationComponent.currentCostPollingViewModel
        )
        mainApplication.setServerTimeViewModel(
                presentationComponent.serverTimeViewModel
        )
        mainApplication.setNavigationMapper(
                navigationMapper
        )
    }

    fun inject(baseActivity: BaseActivity) {
        baseActivity.setAppSettingsService(
                backendComponent.appSettingsService
        )
        baseActivity.setGeoLocationStateViewModel(
                presentationComponent.getGeoLocationStateViewModel(baseActivity)
        )
        baseActivity.setExecutorStateViewModel(
                presentationComponent.executorStateViewModel
        )
        baseActivity.setUpdateMessageViewModel(
                presentationComponent.updateMessageViewModel
        )
        baseActivity.setAnnouncementViewModel(
                presentationComponent.announcementViewModel
        )
        baseActivity.setServerConnectionViewModel(
                presentationComponent.serverConnectionViewModel
        )
        baseActivity.setServerTimeViewModel(
                presentationComponent.serverTimeViewModel
        )
    }

    fun inject(movingToClientActivity: MovingToClientActivity) {
        movingToClientActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(waitingForClientActivity: WaitingForClientActivity) {
        waitingForClientActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(orderFulfillmentActivity: OrderFulfillmentActivity) {
        orderFulfillmentActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(orderCostDetailsActivity: OrderCostDetailsActivity) {
        orderCostDetailsActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(menuActivity: MenuActivity) {
        menuActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(onlineMenuActivity: OnlineMenuActivity) {
        onlineMenuActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(nightModeActivity: NightModeActivity) {
        nightModeActivity.setAppSettingsService(
                backendComponent.appSettingsService
        )
    }

    fun inject(onlineActivity: OnlineActivity) {
        onlineActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(preOrdersActivity: PreOrdersActivity) {
        preOrdersActivity.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(passwordActivity: PasswordActivity) {
        passwordActivity.setApiService(
                backendComponent.apiService
        )
        passwordActivity.setErrorReporter(
                backendComponent.errorReporter
        )
    }

    fun inject(fcmService: FcmService) {
        fcmService.fcmObserver = backendComponent.fcmReceiver
        fcmService.apiService = backendComponent.apiService
        fcmService.shakeItPlayer = backendComponent.shakeItPlayer
        fcmService.ringTonePlayer = backendComponent.ringTonePlayer
    }

    fun inject(loginFragment: LoginFragment) {
        loginFragment.setAppSettings(
                backendComponent.appSettingsService
        )
        loginFragment.setPhoneViewModel(
                presentationComponent.getPhoneViewModel(loginFragment)
        )
    }

    fun inject(passwordFragment: PasswordFragment) {
        passwordFragment.setSmsButtonViewModel(
                presentationComponent.getSmsButtonViewModel(passwordFragment)
        )
        passwordFragment.setCodeHeaderViewModel(
                presentationComponent.getCodeHeaderViewModel(passwordFragment)
        )
        passwordFragment.setCodeViewModel(
                presentationComponent.getCodeViewModel(passwordFragment)
        )
        passwordFragment.setShakeItPlayer(
                backendComponent.shakeItPlayer
        )
    }

    fun inject(mapFragment: MapFragment) {
        mapFragment.setMapViewModel(
                presentationComponent.getMapViewModel(mapFragment)
        )
        mapFragment.setGeoLocationViewModel(
                presentationComponent.geoLocationViewModel
        )
    }

    fun inject(onlineFragment: OnlineFragment) {
        onlineFragment.setOnlineSwitchViewModel(
                presentationComponent.getOnlineSwitchViewModel(onlineFragment)
        )
        onlineFragment.setOnlineButtonViewModel(
                presentationComponent.getSelectedOnlineButtonViewModel(onlineFragment)
        )
    }

    fun inject(goOnlineFragment: GoOnlineFragment) {
        goOnlineFragment.setOnlineButtonViewModel(
                presentationComponent.getOnlineButtonViewModel(goOnlineFragment)
        )
    }

    fun inject(chooseVehicleFragment: ChooseVehicleFragment) {
        chooseVehicleFragment.setChooseVehicleViewModel(
                presentationComponent.getChooseVehicleViewModel(chooseVehicleFragment)
        )
    }

    fun inject(vehicleOptionsFragment: VehicleOptionsFragment) {
        vehicleOptionsFragment.setVehicleOptionsViewModel(
                presentationComponent.getVehicleOptionsViewModel(vehicleOptionsFragment)
        )
    }

    fun inject(vehicleOptionsFragment: SelectedVehicleOptionsFragment) {
        vehicleOptionsFragment.setVehicleOptionsViewModel(
                presentationComponent.getSelectedVehicleOptionsViewModel(vehicleOptionsFragment)
        )
    }

    fun inject(selectedVehicleFragment: SelectedVehicleFragment) {
        selectedVehicleFragment.setSelectedVehicleViewModel(
                presentationComponent.getSelectedVehicleViewModel(selectedVehicleFragment)
        )
        selectedVehicleFragment.setChooseVehicleViewModel(
                presentationComponent.getSelectedChooseVehicleViewModel(selectedVehicleFragment)
        )
    }

    fun inject(currentVehicleFragment: CurrentVehicleFragment) {
        currentVehicleFragment.setSelectedVehicleViewModel(
                presentationComponent.getSelectedVehicleViewModel(currentVehicleFragment)
        )
        currentVehicleFragment.setChooseVehicleViewModel(
                presentationComponent.getCurrentChooseVehicleViewModel(currentVehicleFragment)
        )
    }

    fun inject(driverOrderConfirmationFragment: DriverOrderConfirmationFragment) {
        driverOrderConfirmationFragment.setOrderConfirmationViewModel(
                presentationComponent.getRushOrderConfirmationViewModel(driverOrderConfirmationFragment)
        )
        driverOrderConfirmationFragment.setOrderViewModel(
                presentationComponent.orderViewModel
        )
    }

    fun inject(clientOrderConfirmationFragment: ClientOrderConfirmationFragment) {
        clientOrderConfirmationFragment.setOrderViewModel(
                presentationComponent.orderViewModel
        )
    }

    fun inject(clientOrderConfirmationTimeFragment: ClientOrderConfirmationTimeFragment) {
        clientOrderConfirmationTimeFragment.setClientOrderConfirmationTimeViewModel(
                presentationComponent.getClientOrderConfirmationTimeViewModel(
                        clientOrderConfirmationTimeFragment
                )
        )
    }

    fun inject(movingToClientFragment: MovingToClientFragment) {
        movingToClientFragment.setOrderViewModel(
                presentationComponent.orderViewModel
        )
        movingToClientFragment.setMovingToClientTimerViewModel(
                presentationComponent.getMovingToClientTimerViewModel(movingToClientFragment)
        )
        movingToClientFragment.setReportArrivedViewModel(
                presentationComponent.getReportArrivedViewModel(movingToClientFragment)
        )
        movingToClientFragment.setShakeItPlayer(
                backendComponent.shakeItPlayer
        )
    }

    fun inject(movingToClientActionsDialogFragment: MovingToClientActionsDialogFragment) {
        movingToClientActionsDialogFragment.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(actionOrderDetailsFragment: ActionOrderDetailsFragment) {
        actionOrderDetailsFragment.setOrderViewModel(
                presentationComponent.orderViewModel
        )
    }

    fun inject(movingToClientRouteFragment: MovingToClientRouteFragment) {
        movingToClientRouteFragment.setOrderRouteViewModel(
                presentationComponent.orderRouteViewModel
        )
    }

    fun inject(waitingForClientFragment: WaitingForClientFragment) {
        waitingForClientFragment.setStartOrderViewModel(
                presentationComponent.getStartOrderViewModel(waitingForClientFragment)
        )
        waitingForClientFragment.setOrderViewModel(
                presentationComponent.orderViewModel
        )
        waitingForClientFragment.setShakeItPlayer(
                backendComponent.shakeItPlayer
        )
    }

    fun inject(waitingForClientActionsDialogFragment: WaitingForClientActionsDialogFragment) {
        waitingForClientActionsDialogFragment.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(waitingForClientRouteFragment: WaitingForClientRouteFragment) {
        waitingForClientRouteFragment.setOrderRouteViewModel(
                presentationComponent.orderRouteViewModel
        )
    }

    fun inject(orderFulfillmentFragment: OrderFulfillmentFragment) {
        orderFulfillmentFragment.setOrderTimeViewModel(
                presentationComponent.getOrderTimeViewModel(orderFulfillmentFragment)
        )
        orderFulfillmentFragment.setOrderCostViewModel(
                presentationComponent.getOrderCostViewModel(orderFulfillmentFragment)
        )
        orderFulfillmentFragment.setNextRoutePointViewModel(
                presentationComponent.getNextRoutePointViewModel(orderFulfillmentFragment)
        )
        orderFulfillmentFragment.setCompleteOrderViewModel(
                presentationComponent.getCompleteOrderViewModel(orderFulfillmentFragment)
        )
        orderFulfillmentFragment.setOrderRouteViewModel(
                presentationComponent.orderRouteViewModel
        )
        orderFulfillmentFragment.setShakeItPlayer(
                backendComponent.shakeItPlayer
        )
    }

    fun inject(orderFulfillmentActionsDialogFragment: OrderFulfillmentActionsDialogFragment) {
        orderFulfillmentActionsDialogFragment.setEventLogger(
                backendComponent.eventLogger
        )
        orderFulfillmentActionsDialogFragment.setNextRoutePointViewModel(
                presentationComponent.getNextRoutePointViewModel(orderFulfillmentActionsDialogFragment)
        )
        orderFulfillmentActionsDialogFragment.setCompleteOrderViewModel(
                presentationComponent.getCompleteOrderViewModel(orderFulfillmentActionsDialogFragment)
        )
    }

    fun inject(orderRouteFragment: OrderRouteFragment) {
        orderRouteFragment.setOrderRouteViewModel(
                presentationComponent.orderRouteViewModel
        )
    }

    fun inject(callToClientFragment: CallToClientFragment) {
        callToClientFragment.setCallToClientViewModel(
                presentationComponent.getCallToClientViewModel(callToClientFragment)
        )
    }

    fun inject(callToOperatorFragment: CallToOperatorFragment) {
        callToOperatorFragment.setCallToOperatorViewModel(
                presentationComponent.getCallToOperatorViewModel(callToOperatorFragment)
        )
    }

    fun inject(cancelOrderDialogFragment: ReportProblemDialogFragment) {
        cancelOrderDialogFragment.setReportProblemViewModel(
                presentationComponent.getCancelOrderViewModel(cancelOrderDialogFragment)
        )
    }

    fun inject(balanceFragment: BalanceFragment) {
        balanceFragment.setBalanceViewModel(
                presentationComponent.balanceViewModel
        )
    }

    fun inject(balanceSummaryFragment: BalanceSummaryFragment) {
        balanceSummaryFragment.setBalanceViewModel(
                presentationComponent.balanceViewModel
        )
    }

    fun inject(menuFragment: MenuFragment) {
        menuFragment.setAppSettingsService(
                backendComponent.appSettingsService
        )
        menuFragment.setBalanceViewModel(
                presentationComponent.balanceViewModel
        )
        menuFragment.setOnlineSwitchViewModel(
                presentationComponent.getExitOnlineSwitchViewModel(menuFragment)
        )
        menuFragment.setPreOrdersListViewModel(
                presentationComponent.preOrdersListViewModel
        )
        menuFragment.setMenuViewModel(
                presentationComponent.getMenuViewModel(menuFragment)
        )
        menuFragment.setOnlineButtonViewModel(
                presentationComponent.getSelectedOnlineButtonViewModel(menuFragment)
        )
    }

    fun inject(serverConnectionFragment: ServerConnectionFragment) {
        serverConnectionFragment.setServerConnectionViewModel(
                presentationComponent.serverConnectionViewModel
        )
    }

    fun inject(orderCostDetailsFragment: OrderCostDetailsFragment) {
        orderCostDetailsFragment.setOrderCostDetailsViewModel(
                presentationComponent.orderCostDetailsViewModel
        )
        orderCostDetailsFragment.setConfirmOrderPaymentViewModel(
                presentationComponent.getConfirmOrderPaymentViewModel(orderCostDetailsFragment)
        )
        orderCostDetailsFragment.setShakeItPlayer(
                backendComponent.shakeItPlayer
        )
    }

    fun inject(orderCostDetailsRouteFragment: OrderCostDetailsRouteFragment) {
        orderCostDetailsRouteFragment.setOrderRouteViewModel(
                presentationComponent.orderRouteViewModel
        )
    }

    fun inject(orderCostDetailsActionsDialogFragment: OrderCostDetailsActionsDialogFragment) {
        orderCostDetailsActionsDialogFragment.setEventLogger(
                backendComponent.eventLogger
        )
    }

    fun inject(profileFragment: ProfileFragment) {
        profileFragment.setAppSettings(
                backendComponent.appSettingsService
        )
    }

    fun inject(driverPreOrderBookingFragment: DriverPreOrderBookingFragment) {
        driverPreOrderBookingFragment.setOrderConfirmationViewModel(
                presentationComponent.getPreOrderBookingViewModel(driverPreOrderBookingFragment)
        )
    }

    fun inject(preOrdersFragment: PreOrdersFragment) {
        preOrdersFragment.setPreOrdersListViewModel(
                presentationComponent.preOrdersListViewModel
        )
    }

    fun inject(preOrderFragment: PreOrderFragment) {
        preOrderFragment.setOrderViewModel(
                presentationComponent.getPOrderViewModel(preOrderFragment)
        )
    }

    fun inject(selectedPreOrderFragment: SelectedPreOrderFragment) {
        selectedPreOrderFragment.setOrderViewModel(
                presentationComponent.getSelectedPreOrderViewModel(selectedPreOrderFragment)
        )
    }

    fun inject(selectedPreOrderConfirmationFragment: SelectedPreOrderConfirmationFragment) {
        selectedPreOrderConfirmationFragment.setOrderConfirmationViewModel(
                presentationComponent
                        .getSelectedPreOrderConfirmationViewModel(selectedPreOrderConfirmationFragment)
        )
    }

    fun inject(newPreOrderFragment: NewPreOrderFragment) {
        newPreOrderFragment.setPreOrderViewModel(
                presentationComponent.preOrderViewModel
        )
    }

    fun inject(newPreOrderButtonFragment: NewPreOrderButtonFragment) {
        newPreOrderButtonFragment.setPreOrderViewModel(
                presentationComponent.preOrderViewModel
        )
    }

    fun inject(driverPreOrderConfirmationFragment: DriverPreOrderConfirmationFragment) {
        driverPreOrderConfirmationFragment.setOrderConfirmationViewModel(
                presentationComponent.getPreOrderConfirmationViewModel(driverPreOrderConfirmationFragment)
        )
        driverPreOrderConfirmationFragment.setOrderViewModel(
                presentationComponent.orderViewModel
        )
    }

    fun inject(preOrderConfirmationFragment: PreOrderConfirmationFragment) {
        preOrderConfirmationFragment.setOrderViewModel(
                presentationComponent.orderViewModel
        )
    }

    fun inject(upcomingPreOrderFragment: UpcomingPreOrderFragment) {
        upcomingPreOrderFragment.setOrderViewModel(
                presentationComponent.getUpcomingPreOrderViewModel(upcomingPreOrderFragment)
        )
    }

    fun inject(upcomingPreOrderConfirmationFragment: UpcomingPreOrderConfirmationFragment) {
        upcomingPreOrderConfirmationFragment.setOrderConfirmationViewModel(
                presentationComponent
                        .getUpcomingPreOrderConfirmationViewModel(upcomingPreOrderConfirmationFragment)
        )
    }

    fun inject(upcomingPreOrderNotificationFragment: UpcomingPreOrderNotificationFragment) {
        upcomingPreOrderNotificationFragment.setUpcomingPreOrderViewModel(
                presentationComponent
                        .getUpcomingPreOrderViewModel(upcomingPreOrderNotificationFragment)
        )
        upcomingPreOrderNotificationFragment.setUpcomingPreOrderNotificationViewModel(
                presentationComponent.upcomingPreOrderAvailabilityViewModel
        )
    }

    fun inject(geoEngagementDialogFragment: GeoEngagementDialogFragment) {
        geoEngagementDialogFragment.setGeoLocationStateViewModel(
                presentationComponent.getGeoLocationStateViewModel(
                        geoEngagementDialogFragment
                )
        )
    }

    fun inject(ordersHistoryHeaderFragment: OrdersHistoryHeaderFragment, offset: Int) {
        ordersHistoryHeaderFragment.setOrdersHistoryHeaderViewModel(
                presentationComponent
                        .getOrdersHistoryHeaderViewModel(ordersHistoryHeaderFragment, offset)
        )
    }

    override fun release() {
        backendComponent.release()
    }
}
