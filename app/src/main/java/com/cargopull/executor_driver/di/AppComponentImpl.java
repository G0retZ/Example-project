package com.cargopull.executor_driver.di;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.FcmService;
import com.cargopull.executor_driver.application.MainApplication;
import com.cargopull.executor_driver.application.MenuActivity;
import com.cargopull.executor_driver.application.MovingToClientActivity;
import com.cargopull.executor_driver.application.NavigationMapper;
import com.cargopull.executor_driver.application.NavigationMapperImpl;
import com.cargopull.executor_driver.application.NightModeActivity;
import com.cargopull.executor_driver.application.OnlineActivity;
import com.cargopull.executor_driver.application.OnlineMenuActivity;
import com.cargopull.executor_driver.application.OrderCostDetailsActivity;
import com.cargopull.executor_driver.application.OrderFulfillmentActivity;
import com.cargopull.executor_driver.application.PasswordActivity;
import com.cargopull.executor_driver.application.PreOrdersActivity;
import com.cargopull.executor_driver.application.WaitingForClientActivity;
import com.cargopull.executor_driver.backend.ringtone.SingleRingTonePlayer;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.vibro.NewPatternMapper;
import com.cargopull.executor_driver.backend.vibro.OldPatternMapper;
import com.cargopull.executor_driver.backend.vibro.OldSingleShakePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.backend.vibro.SingleShakePlayer;
import com.cargopull.executor_driver.gateway.SmsCodeMapper;
import com.cargopull.executor_driver.utils.Consumer;
import com.cargopull.executor_driver.utils.TimeUtils;
import com.cargopull.executor_driver.utils.TimeUtilsImpl;
import com.cargopull.executor_driver.view.ActionOrderDetailsFragment;
import com.cargopull.executor_driver.view.BalanceFragment;
import com.cargopull.executor_driver.view.BalanceSummaryFragment;
import com.cargopull.executor_driver.view.CallToClientFragment;
import com.cargopull.executor_driver.view.CallToOperatorFragment;
import com.cargopull.executor_driver.view.CancelOrderDialogFragment;
import com.cargopull.executor_driver.view.ChooseVehicleFragment;
import com.cargopull.executor_driver.view.ClientOrderConfirmationFragment;
import com.cargopull.executor_driver.view.ClientOrderConfirmationTimeFragment;
import com.cargopull.executor_driver.view.DriverOrderConfirmationFragment;
import com.cargopull.executor_driver.view.DriverPreOrderBookingFragment;
import com.cargopull.executor_driver.view.DriverPreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.GoOnlineFragment;
import com.cargopull.executor_driver.view.MapFragment;
import com.cargopull.executor_driver.view.MenuFragment;
import com.cargopull.executor_driver.view.MovingToClientActionsDialogFragment;
import com.cargopull.executor_driver.view.MovingToClientFragment;
import com.cargopull.executor_driver.view.MovingToClientRouteFragment;
import com.cargopull.executor_driver.view.NewPreOrderButtonFragment;
import com.cargopull.executor_driver.view.NewPreOrderFragment;
import com.cargopull.executor_driver.view.OnlineFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsActionsDialogFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsRouteFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentActionsDialogFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentFragment;
import com.cargopull.executor_driver.view.OrderRouteFragment;
import com.cargopull.executor_driver.view.PreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.PreOrderFragment;
import com.cargopull.executor_driver.view.PreOrdersFragment;
import com.cargopull.executor_driver.view.ProfileFragment;
import com.cargopull.executor_driver.view.SelectedPreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.SelectedPreOrderFragment;
import com.cargopull.executor_driver.view.SelectedVehicleFragment;
import com.cargopull.executor_driver.view.SelectedVehicleOptionsFragment;
import com.cargopull.executor_driver.view.ServerConnectionFragment;
import com.cargopull.executor_driver.view.ServicesFragment;
import com.cargopull.executor_driver.view.UpcomingPreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.UpcomingPreOrderFragment;
import com.cargopull.executor_driver.view.UpcomingPreOrderNotificationFragment;
import com.cargopull.executor_driver.view.VehicleOptionsFragment;
import com.cargopull.executor_driver.view.WaitingForClientActionsDialogFragment;
import com.cargopull.executor_driver.view.WaitingForClientFragment;
import com.cargopull.executor_driver.view.WaitingForClientRouteFragment;
import com.cargopull.executor_driver.view.auth.LoginFragment;
import com.cargopull.executor_driver.view.auth.PasswordFragment;
import com.cargopull.executor_driver.view.auth.SmsReceiver;

public class AppComponentImpl implements AppComponent {

  @NonNull
  private final Context appContext;
  @Nullable
  private TimeUtils timeUtils;
  @Nullable
  private PresentationComponent presentationComponent;
  @Nullable
  private BackendComponent backendComponent;
  @Nullable
  private NavigationMapper navigationMapper;
  @Nullable
  private SingleRingTonePlayer singleRingTonePlayer;
  @Nullable
  private ShakeItPlayer shakeItPlayer;

  public AppComponentImpl(@NonNull Context appContext) {
    this.appContext = appContext.getApplicationContext();
  }

  @Override
  public void inject(Consumer<AppSettingsService> appSettingsServiceConsumer) {
    appSettingsServiceConsumer.accept(
        getBackendComponent().getAppSettingsService()
    );
  }

  @Override
  public void inject(MainApplication mainApplication) {
    mainApplication.setRingTonePlayer(
        getSingleRingTonePlayer()
    );
    mainApplication.setShakeItPlayer(
        getShakeItPlayer()
    );
    mainApplication.setServerConnectionViewModel(
        getPresentationComponent().getServerConnectionViewModel()
    );
    mainApplication.setCancelOrderReasonsViewModel(
        getPresentationComponent().getCancelOrderReasonsViewModel()
    );
    mainApplication.setBalanceViewModel(
        getPresentationComponent().getBalanceViewModel()
    );
    mainApplication.setExecutorStateViewModel(
        getPresentationComponent().getExecutorStateViewModel()
    );
    mainApplication.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
    mainApplication.setPreOrderViewModel(
        getPresentationComponent().getPreOrderViewModel()
    );
    mainApplication.setUpcomingPreOrderViewModel(
        getPresentationComponent().getUpcomingPreOrderAvailabilityViewModel()
    );
    mainApplication.setPreOrdersListViewModel(
        getPresentationComponent().getPreOrdersListViewModel()
    );
    mainApplication.setOrderCostDetailsViewModel(
        getPresentationComponent().getOrderCostDetailsViewModel()
    );
    mainApplication.setGeoLocationViewModel(
        getPresentationComponent().getGeoLocationViewModel()
    );
    mainApplication.setMissedOrderViewModel(
        getPresentationComponent().getMissedOrderViewModel()
    );
    mainApplication.setUpcomingPreOrderMessageViewModel(
        getPresentationComponent().getUpcomingPreOrderMessagesViewModel()
    );
    mainApplication.setCancelledOrderViewModel(
        getPresentationComponent().getCancelledOrderViewModel()
    );
    mainApplication.setCurrentCostPollingViewModel(
        getPresentationComponent().getCurrentCostPollingViewModel()
    );
    mainApplication.setServerTimeViewModel(
        getPresentationComponent().getServerTimeViewModel()
    );
    mainApplication.setNavigationMapper(
        getNavigationMapper()
    );
  }

  @Override
  public void inject(BaseActivity baseActivity) {
    baseActivity.setAppSettingsService(
        getBackendComponent().getAppSettingsService()
    );
    baseActivity.setGeoLocationStateViewModel(
        getPresentationComponent().getGeoLocationStateViewModel(baseActivity)
    );
    baseActivity.setExecutorStateViewModel(
        getPresentationComponent().getExecutorStateViewModel()
    );
    baseActivity.setUpdateMessageViewModel(
        getPresentationComponent().getUpdateMessageViewModel()
    );
    baseActivity.setAnnouncementViewModel(
        getPresentationComponent().getAnnouncementViewModel()
    );
    baseActivity.setServerConnectionViewModel(
        getPresentationComponent().getServerConnectionViewModel()
    );
    baseActivity.setServerTimeViewModel(
        getPresentationComponent().getServerTimeViewModel()
    );
  }

  @Override
  public void inject(MovingToClientActivity movingToClientActivity) {
    movingToClientActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(WaitingForClientActivity waitingForClientActivity) {
    waitingForClientActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(OrderFulfillmentActivity orderFulfillmentActivity) {
    orderFulfillmentActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(OrderCostDetailsActivity orderCostDetailsActivity) {
    orderCostDetailsActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(MenuActivity menuActivity) {
    menuActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(OnlineMenuActivity onlineMenuActivity) {
    onlineMenuActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(NightModeActivity nightModeActivity) {
    nightModeActivity.setAppSettingsService(
        getBackendComponent().getAppSettingsService()
    );
  }

  @Override
  public void inject(OnlineActivity onlineActivity) {
    onlineActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(PreOrdersActivity preOrdersActivity) {
    preOrdersActivity.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(PasswordActivity passwordActivity) {
    passwordActivity.setApiService(
        getBackendComponent().getApiService()
    );
    passwordActivity.setErrorReporter(
        getBackendComponent().getErrorReporter()
    );
  }

  @Override
  public void inject(FcmService fcmService) {
    fcmService.setAnnouncementViewModel(
        getPresentationComponent().getAnnouncementViewModel()
    );
    fcmService.setApiService(
        getBackendComponent().getApiService()
    );
  }

  @Override
  public void inject(LoginFragment loginFragment) {
    loginFragment.setAppSettings(
        getBackendComponent().getAppSettingsService()
    );
    loginFragment.setPhoneViewModel(
        getPresentationComponent().getPhoneViewModel(loginFragment)
    );
  }

  @Override
  public void inject(PasswordFragment passwordFragment) {
    passwordFragment.setSmsButtonViewModel(
        getPresentationComponent().getSmsButtonViewModel(passwordFragment)
    );
    passwordFragment.setCodeHeaderViewModel(
        getPresentationComponent().getCodeHeaderViewModel(passwordFragment)
    );
    passwordFragment.setCodeViewModel(
        getPresentationComponent().getCodeViewModel(passwordFragment)
    );
    passwordFragment.setSmsReceiver(
        new SmsReceiver(
            new SmsCodeMapper()
        )
    );
  }

  @Override
  public void inject(MapFragment mapFragment) {
    mapFragment.setMapViewModel(
        getPresentationComponent().getMapViewModel(mapFragment)
    );
    mapFragment.setGeoLocationViewModel(
        getPresentationComponent().getGeoLocationViewModel()
    );
  }

  @Override
  public void inject(OnlineFragment onlineFragment) {
    onlineFragment.setOnlineSwitchViewModel(
        getPresentationComponent().getOnlineSwitchViewModel(onlineFragment)
    );
    onlineFragment.setOnlineButtonViewModel(
        getPresentationComponent().getSelectedOnlineButtonViewModel(onlineFragment)
    );
  }

  @Override
  public void inject(GoOnlineFragment goOnlineFragment) {
    goOnlineFragment.setOnlineButtonViewModel(
        getPresentationComponent().getOnlineButtonViewModel(goOnlineFragment)
    );
  }

  @Override
  public void inject(ChooseVehicleFragment chooseVehicleFragment) {
    chooseVehicleFragment.setChooseVehicleViewModel(
        getPresentationComponent().getChooseVehicleViewModel(chooseVehicleFragment)
    );
  }

  @Override
  public void inject(VehicleOptionsFragment vehicleOptionsFragment) {
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        getPresentationComponent().getVehicleOptionsViewModel(vehicleOptionsFragment)
    );
  }

  @Override
  public void inject(SelectedVehicleOptionsFragment vehicleOptionsFragment) {
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        getPresentationComponent().getSelectedVehicleOptionsViewModel(vehicleOptionsFragment)
    );
  }

  @Override
  public void inject(SelectedVehicleFragment selectedVehicleFragment) {
    selectedVehicleFragment.setSelectedVehicleViewModel(
        getPresentationComponent().getSelectedVehicleViewModel(selectedVehicleFragment)
    );
    selectedVehicleFragment.setChooseVehicleViewModel(
        getPresentationComponent().getCurrentChooseVehicleViewModel(selectedVehicleFragment)
    );
  }

  @Override
  public void inject(ServicesFragment servicesFragment) {
    servicesFragment.setServicesSliderViewModel(
        getPresentationComponent().getServicesSliderViewModel()
    );
    servicesFragment.setServicesViewModel(
        getPresentationComponent().getServicesViewModel(servicesFragment)
    );
  }

  @Override
  public void inject(DriverOrderConfirmationFragment driverOrderConfirmationFragment) {
    driverOrderConfirmationFragment.setOrderConfirmationViewModel(
        getPresentationComponent().getOrderConfirmationViewModel(driverOrderConfirmationFragment)
    );
    driverOrderConfirmationFragment.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
  }

  @Override
  public void inject(ClientOrderConfirmationFragment clientOrderConfirmationFragment) {
    clientOrderConfirmationFragment.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
  }

  @Override
  public void inject(ClientOrderConfirmationTimeFragment clientOrderConfirmationTimeFragment) {
    clientOrderConfirmationTimeFragment.setClientOrderConfirmationTimeViewModel(
        getPresentationComponent().getClientOrderConfirmationTimeViewModel(
            clientOrderConfirmationTimeFragment
        )
    );
  }

  @Override
  public void inject(MovingToClientFragment movingToClientFragment) {
    movingToClientFragment.setMovingToClientViewModel(
        getPresentationComponent().getMovingToClientViewModel(movingToClientFragment)
    );
    movingToClientFragment.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
    movingToClientFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
  }

  @Override
  public void inject(MovingToClientActionsDialogFragment movingToClientActionsDialogFragment) {
    movingToClientActionsDialogFragment.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(ActionOrderDetailsFragment actionOrderDetailsFragment) {
    actionOrderDetailsFragment.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
  }

  @Override
  public void inject(MovingToClientRouteFragment movingToClientRouteFragment) {
    movingToClientRouteFragment.setOrderRouteViewModel(
        getPresentationComponent().getOrderRouteViewModel(movingToClientRouteFragment)
    );
  }

  @Override
  public void inject(WaitingForClientFragment waitingForClientFragment) {
    waitingForClientFragment.setWaitingForClientViewModel(
        getPresentationComponent().getWaitingForClientViewModel(waitingForClientFragment)
    );
    waitingForClientFragment.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
    waitingForClientFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
  }

  @Override
  public void inject(WaitingForClientActionsDialogFragment waitingForClientActionsDialogFragment) {
    waitingForClientActionsDialogFragment.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(WaitingForClientRouteFragment waitingForClientRouteFragment) {
    waitingForClientRouteFragment.setOrderRouteViewModel(
        getPresentationComponent().getOrderRouteViewModel(waitingForClientRouteFragment)
    );
  }

  @Override
  public void inject(OrderFulfillmentFragment orderFulfillmentFragment) {
    orderFulfillmentFragment.setOrderTimeViewModel(
        getPresentationComponent().getOrderTimeViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setOrderCostViewModel(
        getPresentationComponent().getOrderCostViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setNextRoutePointViewModel(
        getPresentationComponent().getNextRoutePointViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setOrderRouteViewModel(
        getPresentationComponent().getOrderRouteViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
  }

  @Override
  public void inject(OrderFulfillmentActionsDialogFragment orderFulfillmentActionsDialogFragment) {
    orderFulfillmentActionsDialogFragment.setEventLogger(
        getBackendComponent().getEventLogger()
    );
    orderFulfillmentActionsDialogFragment.setNextRoutePointViewModel(
        getPresentationComponent().getNextRoutePointViewModel(orderFulfillmentActionsDialogFragment)
    );
  }

  @Override
  public void inject(OrderRouteFragment orderRouteFragment) {
    orderRouteFragment.setOrderRouteViewModel(
        getPresentationComponent().getOrderRouteViewModel(orderRouteFragment)
    );
  }

  @Override
  public void inject(CallToClientFragment callToClientFragment) {
    callToClientFragment.setCallToClientViewModel(
        getPresentationComponent().getCallToClientViewModel(callToClientFragment)
    );
  }

  @Override
  public void inject(CallToOperatorFragment callToOperatorFragment) {
    callToOperatorFragment.setCallToOperatorViewModel(
        getPresentationComponent().getCallToOperatorViewModel(callToOperatorFragment)
    );
  }

  @Override
  public void inject(CancelOrderDialogFragment cancelOrderDialogFragment) {
    cancelOrderDialogFragment.setCancelOrderViewModel(
        getPresentationComponent().getCancelOrderViewModel(cancelOrderDialogFragment)
    );
    cancelOrderDialogFragment.setCancelOrderReasonsViewModel(
        getPresentationComponent().getCancelOrderReasonsViewModel()
    );
  }

  @Override
  public void inject(BalanceFragment balanceFragment) {
    balanceFragment.setBalanceViewModel(
        getPresentationComponent().getBalanceViewModel()
    );
  }

  @Override
  public void inject(BalanceSummaryFragment balanceSummaryFragment) {
    balanceSummaryFragment.setBalanceViewModel(
        getPresentationComponent().getBalanceViewModel()
    );
  }

  @Override
  public void inject(MenuFragment menuFragment) {
    menuFragment.setAppSettingsService(
        getBackendComponent().getAppSettingsService()
    );
    menuFragment.setBalanceViewModel(
        getPresentationComponent().getBalanceViewModel()
    );
    menuFragment.setOnlineSwitchViewModel(
        getPresentationComponent().getExitOnlineSwitchViewModel(menuFragment)
    );
    menuFragment.setPreOrdersListViewModel(
        getPresentationComponent().getPreOrdersListViewModel()
    );
  }

  @Override
  public void inject(ServerConnectionFragment serverConnectionFragment) {
    serverConnectionFragment.setServerConnectionViewModel(
        getPresentationComponent().getServerConnectionViewModel()
    );
  }

  @Override
  public void inject(OrderCostDetailsFragment orderCostDetailsFragment) {
    orderCostDetailsFragment.setOrderCostDetailsViewModel(
        getPresentationComponent().getOrderCostDetailsViewModel()
    );
    orderCostDetailsFragment.setConfirmOrderPaymentViewModel(
        getPresentationComponent().getConfirmOrderPaymentViewModel(orderCostDetailsFragment)
    );
    orderCostDetailsFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
  }

  @Override
  public void inject(OrderCostDetailsRouteFragment orderCostDetailsRouteFragment) {
    orderCostDetailsRouteFragment.setOrderRouteViewModel(
        getPresentationComponent().getOrderRouteViewModel(orderCostDetailsRouteFragment)
    );
  }

  @Override
  public void inject(OrderCostDetailsActionsDialogFragment orderCostDetailsActionsDialogFragment) {
    orderCostDetailsActionsDialogFragment.setEventLogger(
        getBackendComponent().getEventLogger()
    );
  }

  @Override
  public void inject(ProfileFragment profileFragment) {
    profileFragment.setAppSettings(
        getBackendComponent().getAppSettingsService()
    );
  }

  @Override
  public void inject(DriverPreOrderBookingFragment driverPreOrderBookingFragment) {
    driverPreOrderBookingFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
    driverPreOrderBookingFragment.setRingTonePlayer(
        getSingleRingTonePlayer()
    );
    driverPreOrderBookingFragment.setOrderConfirmationViewModel(
        getPresentationComponent().getPreOrderBookingViewModel(driverPreOrderBookingFragment)
    );
  }

  @Override
  public void inject(PreOrdersFragment preOrdersFragment) {
    preOrdersFragment.setPreOrdersListViewModel(
        getPresentationComponent().getPreOrdersListViewModel()
    );
  }

  @Override
  public void inject(PreOrderFragment preOrderFragment) {
    preOrderFragment.setOrderViewModel(
        getPresentationComponent().getPOrderViewModel(preOrderFragment)
    );
  }

  @Override
  public void inject(SelectedPreOrderFragment selectedPreOrderFragment) {
    selectedPreOrderFragment.setOrderViewModel(
        getPresentationComponent().getSelectedPreOrderViewModel(selectedPreOrderFragment)
    );
  }

  @Override
  public void inject(SelectedPreOrderConfirmationFragment selectedPreOrderConfirmationFragment) {
    selectedPreOrderConfirmationFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
    selectedPreOrderConfirmationFragment.setRingTonePlayer(
        getSingleRingTonePlayer()
    );
    selectedPreOrderConfirmationFragment.setOrderConfirmationViewModel(
        getPresentationComponent()
            .getSelectedPreOrderConfirmationViewModel(selectedPreOrderConfirmationFragment)
    );
  }

  @Override
  public void inject(NewPreOrderFragment newPreOrderFragment) {
    newPreOrderFragment.setPreOrderViewModel(
        getPresentationComponent().getPreOrderViewModel()
    );
  }

  @Override
  public void inject(NewPreOrderButtonFragment newPreOrderButtonFragment) {
    newPreOrderButtonFragment.setPreOrderViewModel(
        getPresentationComponent().getPreOrderViewModel()
    );
  }

  @Override
  public void inject(DriverPreOrderConfirmationFragment driverPreOrderConfirmationFragment) {
    driverPreOrderConfirmationFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
    driverPreOrderConfirmationFragment.setOrderConfirmationViewModel(
        getPresentationComponent().getOrderConfirmationViewModel(driverPreOrderConfirmationFragment)
    );
    driverPreOrderConfirmationFragment.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
  }

  @Override
  public void inject(PreOrderConfirmationFragment preOrderConfirmationFragment) {
    preOrderConfirmationFragment.setOrderViewModel(
        getPresentationComponent().getOrderViewModel()
    );
  }

  @Override
  public void inject(UpcomingPreOrderFragment upcomingPreOrderFragment) {
    upcomingPreOrderFragment.setOrderViewModel(
        getPresentationComponent().getUpcomingPreOrderViewModel(upcomingPreOrderFragment)
    );
  }

  @Override
  public void inject(UpcomingPreOrderConfirmationFragment upcomingPreOrderConfirmationFragment) {
    upcomingPreOrderConfirmationFragment.setShakeItPlayer(
        getShakeItPlayer()
    );
    upcomingPreOrderConfirmationFragment.setRingTonePlayer(
        getSingleRingTonePlayer()
    );
    upcomingPreOrderConfirmationFragment.setOrderConfirmationViewModel(
        getPresentationComponent()
            .getUpcomingPreOrderConfirmationViewModel(upcomingPreOrderConfirmationFragment)
    );
  }

  @Override
  public void inject(UpcomingPreOrderNotificationFragment upcomingPreOrderNotificationFragment) {
    upcomingPreOrderNotificationFragment.setUpcomingPreOrderViewModel(
        getPresentationComponent()
            .getUpcomingPreOrderViewModel(upcomingPreOrderNotificationFragment)
    );
    upcomingPreOrderNotificationFragment.setUpcomingPreOrderNotificationViewModel(
        getPresentationComponent().getUpcomingPreOrderAvailabilityViewModel()
    );
  }

  @NonNull
  private TimeUtils getTimeUtils() {
    if (timeUtils == null) {
      timeUtils = new TimeUtilsImpl();
    }
    return timeUtils;
  }

  @NonNull
  private PresentationComponent getPresentationComponent() {
    if (presentationComponent == null) {
      presentationComponent = new PresentationComponentImpl(
          getBackendComponent(),
          getTimeUtils()
      );
    }
    return presentationComponent;
  }

  @NonNull
  private BackendComponent getBackendComponent() {
    if (backendComponent == null) {
      backendComponent = new BackendComponentImpl(appContext);
    }
    return backendComponent;
  }

  @NonNull
  private NavigationMapper getNavigationMapper() {
    if (navigationMapper == null) {
      navigationMapper = new NavigationMapperImpl(
          getSingleRingTonePlayer(),
          getShakeItPlayer()
      );
    }
    return navigationMapper;
  }

  @NonNull
  private SingleRingTonePlayer getSingleRingTonePlayer() {
    if (singleRingTonePlayer == null) {
      singleRingTonePlayer = new SingleRingTonePlayer(appContext);
    }
    return singleRingTonePlayer;
  }

  @NonNull
  private ShakeItPlayer getShakeItPlayer() {
    if (shakeItPlayer == null) {
      if (VERSION.SDK_INT >= VERSION_CODES.O) {
        shakeItPlayer = new SingleShakePlayer(
            appContext,
            new NewPatternMapper()
        );
      } else {
        shakeItPlayer = new OldSingleShakePlayer(
            appContext,
            new OldPatternMapper()
        );
      }
    }
    return shakeItPlayer;
  }
}
