package com.cargopull.executor_driver.di;

import android.content.Context;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.application.AutoRouterImpl;
import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.FcmService;
import com.cargopull.executor_driver.application.MainApplication;
import com.cargopull.executor_driver.application.MovingToClientActivity;
import com.cargopull.executor_driver.application.OrderCostDetailsActivity;
import com.cargopull.executor_driver.application.OrderFulfillmentActivity;
import com.cargopull.executor_driver.application.WaitingForClientActivity;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenterImpl;
import com.cargopull.executor_driver.backend.ringtone.SingleRingTonePlayer;
import com.cargopull.executor_driver.backend.settings.AppPreferences;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.vibro.NewPatternMapper;
import com.cargopull.executor_driver.backend.vibro.OldPatternMapper;
import com.cargopull.executor_driver.backend.vibro.SingleShakePlayer;
import com.cargopull.executor_driver.backend.web.AuthorizationInterceptor;
import com.cargopull.executor_driver.backend.web.ConnectivityInterceptor;
import com.cargopull.executor_driver.backend.web.DeprecatedVersionInterceptor;
import com.cargopull.executor_driver.backend.web.ReceiveTokenInterceptor;
import com.cargopull.executor_driver.backend.web.SendTokenInterceptor;
import com.cargopull.executor_driver.backend.web.SendVersionInterceptor;
import com.cargopull.executor_driver.backend.web.TokenKeeper;
import com.cargopull.executor_driver.gateway.SmsCodeMapper;
import com.cargopull.executor_driver.gateway.TokenKeeperImpl;
import com.cargopull.executor_driver.interactor.auth.LoginSharer;
import com.cargopull.executor_driver.utils.EventLogger;
import com.cargopull.executor_driver.utils.EventLoggerImpl;
import com.cargopull.executor_driver.utils.TimeUtils;
import com.cargopull.executor_driver.utils.TimeUtilsImpl;
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
import com.cargopull.executor_driver.view.MovingToClientDetailsFragment;
import com.cargopull.executor_driver.view.MovingToClientFragment;
import com.cargopull.executor_driver.view.MovingToClientRouteFragment;
import com.cargopull.executor_driver.view.NewPreOrderButtonFragment;
import com.cargopull.executor_driver.view.NewPreOrderFragment;
import com.cargopull.executor_driver.view.OnlineFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsOrderDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsRouteFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentActionsDialogFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentDetailsFragment;
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
import com.cargopull.executor_driver.view.WaitingForClientFragment;
import com.cargopull.executor_driver.view.WaitingForClientRouteFragment;
import com.cargopull.executor_driver.view.auth.LoginFragment;
import com.cargopull.executor_driver.view.auth.PasswordFragment;
import com.cargopull.executor_driver.view.auth.SmsReceiver;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AppComponentImpl implements AppComponent {

  @SuppressWarnings("FieldCanBeLocal")
  @NonNull
  private final EventLogger eventLogger;
  @NonNull
  private final PresentationComponent presentationComponent;
  @NonNull
  private final AppSettingsService appSettingsService;
  @NonNull
  private final AutoRouterImpl autoRouter;
  @NonNull
  private final SingleRingTonePlayer singleRingTonePlayer;
  @NonNull
  private final SingleShakePlayer singleShakePlayer;

  public AppComponentImpl(@NonNull Context appContext) {
    appContext = appContext.getApplicationContext();
    TimeUtils timeUtils = new TimeUtilsImpl();
    appSettingsService = new AppPreferences(appContext);
    TokenKeeper tokenKeeper = new TokenKeeperImpl(appSettingsService);
    LoginSharer loginSharer = new LoginSharer(appSettingsService);
    eventLogger = new EventLoggerImpl(loginSharer, FirebaseAnalytics.getInstance(appContext));
    BackendComponent backendComponent = new BackendComponentImpl(
        loginSharer,
        appSettingsService,
        new ConnectivityInterceptor(appContext),
        new SendVersionInterceptor(),
        new DeprecatedVersionInterceptor(),
        new AuthorizationInterceptor(),
        new SendTokenInterceptor(tokenKeeper),
        new ReceiveTokenInterceptor(tokenKeeper)
    );
    GeolocationCenter geolocationCenter = new GeolocationCenterImpl(appContext);
    RepositoryComponent repositoryComponent = new RepositoryComponentImpl(
        backendComponent,
        geolocationCenter
    );
    InteractorComponent interactorComponent = new InteractorComponentImpl(
        loginSharer, timeUtils, repositoryComponent
    );
    presentationComponent = new PresentationComponentImpl(
        eventLogger, loginSharer, interactorComponent, timeUtils
    );
    singleRingTonePlayer = new SingleRingTonePlayer(appContext);
    singleShakePlayer = new SingleShakePlayer(
        appContext,
        new NewPatternMapper(),
        new OldPatternMapper()
    );
    autoRouter = new AutoRouterImpl(singleRingTonePlayer, singleShakePlayer);
  }

  @Override
  public void inject(MainApplication mainApplication) {
    mainApplication.setRingTonePlayer(
        singleRingTonePlayer
    );
    mainApplication.setShakeItPlayer(
        singleShakePlayer
    );
    mainApplication.setServerConnectionViewModel(
        presentationComponent.getServerConnectionViewModel()
    );
    mainApplication.setCancelOrderReasonsViewModel(
        presentationComponent.getCancelOrderReasonsViewModel()
    );
    mainApplication.setBalanceViewModel(
        presentationComponent.getBalanceViewModel()
    );
    mainApplication.setExecutorStateViewModel(
        presentationComponent.getExecutorStateViewModel()
    );
    mainApplication.setOrderViewModel(
        presentationComponent.getOrderViewModel()
    );
    mainApplication.setPreOrderViewModel(
        presentationComponent.getPreOrderViewModel()
    );
    mainApplication.setUpcomingPreOrderViewModel(
        presentationComponent.getUpcomingPreOrderAvailabilityViewModel()
    );
    mainApplication.setPreOrdersListViewModel(
        presentationComponent.getPreOrdersListViewModel()
    );
    mainApplication.setOrderCostDetailsViewModel(
        presentationComponent.getOrderCostDetailsViewModel()
    );
    mainApplication.setGeoLocationViewModel(
        presentationComponent.getGeoLocationViewModel()
    );
    mainApplication.setMissedOrderViewModel(
        presentationComponent.getMissedOrderViewModel()
    );
    mainApplication.setUpcomingPreOrderMessageViewModel(
        presentationComponent.getUpcomingPreOrderMessagesViewModel()
    );
    mainApplication.setCancelledOrderViewModel(
        presentationComponent.getCancelledOrderViewModel()
    );
    mainApplication.setUpdateMessageViewModel(
        presentationComponent.getUpdateMessageViewModel()
    );
    mainApplication.setCurrentCostPollingViewModel(
        presentationComponent.getCurrentCostPollingViewModel()
    );
    mainApplication.setServerTimeViewModel(
        presentationComponent.getServerTimeViewModel()
    );
    mainApplication.setAutoRouter(autoRouter);
    mainApplication.setLifeCycleCallbacks(autoRouter);
  }

  @Override
  public void inject(BaseActivity baseActivity) {
    baseActivity.setExecutorStateViewModel(
        presentationComponent.getExecutorStateViewModel()
    );
    baseActivity.setUpdateMessageViewModel(
        presentationComponent.getUpdateMessageViewModel()
    );
    baseActivity.setAnnouncementViewModel(
        presentationComponent.getAnnouncementViewModel()
    );
    baseActivity.setServerConnectionViewModel(
        presentationComponent.getServerConnectionViewModel()
    );
    baseActivity.setServerTimeViewModel(
        presentationComponent.getServerTimeViewModel()
    );
  }

  @Override
  public void inject(MovingToClientActivity movingToClientActivity) {
    movingToClientActivity.setEventLogger(eventLogger);
  }

  @Override
  public void inject(WaitingForClientActivity waitingForClientActivity) {
    waitingForClientActivity.setEventLogger(eventLogger);
  }

  @Override
  public void inject(OrderFulfillmentActivity orderFulfillmentActivity) {
    orderFulfillmentActivity.setEventLogger(eventLogger);
  }

  @Override
  public void inject(OrderCostDetailsActivity orderCostDetailsActivity) {
    orderCostDetailsActivity.setEventLogger(eventLogger);
  }

  @Override
  public void inject(FcmService fcmService) {
    fcmService.setAnnouncementViewModel(presentationComponent.getAnnouncementViewModel());
  }

  @Override
  public void inject(LoginFragment loginFragment) {
    loginFragment.setAppSettings(appSettingsService);
    loginFragment.setPhoneViewModel(presentationComponent.getPhoneViewModel(loginFragment));
  }

  @Override
  public void inject(PasswordFragment passwordFragment) {
    passwordFragment.setSmsButtonViewModel(
        presentationComponent.getSmsButtonViewModel(passwordFragment)
    );
    passwordFragment.setCodeHeaderViewModel(
        presentationComponent.getCodeHeaderViewModel(passwordFragment)
    );
    passwordFragment.setCodeViewModel(
        presentationComponent.getCodeViewModel(passwordFragment)
    );
    passwordFragment.setSmsReceiver(
        new SmsReceiver(
            new SmsCodeMapper()
        )
    );
  }

  @Override
  public void inject(MapFragment mapFragment) {
    mapFragment.setMapViewModel(presentationComponent.getMapViewModel(mapFragment));
    mapFragment.setGeoLocationViewModel(presentationComponent.getGeoLocationViewModel());
  }

  @Override
  public void inject(OnlineFragment onlineFragment) {
    onlineFragment.setOnlineSwitchViewModel(
        presentationComponent.getOnlineSwitchViewModel(onlineFragment)
    );
    onlineFragment.setOnlineButtonViewModel(
        presentationComponent.getSelectedOnlineButtonViewModel(onlineFragment)
    );
  }

  @Override
  public void inject(GoOnlineFragment goOnlineFragment) {
    goOnlineFragment.setOnlineButtonViewModel(
        presentationComponent.getOnlineButtonViewModel(goOnlineFragment)
    );
  }

  @Override
  public void inject(ChooseVehicleFragment chooseVehicleFragment) {
    chooseVehicleFragment.setChooseVehicleViewModel(
        presentationComponent.getChooseVehicleViewModel(chooseVehicleFragment)
    );
  }

  @Override
  public void inject(VehicleOptionsFragment vehicleOptionsFragment) {
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        presentationComponent.getVehicleOptionsViewModel(vehicleOptionsFragment)
    );
  }

  @Override
  public void inject(SelectedVehicleOptionsFragment vehicleOptionsFragment) {
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        presentationComponent.getSelectedVehicleOptionsViewModel(vehicleOptionsFragment)
    );
  }

  @Override
  public void inject(SelectedVehicleFragment selectedVehicleFragment) {
    selectedVehicleFragment.setSelectedVehicleViewModel(
        presentationComponent.getSelectedVehicleViewModel(selectedVehicleFragment)
    );
    selectedVehicleFragment.setChooseVehicleViewModel(
        presentationComponent.getCurrentChooseVehicleViewModel(selectedVehicleFragment)
    );
  }

  @Override
  public void inject(ServicesFragment servicesFragment) {
    servicesFragment.setServicesSliderViewModel(presentationComponent.getServicesSliderViewModel());
    servicesFragment.setServicesViewModel(
        presentationComponent.getServicesViewModel(servicesFragment)
    );
  }

  @Override
  public void inject(DriverOrderConfirmationFragment driverOrderConfirmationFragment) {
    driverOrderConfirmationFragment.setOrderConfirmationViewModel(
        presentationComponent.getOrderConfirmationViewModel(driverOrderConfirmationFragment)
    );
    driverOrderConfirmationFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
  }

  @Override
  public void inject(ClientOrderConfirmationFragment clientOrderConfirmationFragment) {
    clientOrderConfirmationFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
  }

  @Override
  public void inject(ClientOrderConfirmationTimeFragment clientOrderConfirmationTimeFragment) {
    clientOrderConfirmationTimeFragment.setClientOrderConfirmationTimeViewModel(
        presentationComponent.getClientOrderConfirmationTimeViewModel(
            clientOrderConfirmationTimeFragment
        )
    );
  }

  @Override
  public void inject(MovingToClientFragment movingToClientFragment) {
    movingToClientFragment.setMovingToClientViewModel(
        presentationComponent.getMovingToClientViewModel(movingToClientFragment)
    );
    movingToClientFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
    movingToClientFragment.setShakeItPlayer(singleShakePlayer);
  }

  @Override
  public void inject(MovingToClientDetailsFragment movingToClientDetailsFragment) {
    movingToClientDetailsFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
  }

  @Override
  public void inject(MovingToClientRouteFragment movingToClientRouteFragment) {
    movingToClientRouteFragment.setOrderRouteViewModel(
        presentationComponent.getOrderRouteViewModel(movingToClientRouteFragment)
    );
  }

  @Override
  public void inject(WaitingForClientFragment waitingForClientFragment) {
    waitingForClientFragment.setWaitingForClientViewModel(
        presentationComponent.getWaitingForClientViewModel(waitingForClientFragment)
    );
    waitingForClientFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
    waitingForClientFragment.setShakeItPlayer(singleShakePlayer);
  }

  @Override
  public void inject(WaitingForClientRouteFragment waitingForClientRouteFragment) {
    waitingForClientRouteFragment.setOrderRouteViewModel(
        presentationComponent.getOrderRouteViewModel(waitingForClientRouteFragment)
    );
  }

  @Override
  public void inject(OrderFulfillmentFragment orderFulfillmentFragment) {
    orderFulfillmentFragment.setOrderTimeViewModel(
        presentationComponent.getOrderTimeViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setOrderCostViewModel(
        presentationComponent.getOrderCostViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setNextRoutePointViewModel(
        presentationComponent.getNextRoutePointViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setOrderRouteViewModel(
        presentationComponent.getOrderRouteViewModel(orderFulfillmentFragment)
    );
    orderFulfillmentFragment.setShakeItPlayer(singleShakePlayer);
  }

  @Override
  public void inject(OrderFulfillmentDetailsFragment orderFulfillmentDetailsFragment) {
    orderFulfillmentDetailsFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
  }

  @Override
  public void inject(OrderFulfillmentActionsDialogFragment orderFulfillmentActionsDialogFragment) {
    orderFulfillmentActionsDialogFragment.setNextRoutePointViewModel(
        presentationComponent.getNextRoutePointViewModel(orderFulfillmentActionsDialogFragment)
    );
  }

  @Override
  public void inject(OrderRouteFragment orderRouteFragment) {
    orderRouteFragment.setOrderRouteViewModel(
        presentationComponent.getOrderRouteViewModel(orderRouteFragment)
    );
  }

  @Override
  public void inject(CallToClientFragment callToClientFragment) {
    callToClientFragment.setCallToClientViewModel(
        presentationComponent.getCallToClientViewModel(callToClientFragment)
    );
  }

  @Override
  public void inject(CallToOperatorFragment callToOperatorFragment) {
    callToOperatorFragment.setCallToOperatorViewModel(
        presentationComponent.getCallToOperatorViewModel(callToOperatorFragment)
    );
  }

  @Override
  public void inject(CancelOrderDialogFragment cancelOrderDialogFragment) {
    cancelOrderDialogFragment.setCancelOrderViewModel(
        presentationComponent.getCancelOrderViewModel(cancelOrderDialogFragment)
    );
    cancelOrderDialogFragment.setCancelOrderReasonsViewModel(
        presentationComponent.getCancelOrderReasonsViewModel()
    );
  }

  @Override
  public void inject(BalanceFragment balanceFragment) {
    balanceFragment.setBalanceViewModel(presentationComponent.getBalanceViewModel());
  }

  @Override
  public void inject(BalanceSummaryFragment balanceSummaryFragment) {
    balanceSummaryFragment.setBalanceViewModel(presentationComponent.getBalanceViewModel());
  }

  @Override
  public void inject(MenuFragment menuFragment) {
    menuFragment.setBalanceViewModel(presentationComponent.getBalanceViewModel());
    menuFragment.setOnlineSwitchViewModel(
        presentationComponent.getExitOnlineSwitchViewModel(menuFragment)
    );
    menuFragment.setPreOrdersListViewModel(presentationComponent.getPreOrdersListViewModel());
  }

  @Override
  public void inject(ServerConnectionFragment serverConnectionFragment) {
    serverConnectionFragment.setServerConnectionViewModel(
        presentationComponent.getServerConnectionViewModel()
    );
  }

  @Override
  public void inject(OrderCostDetailsFragment orderCostDetailsFragment) {
    orderCostDetailsFragment.setOrderCostDetailsViewModel(
        presentationComponent.getOrderCostDetailsViewModel()
    );
    orderCostDetailsFragment.setConfirmOrderPaymentViewModel(
        presentationComponent.getConfirmOrderPaymentViewModel(orderCostDetailsFragment)
    );
    orderCostDetailsFragment.setShakeItPlayer(singleShakePlayer);
  }

  @Override
  public void inject(OrderCostDetailsOrderDetailsFragment orderCostDetailsOrderDetailsFragment) {
    orderCostDetailsOrderDetailsFragment.setOrderViewModel(
        presentationComponent.getOrderViewModel()
    );
  }

  @Override
  public void inject(OrderCostDetailsRouteFragment orderCostDetailsRouteFragment) {
    orderCostDetailsRouteFragment.setOrderRouteViewModel(
        presentationComponent.getOrderRouteViewModel(orderCostDetailsRouteFragment)
    );
  }

  @Override
  public void inject(ProfileFragment profileFragment) {
    profileFragment.setAppSettings(appSettingsService);
  }

  @Override
  public void inject(DriverPreOrderBookingFragment driverPreOrderBookingFragment) {
    driverPreOrderBookingFragment.setShakeItPlayer(singleShakePlayer);
    driverPreOrderBookingFragment.setRingTonePlayer(singleRingTonePlayer);
    driverPreOrderBookingFragment.setOrderConfirmationViewModel(
        presentationComponent.getPreOrderBookingViewModel(driverPreOrderBookingFragment)
    );
  }

  @Override
  public void inject(PreOrdersFragment preOrdersFragment) {
    preOrdersFragment.setPreOrdersListViewModel(
        presentationComponent.getPreOrdersListViewModel()
    );
  }

  @Override
  public void inject(PreOrderFragment preOrderFragment) {
    preOrderFragment.setOrderViewModel(
        presentationComponent.getPOrderViewModel(preOrderFragment)
    );
  }

  @Override
  public void inject(SelectedPreOrderFragment selectedPreOrderFragment) {
    selectedPreOrderFragment.setOrderViewModel(
        presentationComponent.getSelectedPreOrderViewModel(selectedPreOrderFragment)
    );
  }

  @Override
  public void inject(SelectedPreOrderConfirmationFragment selectedPreOrderConfirmationFragment) {
    selectedPreOrderConfirmationFragment.setShakeItPlayer(singleShakePlayer);
    selectedPreOrderConfirmationFragment.setRingTonePlayer(singleRingTonePlayer);
    selectedPreOrderConfirmationFragment.setOrderConfirmationViewModel(
        presentationComponent
            .getSelectedPreOrderConfirmationViewModel(selectedPreOrderConfirmationFragment)
    );
  }

  @Override
  public void inject(NewPreOrderFragment newPreOrderFragment) {
    newPreOrderFragment.setPreOrderViewModel(
        presentationComponent.getPreOrderViewModel()
    );
  }

  @Override
  public void inject(NewPreOrderButtonFragment newPreOrderButtonFragment) {
    newPreOrderButtonFragment.setPreOrderViewModel(
        presentationComponent.getPreOrderViewModel()
    );
  }

  @Override
  public void inject(DriverPreOrderConfirmationFragment driverPreOrderConfirmationFragment) {
    driverPreOrderConfirmationFragment.setShakeItPlayer(singleShakePlayer);
    driverPreOrderConfirmationFragment.setOrderConfirmationViewModel(
        presentationComponent.getOrderConfirmationViewModel(driverPreOrderConfirmationFragment)
    );
    driverPreOrderConfirmationFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
  }

  @Override
  public void inject(PreOrderConfirmationFragment preOrderConfirmationFragment) {
    preOrderConfirmationFragment.setOrderViewModel(presentationComponent.getOrderViewModel());
  }

  @Override
  public void inject(UpcomingPreOrderFragment upcomingPreOrderFragment) {
    upcomingPreOrderFragment.setOrderViewModel(
        presentationComponent.getUpcomingPreOrderViewModel(upcomingPreOrderFragment)
    );
  }

  @Override
  public void inject(UpcomingPreOrderConfirmationFragment upcomingPreOrderConfirmationFragment) {
    upcomingPreOrderConfirmationFragment.setShakeItPlayer(singleShakePlayer);
    upcomingPreOrderConfirmationFragment.setRingTonePlayer(singleRingTonePlayer);
    upcomingPreOrderConfirmationFragment.setOrderConfirmationViewModel(
        presentationComponent
            .getUpcomingPreOrderConfirmationViewModel(upcomingPreOrderConfirmationFragment)
    );
  }

  @Override
  public void inject(UpcomingPreOrderNotificationFragment upcomingPreOrderNotificationFragment) {
    upcomingPreOrderNotificationFragment.setUpcomingPreOrderViewModel(
        presentationComponent.getUpcomingPreOrderViewModel(upcomingPreOrderNotificationFragment)
    );
    upcomingPreOrderNotificationFragment.setUpcomingPreOrderNotificationViewModel(
        presentationComponent.getUpcomingPreOrderAvailabilityViewModel()
    );
  }
}
