package com.cargopull.executor_driver.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.cargopull.executor_driver.application.AutoRouterImpl;
import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.FcmService;
import com.cargopull.executor_driver.application.MainApplication;
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
import com.cargopull.executor_driver.presentation.ViewModelFactory;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModelImpl;
import com.cargopull.executor_driver.presentation.balance.BalanceViewModelImpl;
import com.cargopull.executor_driver.presentation.calltoclient.CallToClientViewModelImpl;
import com.cargopull.executor_driver.presentation.calltooperator.CallToOperatorViewModelImpl;
import com.cargopull.executor_driver.presentation.cancelorder.CancelOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModelImpl;
import com.cargopull.executor_driver.presentation.choosevehicle.ChooseVehicleViewModelImpl;
import com.cargopull.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewModelImpl;
import com.cargopull.executor_driver.presentation.code.CodeViewModelImpl;
import com.cargopull.executor_driver.presentation.codeheader.CodeHeaderViewModelImpl;
import com.cargopull.executor_driver.presentation.confirmorderpayment.ConfirmOrderPaymentViewModelImpl;
import com.cargopull.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModelImpl;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModelImpl;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationViewModelImpl;
import com.cargopull.executor_driver.presentation.map.MapViewModelImpl;
import com.cargopull.executor_driver.presentation.missedorder.MissedOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.movingtoclient.MovingToClientViewModelImpl;
import com.cargopull.executor_driver.presentation.nextroutepoint.NextRoutePointViewModelImpl;
import com.cargopull.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl;
import com.cargopull.executor_driver.presentation.onlineswitch.OnlineSwitchViewModelImpl;
import com.cargopull.executor_driver.presentation.ordecostdetails.OrderCostDetailsViewModelImpl;
import com.cargopull.executor_driver.presentation.order.OrderViewModelImpl;
import com.cargopull.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModelImpl;
import com.cargopull.executor_driver.presentation.ordercost.OrderCostViewModelImpl;
import com.cargopull.executor_driver.presentation.orderroute.OrderRouteViewModelImpl;
import com.cargopull.executor_driver.presentation.ordertime.OrderTimeViewModelImpl;
import com.cargopull.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.cargopull.executor_driver.presentation.preorder.PreOrderViewModelImpl;
import com.cargopull.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModelImpl;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModelImpl;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModel;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModelImpl;
import com.cargopull.executor_driver.presentation.services.ServicesListItems;
import com.cargopull.executor_driver.presentation.services.ServicesSliderViewModelImpl;
import com.cargopull.executor_driver.presentation.services.ServicesViewModelImpl;
import com.cargopull.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModelImpl;
import com.cargopull.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModelImpl;
import com.cargopull.executor_driver.presentation.waitingforclient.WaitingForClientViewModelImpl;
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
import com.cargopull.executor_driver.view.DriverPreOrderConfirmationFragment;
import com.cargopull.executor_driver.view.GoOnlineFragment;
import com.cargopull.executor_driver.view.MapFragment;
import com.cargopull.executor_driver.view.MenuFragment;
import com.cargopull.executor_driver.view.MovingToClientDetailsFragment;
import com.cargopull.executor_driver.view.MovingToClientFragment;
import com.cargopull.executor_driver.view.MovingToClientRouteFragment;
import com.cargopull.executor_driver.view.OnlineFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsOrderDetailsFragment;
import com.cargopull.executor_driver.view.OrderCostDetailsRouteFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentActionsDialogFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentDetailsFragment;
import com.cargopull.executor_driver.view.OrderFulfillmentFragment;
import com.cargopull.executor_driver.view.OrderRouteFragment;
import com.cargopull.executor_driver.view.ProfileFragment;
import com.cargopull.executor_driver.view.SelectedVehicleFragment;
import com.cargopull.executor_driver.view.SelectedVehicleOptionsFragment;
import com.cargopull.executor_driver.view.ServerConnectionFragment;
import com.cargopull.executor_driver.view.ServicesFragment;
import com.cargopull.executor_driver.view.VehicleOptionsFragment;
import com.cargopull.executor_driver.view.WaitingForClientFragment;
import com.cargopull.executor_driver.view.WaitingForClientRouteFragment;
import com.cargopull.executor_driver.view.auth.LoginFragment;
import com.cargopull.executor_driver.view.auth.PasswordFragment;
import com.cargopull.executor_driver.view.auth.SmsReceiver;

public class AppComponentImpl implements AppComponent {

  @NonNull
  private final InteractorComponent interactorComponent;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private final AppSettingsService appSettingsService;
  @NonNull
  private final AutoRouterImpl autoRouter;
  @NonNull
  private final ServerConnectionViewModel serverConnectionViewModel;
  @NonNull
  private final ExecutorStateViewModelImpl executorStateViewModel;
  @NonNull
  private final UpdateMessageViewModelImpl updateMessageViewModel;
  @NonNull
  private final ServerTimeViewModel serverTimeViewModel;
  @NonNull
  private final SingleRingTonePlayer singleRingTonePlayer;
  @NonNull
  private final SingleShakePlayer singleShakePlayer;
  @NonNull
  private final AnnouncementViewModel announcementViewModel;

  public AppComponentImpl(@NonNull Context appContext) {
    appContext = appContext.getApplicationContext();
    timeUtils = new TimeUtilsImpl();
    appSettingsService = new AppPreferences(appContext);
    TokenKeeper tokenKeeper = new TokenKeeperImpl(appSettingsService);
    LoginSharer loginSharer = new LoginSharer(appSettingsService);
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
    this.interactorComponent = new InteractorComponentImpl(
        loginSharer, timeUtils, repositoryComponent
    );
    serverConnectionViewModel = new ServerConnectionViewModelImpl(
        interactorComponent.getServerConnectionUseCase()
    );
    executorStateViewModel = new ExecutorStateViewModelImpl(
        interactorComponent.getExecutorStateUseCase()
    );
    updateMessageViewModel = new UpdateMessageViewModelImpl(
        interactorComponent.getUpdateMessageUseCase()
    );
    serverTimeViewModel = new ServerTimeViewModelImpl(interactorComponent.getServerTimeUseCase());
    announcementViewModel = new AnnouncementViewModelImpl();
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
        serverConnectionViewModel
    );
    mainApplication.setCancelOrderReasonsViewModel(
        new CancelOrderReasonsViewModelImpl(
            interactorComponent.getCancelOrderReasonsUseCase()
        )
    );
    mainApplication.setBalanceViewModel(
        new BalanceViewModelImpl(
            interactorComponent.getExecutorBalanceUseCase()
        )
    );
    mainApplication.setExecutorStateViewModel(
        executorStateViewModel
    );
    mainApplication.setOrderViewModel(
        new OrderViewModelImpl(
            interactorComponent.getOrderUseCase(),
            timeUtils
        )
    );
    mainApplication.setPreOrderViewModel(
        new PreOrderViewModelImpl(
            interactorComponent.getPreOrderUseCase()
        )
    );
    mainApplication.setGeoLocationViewModel(
        new GeoLocationViewModelImpl(
            interactorComponent.getGeoLocationUseCase()
        )
    );
    mainApplication.setMissedOrderViewModel(
        new MissedOrderViewModelImpl(
            interactorComponent.getMissedOrderUseCase()
        )
    );
    mainApplication.setUpdateMessageViewModel(
        updateMessageViewModel
    );
    mainApplication.setCurrentCostPollingViewModel(
        new CurrentCostPollingViewModelImpl(
            interactorComponent.getCurrentCostPollingUseCase()
        )
    );
    mainApplication.setServerTimeViewModel(
        serverTimeViewModel
    );
    mainApplication.setAutoRouter(autoRouter);
    mainApplication.setLifeCycleCallbacks(autoRouter);
  }

  @Override
  public void inject(BaseActivity baseActivity) {
    baseActivity.setExecutorStateViewModel(executorStateViewModel);
    baseActivity.setUpdateMessageViewModel(updateMessageViewModel);
    baseActivity.setAnnouncementViewModel(announcementViewModel);
    baseActivity.setServerConnectionViewModel(serverConnectionViewModel);
    baseActivity.setServerTimeViewModel(serverTimeViewModel);
  }

  @Override
  public void inject(FcmService fcmService) {
    fcmService.setAnnouncementViewModel(announcementViewModel);
  }

  @Override
  public void inject(LoginFragment loginFragment) {
    loginFragment.setAppSettings(appSettingsService);
    loginFragment.setPhoneViewModel(
        getViewModelInstance(
            loginFragment,
            PhoneViewModelImpl.class,
            new PhoneViewModelImpl(interactorComponent.getLoginUseCase())
        )
    );
  }

  @Override
  public void inject(PasswordFragment passwordFragment) {
    passwordFragment.setSmsButtonViewModel(
        getViewModelInstance(
            passwordFragment,
            SmsButtonViewModelImpl.class,
            new SmsButtonViewModelImpl(interactorComponent.getSmsUseCase())
        )
    );
    passwordFragment.setCodeHeaderViewModel(
        getViewModelInstance(
            passwordFragment,
            CodeHeaderViewModelImpl.class,
            new CodeHeaderViewModelImpl(
                new LoginSharer(
                    appSettingsService
                )
            )
        )
    );
    passwordFragment.setCodeViewModel(
        getViewModelInstance(
            passwordFragment,
            CodeViewModelImpl.class,
            new CodeViewModelImpl(interactorComponent.getPasswordUseCase())
        )
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
        getViewModelInstance(
            mapFragment,
            MapViewModelImpl.class,
            new MapViewModelImpl(interactorComponent.getHeatMapUseCase())
        )
    );
    mapFragment.setGeoLocationViewModel(
        getViewModelInstance(
            mapFragment,
            GeoLocationViewModelImpl.class,
            new GeoLocationViewModelImpl(interactorComponent.getGeoLocationUseCase())
        )
    );
  }

  @Override
  public void inject(OnlineFragment onlineFragment) {
    onlineFragment.setOnlineSwitchViewModel(
        getViewModelInstance(
            onlineFragment,
            OnlineSwitchViewModelImpl.class,
            new OnlineSwitchViewModelImpl(
                interactorComponent.getExecutorStateNotOnlineUseCase(),
                interactorComponent.getExecutorStateUseCase()
            )
        )
    );
    onlineFragment.setOnlineButtonViewModel(
        getViewModelInstance(
            onlineFragment,
            OnlineButtonViewModelImpl.class,
            new OnlineButtonViewModelImpl(
                interactorComponent.getSelectedVehiclesAndOptionsUseCase()
            )
        )
    );
  }

  @Override
  public void inject(GoOnlineFragment goOnlineFragment) {
    goOnlineFragment.setOnlineButtonViewModel(
        getViewModelInstance(
            goOnlineFragment,
            OnlineButtonViewModelImpl.class,
            new OnlineButtonViewModelImpl(interactorComponent.getVehiclesAndOptionsUseCase())
        )
    );
  }

  @Override
  public void inject(ChooseVehicleFragment chooseVehicleFragment) {
    chooseVehicleFragment.setChooseVehicleViewModel(
        getViewModelInstance(
            chooseVehicleFragment,
            ChooseVehicleViewModelImpl.class,
            new ChooseVehicleViewModelImpl(interactorComponent.getVehicleChoiceUseCase())
        )
    );
  }

  @Override
  public void inject(VehicleOptionsFragment vehicleOptionsFragment) {
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        getViewModelInstance(
            vehicleOptionsFragment,
            VehicleOptionsViewModelImpl.class,
            new VehicleOptionsViewModelImpl(interactorComponent.getVehicleOptionsUseCase())
        )
    );
  }

  @Override
  public void inject(SelectedVehicleOptionsFragment vehicleOptionsFragment) {
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        getViewModelInstance(
            vehicleOptionsFragment,
            VehicleOptionsViewModelImpl.class,
            new VehicleOptionsViewModelImpl(interactorComponent.getCurrentVehicleOptionsUseCase())
        )
    );
  }

  @Override
  public void inject(SelectedVehicleFragment selectedVehicleFragment) {
    selectedVehicleFragment.setSelectedVehicleViewModel(
        getViewModelInstance(
            selectedVehicleFragment,
            SelectedVehicleViewModelImpl.class,
            new SelectedVehicleViewModelImpl(interactorComponent.getSelectedVehicleUseCase())
        )
    );
    selectedVehicleFragment.setChooseVehicleViewModel(
        getViewModelInstance(
            selectedVehicleFragment,
            ChooseVehicleViewModelImpl.class,
            new ChooseVehicleViewModelImpl(interactorComponent.getCurrentVehicleChoiceUseCase())
        )
    );
  }

  @Override
  public void inject(ServicesFragment servicesFragment) {
    ServicesListItems servicesListItems = new ServicesListItems();
    ServicesSliderViewModelImpl servicesSliderViewModel = getViewModelInstance(
        servicesFragment,
        ServicesSliderViewModelImpl.class,
        new ServicesSliderViewModelImpl(
            servicesListItems
        )
    );
    servicesFragment.setServicesSliderViewModel(servicesSliderViewModel);
    servicesFragment.setServicesViewModel(
        getViewModelInstance(
            servicesFragment,
            ServicesViewModelImpl.class,
            new ServicesViewModelImpl(
                interactorComponent.getServicesUseCase(),
                servicesSliderViewModel,
                servicesListItems
            )
        )
    );
  }

  @Override
  public void inject(DriverOrderConfirmationFragment driverOrderConfirmationFragment) {
    driverOrderConfirmationFragment.setOrderConfirmationViewModel(
        getViewModelInstance(
            driverOrderConfirmationFragment,
            OrderConfirmationViewModelImpl.class,
            new OrderConfirmationViewModelImpl(interactorComponent.getOrderConfirmationUseCase())
        )
    );
    driverOrderConfirmationFragment.setOrderViewModel(
        getViewModelInstance(
            driverOrderConfirmationFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getOrderUseCase(), timeUtils)
        )
    );
  }

  @Override
  public void inject(ClientOrderConfirmationFragment clientOrderConfirmationFragment) {
    clientOrderConfirmationFragment.setOrderViewModel(
        getViewModelInstance(
            clientOrderConfirmationFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getOrderUseCase(), timeUtils)
        )
    );
  }

  @Override
  public void inject(ClientOrderConfirmationTimeFragment clientOrderConfirmationTimeFragment) {
    clientOrderConfirmationTimeFragment.setClientOrderConfirmationTimeViewModel(
        getViewModelInstance(
            clientOrderConfirmationTimeFragment,
            ClientOrderConfirmationTimeViewModelImpl.class,
            new ClientOrderConfirmationTimeViewModelImpl(
                interactorComponent.getExecutorStateUseCase()
            )
        )
    );
  }

  @Override
  public void inject(MovingToClientFragment movingToClientFragment) {
    movingToClientFragment.setMovingToClientViewModel(
        getViewModelInstance(
            movingToClientFragment,
            MovingToClientViewModelImpl.class,
            new MovingToClientViewModelImpl(interactorComponent.getMovingToClientUseCase())
        )
    );
    movingToClientFragment.setOrderViewModel(
        getViewModelInstance(
            movingToClientFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getOrderUseCase(), timeUtils)
        )
    );
    movingToClientFragment.setShakeItPlayer(
        singleShakePlayer
    );
  }

  @Override
  public void inject(MovingToClientDetailsFragment movingToClientDetailsFragment) {
    movingToClientDetailsFragment.setOrderViewModel(
        getViewModelInstance(
            movingToClientDetailsFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getOrderUseCase(), timeUtils)
        )
    );
  }

  @Override
  public void inject(MovingToClientRouteFragment movingToClientRouteFragment) {
    movingToClientRouteFragment.setOrderRouteViewModel(
        getViewModelInstance(
            movingToClientRouteFragment,
            OrderRouteViewModelImpl.class,
            new OrderRouteViewModelImpl(interactorComponent.getOrderRouteUseCase())
        )
    );
  }

  @Override
  public void inject(WaitingForClientFragment waitingForClientFragment) {
    waitingForClientFragment.setWaitingForClientViewModel(
        getViewModelInstance(
            waitingForClientFragment,
            WaitingForClientViewModelImpl.class,
            new WaitingForClientViewModelImpl(interactorComponent.getWaitingForClientUseCase())
        )
    );
    waitingForClientFragment.setOrderViewModel(
        getViewModelInstance(
            waitingForClientFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getOrderUseCase(), timeUtils)
        )
    );
    waitingForClientFragment.setShakeItPlayer(
        singleShakePlayer
    );
  }

  @Override
  public void inject(WaitingForClientRouteFragment waitingForClientRouteFragment) {
    waitingForClientRouteFragment.setOrderRouteViewModel(
        getViewModelInstance(
            waitingForClientRouteFragment,
            OrderRouteViewModelImpl.class,
            new OrderRouteViewModelImpl(interactorComponent.getOrderRouteUseCase())
        )
    );
  }

  @Override
  public void inject(OrderFulfillmentFragment orderFulfillmentFragment) {
    orderFulfillmentFragment.setOrderTimeViewModel(
        getViewModelInstance(
            orderFulfillmentFragment,
            OrderTimeViewModelImpl.class,
            new OrderTimeViewModelImpl(interactorComponent.getOrderFulfillmentTimeUseCase())
        )
    );
    orderFulfillmentFragment.setOrderCostViewModel(
        getViewModelInstance(
            orderFulfillmentFragment,
            OrderCostViewModelImpl.class,
            new OrderCostViewModelImpl(interactorComponent.getOrderCurrentCostUseCase())
        )
    );
    orderFulfillmentFragment.setNextRoutePointViewModel(
        getViewModelInstance(
            orderFulfillmentFragment,
            NextRoutePointViewModelImpl.class,
            new NextRoutePointViewModelImpl(interactorComponent.getOrderRouteUseCase())
        )
    );
    orderFulfillmentFragment.setOrderRouteViewModel(
        getViewModelInstance(
            orderFulfillmentFragment,
            OrderRouteViewModelImpl.class,
            new OrderRouteViewModelImpl(interactorComponent.getOrderRouteUseCase())
        )
    );
    orderFulfillmentFragment.setShakeItPlayer(
        singleShakePlayer
    );
  }

  @Override
  public void inject(OrderFulfillmentDetailsFragment orderFulfillmentDetailsFragment) {
    orderFulfillmentDetailsFragment.setOrderViewModel(
        getViewModelInstance(
            orderFulfillmentDetailsFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getOrderUseCase(), timeUtils)
        )
    );
  }

  @Override
  public void inject(OrderFulfillmentActionsDialogFragment orderFulfillmentActionsDialogFragment) {
    orderFulfillmentActionsDialogFragment.setNextRoutePointViewModel(
        getViewModelInstance(
            orderFulfillmentActionsDialogFragment,
            NextRoutePointViewModelImpl.class,
            new NextRoutePointViewModelImpl(interactorComponent.getOrderRouteUseCase())
        )
    );
  }

  @Override
  public void inject(OrderRouteFragment orderRouteFragment) {
    orderRouteFragment.setOrderRouteViewModel(
        getViewModelInstance(
            orderRouteFragment,
            OrderRouteViewModelImpl.class,
            new OrderRouteViewModelImpl(interactorComponent.getOrderRouteUseCase())
        )
    );
  }

  @Override
  public void inject(CallToClientFragment callToClientFragment) {
    callToClientFragment.setCallToClientViewModel(
        getViewModelInstance(
            callToClientFragment,
            CallToClientViewModelImpl.class,
            new CallToClientViewModelImpl(interactorComponent.getCallToClientUseCase())
        )
    );
  }

  @Override
  public void inject(CallToOperatorFragment callToOperatorFragment) {
    callToOperatorFragment.setCallToOperatorViewModel(
        getViewModelInstance(
            callToOperatorFragment,
            CallToOperatorViewModelImpl.class,
            new CallToOperatorViewModelImpl()
        )
    );
  }

  @Override
  public void inject(CancelOrderDialogFragment cancelOrderDialogFragment) {
    cancelOrderDialogFragment.setCancelOrderViewModel(
        getViewModelInstance(
            cancelOrderDialogFragment,
            CancelOrderViewModelImpl.class,
            new CancelOrderViewModelImpl(interactorComponent.getCancelOrderUseCase())
        )
    );
    cancelOrderDialogFragment.setCancelOrderReasonsViewModel(
        getViewModelInstance(
            cancelOrderDialogFragment,
            CancelOrderReasonsViewModelImpl.class,
            new CancelOrderReasonsViewModelImpl(interactorComponent.getCancelOrderReasonsUseCase())
        )
    );
  }

  @Override
  public void inject(BalanceFragment balanceFragment) {
    balanceFragment.setBalanceViewModel(
        getViewModelInstance(
            balanceFragment,
            BalanceViewModelImpl.class,
            new BalanceViewModelImpl(interactorComponent.getExecutorBalanceUseCase())
        )
    );
  }

  @Override
  public void inject(BalanceSummaryFragment balanceSummaryFragment) {
    balanceSummaryFragment.setBalanceViewModel(
        getViewModelInstance(
            balanceSummaryFragment,
            BalanceViewModelImpl.class,
            new BalanceViewModelImpl(interactorComponent.getExecutorBalanceUseCase())
        )
    );
  }

  @Override
  public void inject(MenuFragment menuFragment) {
    menuFragment.setBalanceViewModel(
        getViewModelInstance(
            menuFragment,
            BalanceViewModelImpl.class,
            new BalanceViewModelImpl(interactorComponent.getExecutorBalanceUseCase())
        )
    );
    menuFragment.setOnlineSwitchViewModel(
        getViewModelInstance(
            menuFragment,
            OnlineSwitchViewModelImpl.class,
            new OnlineSwitchViewModelImpl(
                interactorComponent.getExecutorStateExitUseCase(),
                interactorComponent.getExecutorStateUseCase()
            )
        )
    );
  }

  @Override
  public void inject(ServerConnectionFragment serverConnectionFragment) {
    serverConnectionFragment.setServerConnectionViewModel(
        serverConnectionViewModel
    );
  }

  @Override
  public void inject(OrderCostDetailsFragment orderCostDetailsFragment) {
    orderCostDetailsFragment.setOrderCostDetailsViewModel(
        getViewModelInstance(
            orderCostDetailsFragment,
            OrderCostDetailsViewModelImpl.class,
            new OrderCostDetailsViewModelImpl(interactorComponent.getOrderCostDetailsUseCase())
        )
    );
    orderCostDetailsFragment.setConfirmOrderPaymentViewModel(
        getViewModelInstance(
            orderCostDetailsFragment,
            ConfirmOrderPaymentViewModelImpl.class,
            new ConfirmOrderPaymentViewModelImpl(
                interactorComponent.getConfirmOrderPaymentUseCase()
            )
        )
    );
    orderCostDetailsFragment.setShakeItPlayer(singleShakePlayer);
  }

  @Override
  public void inject(OrderCostDetailsOrderDetailsFragment orderCostDetailsOrderDetailsFragment) {
    orderCostDetailsOrderDetailsFragment.setOrderViewModel(
        getViewModelInstance(
            orderCostDetailsOrderDetailsFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getOrderUseCase(), timeUtils)
        )
    );
  }

  @Override
  public void inject(OrderCostDetailsRouteFragment orderCostDetailsRouteFragment) {
    orderCostDetailsRouteFragment.setOrderRouteViewModel(
        getViewModelInstance(
            orderCostDetailsRouteFragment,
            OrderRouteViewModelImpl.class,
            new OrderRouteViewModelImpl(interactorComponent.getOrderRouteUseCase())
        )
    );
  }

  @Override
  public void inject(ProfileFragment profileFragment) {
    profileFragment.setAppSettings(appSettingsService);
  }

  @Override
  public void inject(DriverPreOrderConfirmationFragment driverPreOrderConfirmationFragment) {
    driverPreOrderConfirmationFragment.setShakeItPlayer(singleShakePlayer);
    driverPreOrderConfirmationFragment.setOrderConfirmationViewModel(
        getViewModelInstance(
            driverPreOrderConfirmationFragment,
            OrderConfirmationViewModelImpl.class,
            new OrderConfirmationViewModelImpl(interactorComponent.getPreOrderConfirmationUseCase())
        )
    );
    driverPreOrderConfirmationFragment.setOrderViewModel(
        getViewModelInstance(
            driverPreOrderConfirmationFragment,
            OrderViewModelImpl.class,
            new OrderViewModelImpl(interactorComponent.getPreOrderUseCase(), timeUtils)
        )
    );
  }

  private <V extends ViewModel> V getViewModelInstance(
      @NonNull Fragment fragment,
      @NonNull Class<V> vClass,
      @NonNull V viewModel) {
    return ViewModelProviders.of(
        fragment,
        new ViewModelFactory<>(viewModel)
    ).get(vClass);
  }
}
