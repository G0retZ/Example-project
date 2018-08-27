package com.cargopull.executor_driver.di;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.application.AutoRouterImpl;
import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.FcmService;
import com.cargopull.executor_driver.application.MainApplication;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenterImpl;
import com.cargopull.executor_driver.backend.ringtone.SingleRingTonePlayer;
import com.cargopull.executor_driver.backend.settings.AppPreferences;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.vibro.NewPatternMapper;
import com.cargopull.executor_driver.backend.vibro.OldPatternMapper;
import com.cargopull.executor_driver.backend.vibro.SingleShakePlayer;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.AuthorizationInterceptor;
import com.cargopull.executor_driver.backend.web.ConnectivityInterceptor;
import com.cargopull.executor_driver.backend.web.DeprecatedVersionInterceptor;
import com.cargopull.executor_driver.backend.web.ReceiveTokenInterceptor;
import com.cargopull.executor_driver.backend.web.SendTokenInterceptor;
import com.cargopull.executor_driver.backend.web.SendVersionInterceptor;
import com.cargopull.executor_driver.backend.web.TokenKeeper;
import com.cargopull.executor_driver.backend.websocket.PersonalQueueListener;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.LoginValidator;
import com.cargopull.executor_driver.entity.PasswordValidator;
import com.cargopull.executor_driver.entity.PhoneNumberValidator;
import com.cargopull.executor_driver.entity.Vehicle;
import com.cargopull.executor_driver.gateway.CallToClientGatewayImpl;
import com.cargopull.executor_driver.gateway.CancelOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.CancelOrderReasonApiMapper;
import com.cargopull.executor_driver.gateway.CancelOrderReasonsGatewayImpl;
import com.cargopull.executor_driver.gateway.ConfirmOrderPaymentGatewayImpl;
import com.cargopull.executor_driver.gateway.CurrentCostPollingGatewayImpl;
import com.cargopull.executor_driver.gateway.CurrentCostPollingTimersApiMapper;
import com.cargopull.executor_driver.gateway.CurrentVehicleOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.ErrorMapper;
import com.cargopull.executor_driver.gateway.ExecutorBalanceApiMapper;
import com.cargopull.executor_driver.gateway.ExecutorBalanceGatewayImpl;
import com.cargopull.executor_driver.gateway.ExecutorStateApiMapper;
import com.cargopull.executor_driver.gateway.ExecutorStateGatewayImpl;
import com.cargopull.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;
import com.cargopull.executor_driver.gateway.GeoLocationGatewayImpl;
import com.cargopull.executor_driver.gateway.GeoTrackingGatewayImpl;
import com.cargopull.executor_driver.gateway.HeatMapGatewayImpl;
import com.cargopull.executor_driver.gateway.LastUsedVehicleGatewayImpl;
import com.cargopull.executor_driver.gateway.MissedOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.MovingToClientGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderApiMapper;
import com.cargopull.executor_driver.gateway.OrderConfirmationGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderCostDetailsApiMapper;
import com.cargopull.executor_driver.gateway.OrderCostDetailsGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderCurrentCostApiMapper;
import com.cargopull.executor_driver.gateway.OrderCurrentCostGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderGatewayImpl;
import com.cargopull.executor_driver.gateway.OrderRouteGatewayImpl;
import com.cargopull.executor_driver.gateway.PasswordGatewayImpl;
import com.cargopull.executor_driver.gateway.PreOrderConfirmationGatewayImpl;
import com.cargopull.executor_driver.gateway.PreOrderGatewayImpl;
import com.cargopull.executor_driver.gateway.RoutePointApiMapper;
import com.cargopull.executor_driver.gateway.SelectedVehicleAndOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.ServerConnectionGatewayImpl;
import com.cargopull.executor_driver.gateway.ServerTimeGatewayImpl;
import com.cargopull.executor_driver.gateway.ServiceApiMapper;
import com.cargopull.executor_driver.gateway.ServicesGatewayImpl;
import com.cargopull.executor_driver.gateway.SmsCodeMapper;
import com.cargopull.executor_driver.gateway.SmsGatewayImpl;
import com.cargopull.executor_driver.gateway.TokenKeeperImpl;
import com.cargopull.executor_driver.gateway.UpdateMessageGatewayImpl;
import com.cargopull.executor_driver.gateway.VehicleApiMapper;
import com.cargopull.executor_driver.gateway.VehicleOptionApiMapper;
import com.cargopull.executor_driver.gateway.VehicleOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.VehiclesAndOptionsGatewayImpl;
import com.cargopull.executor_driver.gateway.WaitingForClientGatewayImpl;
import com.cargopull.executor_driver.interactor.CallToClientUseCaseImpl;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsUseCase;
import com.cargopull.executor_driver.interactor.CancelOrderReasonsUseCaseImpl;
import com.cargopull.executor_driver.interactor.CancelOrderUseCase;
import com.cargopull.executor_driver.interactor.CancelOrderUseCaseImpl;
import com.cargopull.executor_driver.interactor.ConfirmOrderPaymentUseCaseImpl;
import com.cargopull.executor_driver.interactor.CurrentCostPollingUseCaseImpl;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCase;
import com.cargopull.executor_driver.interactor.ExecutorBalanceUseCaseImpl;
import com.cargopull.executor_driver.interactor.ExecutorStateNotOnlineUseCaseImpl;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCaseImpl;
import com.cargopull.executor_driver.interactor.GeoLocationUseCase;
import com.cargopull.executor_driver.interactor.GeoLocationUseCaseImpl;
import com.cargopull.executor_driver.interactor.MemoryDataSharer;
import com.cargopull.executor_driver.interactor.MissedOrderUseCaseImpl;
import com.cargopull.executor_driver.interactor.MovingToClientUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderCostDetailsUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderCurrentCostUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderFulfillmentTimeUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderRouteUseCaseImpl;
import com.cargopull.executor_driver.interactor.OrderUseCaseImpl;
import com.cargopull.executor_driver.interactor.ServerConnectionUseCaseImpl;
import com.cargopull.executor_driver.interactor.ServerTimeUseCaseImpl;
import com.cargopull.executor_driver.interactor.UpdateMessageUseCaseImpl;
import com.cargopull.executor_driver.interactor.WaitingForClientUseCaseImpl;
import com.cargopull.executor_driver.interactor.auth.LoginSharer;
import com.cargopull.executor_driver.interactor.auth.LoginUseCaseImpl;
import com.cargopull.executor_driver.interactor.auth.PasswordUseCaseImpl;
import com.cargopull.executor_driver.interactor.auth.SmsUseCaseImpl;
import com.cargopull.executor_driver.interactor.map.HeatMapUseCaseImpl;
import com.cargopull.executor_driver.interactor.services.ServicesUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.LastUsedVehicleGateway;
import com.cargopull.executor_driver.interactor.vehicle.SelectedVehicleUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceSharer;
import com.cargopull.executor_driver.interactor.vehicle.VehicleChoiceUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.VehicleOptionsUseCaseImpl;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway;
import com.cargopull.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCaseImpl;
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
import com.cargopull.executor_driver.utils.ErrorReporter;
import com.cargopull.executor_driver.utils.ErrorReporterImpl;
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
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class AppComponentImpl implements AppComponent {

  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final TimeUtils timeUtils;
  @NonNull
  private final AppSettingsService appSettingsService;
  @NonNull
  private final ApiService apiService;
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final PersonalQueueListener personalQueueListener;
  @NonNull
  private final AutoRouterImpl autoRouter;
  @NonNull
  private final ServerConnectionViewModel serverConnectionViewModel;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final CancelOrderUseCase cancelOrderUseCase;
  @NonNull
  private final CancelOrderReasonsUseCase cancelOrderReasonsUseCase;
  @NonNull
  private final ExecutorBalanceUseCase executorBalanceUseCase;
  @NonNull
  private final GeoLocationUseCase geoLocationUseCase;
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
  private final MemoryDataSharer<String> loginSharer;
  @NonNull
  private final MemoryDataSharer<Vehicle> vehicleChoiceSharer;
  @NonNull
  private final LastUsedVehicleGateway lastUsedVehicleGateway;
  @NonNull
  private final AnnouncementViewModel announcementViewModel;
  // Типа кастомный скоуп.
  @Nullable
  private VehiclesAndOptionsGateway vehiclesAndOptionsGateway;
  @Nullable
  private VehiclesAndOptionsGateway selectedVehiclesAndOptionsGateway;

  public AppComponentImpl(@NonNull Context appContext) {
    appContext = appContext.getApplicationContext();
    appSettingsService = new AppPreferences(appContext);
    TokenKeeper tokenKeeper = new TokenKeeperImpl(appSettingsService);
    OkHttpClient okHttpClient = initHttpClient(
        new ConnectivityInterceptor(appContext),
        new SendVersionInterceptor(),
        new DeprecatedVersionInterceptor(),
        new AuthorizationInterceptor(),
        new SendTokenInterceptor(tokenKeeper),
        new ReceiveTokenInterceptor(tokenKeeper)
    );
    apiService = initApiService(okHttpClient);
    stompClient = initStompClient(okHttpClient);
    loginSharer = new LoginSharer(appSettingsService);
    personalQueueListener = new PersonalQueueListener(stompClient, loginSharer);
    vehicleChoiceSharer = new VehicleChoiceSharer();
    lastUsedVehicleGateway = new LastUsedVehicleGatewayImpl(appSettingsService);
    errorReporter = new ErrorReporterImpl(loginSharer);
    timeUtils = new TimeUtilsImpl();
    serverConnectionViewModel = new ServerConnectionViewModelImpl(
        new ServerConnectionUseCaseImpl(
            new ServerConnectionGatewayImpl(
                stompClient
            )
        )
    );
    cancelOrderReasonsUseCase = new CancelOrderReasonsUseCaseImpl(
        errorReporter,
        new CancelOrderReasonsGatewayImpl(
            personalQueueListener,
            new CancelOrderReasonApiMapper()
        )
    );
    cancelOrderUseCase = new CancelOrderUseCaseImpl(
        cancelOrderReasonsUseCase, errorReporter,
        new CancelOrderGatewayImpl(
            stompClient
        )
    );
    executorBalanceUseCase = new ExecutorBalanceUseCaseImpl(
        errorReporter,
        new ExecutorBalanceGatewayImpl(
            personalQueueListener,
            new ExecutorBalanceApiMapper()
        )
    );
    executorStateUseCase = new ExecutorStateUseCaseImpl(
        errorReporter,
        new ExecutorStateGatewayImpl(
            personalQueueListener,
            new ExecutorStateApiMapper()
        )
    );
    geoLocationUseCase = new GeoLocationUseCaseImpl(
        new GeoLocationGatewayImpl(
            new GeolocationCenterImpl(
                appContext
            )
        ),
        new GeoTrackingGatewayImpl(
            stompClient
        ), executorStateUseCase
    );
    executorStateViewModel = new ExecutorStateViewModelImpl(
        executorStateUseCase
    );
    updateMessageViewModel = new UpdateMessageViewModelImpl(
        new UpdateMessageUseCaseImpl(
            errorReporter,
            new UpdateMessageGatewayImpl(
                stompClient
            ),
            loginSharer
        )
    );
    serverTimeViewModel = new ServerTimeViewModelImpl(
        new ServerTimeUseCaseImpl(
            errorReporter,
            new ServerTimeGatewayImpl(
                stompClient
            ),
            loginSharer,
            timeUtils
        )
    );
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
            cancelOrderReasonsUseCase
        )
    );
    mainApplication.setBalanceViewModel(
        new BalanceViewModelImpl(
            executorBalanceUseCase
        )
    );
    mainApplication.setExecutorStateViewModel(
        executorStateViewModel
    );
    mainApplication.setGeoLocationViewModel(
        new GeoLocationViewModelImpl(
            geoLocationUseCase
        )
    );
    mainApplication.setMissedOrderViewModel(
        new MissedOrderViewModelImpl(
            new MissedOrderUseCaseImpl(
                errorReporter,
                new MissedOrderGatewayImpl(
                    personalQueueListener
                )
            )
        )
    );
    mainApplication.setUpdateMessageViewModel(
        updateMessageViewModel
    );
    mainApplication.setCurrentCostPollingViewModel(
        new CurrentCostPollingViewModelImpl(
            new CurrentCostPollingUseCaseImpl(
                errorReporter,
                new CurrentCostPollingGatewayImpl(
                    personalQueueListener,
                    stompClient,
                    new CurrentCostPollingTimersApiMapper()
                )
            )
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
        ViewModelProviders.of(
            loginFragment,
            new ViewModelFactory<>(
                new PhoneViewModelImpl(
                    new LoginUseCaseImpl(
                        loginSharer,
                        new LoginValidator()
                    )
                )
            )
        ).get(PhoneViewModelImpl.class)
    );
  }

  @Override
  public void inject(PasswordFragment passwordFragment) {
    passwordFragment.setSmsButtonViewModel(
        ViewModelProviders.of(
            passwordFragment,
            new ViewModelFactory<>(
                new SmsButtonViewModelImpl(
                    new SmsUseCaseImpl(
                        new SmsGatewayImpl(apiService),
                        loginSharer,
                        new PhoneNumberValidator()
                    )
                )
            )
        ).get(SmsButtonViewModelImpl.class)
    );
    passwordFragment.setCodeHeaderViewModel(
        ViewModelProviders.of(
            passwordFragment,
            new ViewModelFactory<>(
                new CodeHeaderViewModelImpl(
                    new LoginSharer(
                        appSettingsService
                    )
                )
            )
        ).get(CodeHeaderViewModelImpl.class)
    );
    passwordFragment.setCodeViewModel(
        ViewModelProviders.of(
            passwordFragment,
            new ViewModelFactory<>(
                new CodeViewModelImpl(
                    new PasswordUseCaseImpl(
                        new PasswordGatewayImpl(apiService),
                        loginSharer,
                        new PasswordValidator()
                    )
                )
            )
        ).get(CodeViewModelImpl.class)
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
        ViewModelProviders.of(
            mapFragment,
            new ViewModelFactory<>(
                new MapViewModelImpl(
                    new HeatMapUseCaseImpl(
                        new HeatMapGatewayImpl(apiService)
                    )
                )
            )).get(MapViewModelImpl.class)
    );
    mapFragment.setGeoLocationViewModel(
        ViewModelProviders.of(
            mapFragment,
            new ViewModelFactory<>(
                new GeoLocationViewModelImpl(
                    geoLocationUseCase
                )
            )
        ).get(GeoLocationViewModelImpl.class)
    );
  }

  @Override
  public void inject(OnlineFragment onlineFragment) {
    selectedVehiclesAndOptionsGateway = new SelectedVehicleAndOptionsGatewayImpl(
        apiService,
        new VehicleOptionApiMapper(),
        new VehicleApiMapper(
            new VehicleOptionApiMapper()
        ),
        new ErrorMapper()
    );
    onlineFragment.setOnlineSwitchViewModel(
        ViewModelProviders.of(
            onlineFragment,
            new ViewModelFactory<>(
                new OnlineSwitchViewModelImpl(
                    new ExecutorStateNotOnlineUseCaseImpl(
                        errorReporter,
                        new ExecutorStateSwitchGatewayImpl(stompClient),
                        executorStateUseCase,
                        ExecutorState.ONLINE
                    ),
                    executorStateUseCase)
            )
        ).get(OnlineSwitchViewModelImpl.class)
    );
    onlineFragment.setOnlineButtonViewModel(
        ViewModelProviders.of(
            onlineFragment,
            new ViewModelFactory<>(
                new OnlineButtonViewModelImpl(
                    new VehiclesAndOptionsUseCaseImpl(
                        errorReporter,
                        selectedVehiclesAndOptionsGateway,
                        vehicleChoiceSharer,
                        lastUsedVehicleGateway
                    )
                )
            )
        ).get(OnlineButtonViewModelImpl.class)
    );
  }

  @Override
  public void inject(GoOnlineFragment goOnlineFragment) {
    vehiclesAndOptionsGateway = new VehiclesAndOptionsGatewayImpl(
        apiService,
        new VehicleOptionApiMapper(),
        new VehicleApiMapper(
            new VehicleOptionApiMapper()
        ),
        new ErrorMapper()
    );
    goOnlineFragment.setOnlineButtonViewModel(
        ViewModelProviders.of(
            goOnlineFragment,
            new ViewModelFactory<>(
                new OnlineButtonViewModelImpl(
                    new VehiclesAndOptionsUseCaseImpl(
                        errorReporter,
                        vehiclesAndOptionsGateway,
                        vehicleChoiceSharer,
                        lastUsedVehicleGateway
                    )
                )
            )
        ).get(OnlineButtonViewModelImpl.class)
    );
  }

  @Override
  public void inject(ChooseVehicleFragment chooseVehicleFragment) {
    if (vehiclesAndOptionsGateway == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    chooseVehicleFragment.setChooseVehicleViewModel(
        ViewModelProviders.of(
            chooseVehicleFragment,
            new ViewModelFactory<>(
                new ChooseVehicleViewModelImpl(
                    new VehicleChoiceUseCaseImpl(
                        errorReporter, vehiclesAndOptionsGateway, vehicleChoiceSharer
                    )
                )
            )
        ).get(ChooseVehicleViewModelImpl.class)
    );
  }

  @Override
  public void inject(VehicleOptionsFragment vehicleOptionsFragment) {
    if (vehiclesAndOptionsGateway == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        ViewModelProviders.of(
            vehicleOptionsFragment,
            new ViewModelFactory<>(
                new VehicleOptionsViewModelImpl(
                    new VehicleOptionsUseCaseImpl(
                        errorReporter,
                        new VehicleOptionsGatewayImpl(
                            apiService
                        ),
                        vehicleChoiceSharer,
                        lastUsedVehicleGateway,
                        vehiclesAndOptionsGateway
                    )
                )
            )
        ).get(VehicleOptionsViewModelImpl.class)
    );
  }

  @Override
  public void inject(SelectedVehicleOptionsFragment vehicleOptionsFragment) {
    if (selectedVehiclesAndOptionsGateway == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        ViewModelProviders.of(
            vehicleOptionsFragment,
            new ViewModelFactory<>(
                new VehicleOptionsViewModelImpl(
                    new VehicleOptionsUseCaseImpl(
                        errorReporter,
                        new CurrentVehicleOptionsGatewayImpl(
                            apiService
                        ),
                        vehicleChoiceSharer,
                        lastUsedVehicleGateway,
                        selectedVehiclesAndOptionsGateway
                    )
                )
            )
        ).get(VehicleOptionsViewModelImpl.class)
    );
  }

  @Override
  public void inject(SelectedVehicleFragment selectedVehicleFragment) {
    if (vehiclesAndOptionsGateway == null && selectedVehiclesAndOptionsGateway == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    selectedVehicleFragment.setSelectedVehicleViewModel(
        ViewModelProviders.of(
            selectedVehicleFragment,
            new ViewModelFactory<>(
                new SelectedVehicleViewModelImpl(
                    new SelectedVehicleUseCaseImpl(
                        vehicleChoiceSharer
                    )
                )
            )
        ).get(SelectedVehicleViewModelImpl.class)
    );
    selectedVehicleFragment.setChooseVehicleViewModel(
        ViewModelProviders.of(
            selectedVehicleFragment,
            new ViewModelFactory<>(
                new ChooseVehicleViewModelImpl(
                    new VehicleChoiceUseCaseImpl(
                        errorReporter,
                        selectedVehiclesAndOptionsGateway == null ?
                            vehiclesAndOptionsGateway :
                            selectedVehiclesAndOptionsGateway,
                        vehicleChoiceSharer)
                )
            )
        ).get(ChooseVehicleViewModelImpl.class)
    );
  }

  @Override
  public void inject(ServicesFragment servicesFragment) {
    ServicesListItems servicesListItems = new ServicesListItems();
    ServicesSliderViewModelImpl servicesSliderViewModel = ViewModelProviders.of(
        servicesFragment,
        new ViewModelFactory<>(
            new ServicesSliderViewModelImpl(
                servicesListItems
            )
        )
    ).get(ServicesSliderViewModelImpl.class);
    servicesFragment.setServicesSliderViewModel(servicesSliderViewModel);
    servicesFragment.setServicesViewModel(
        ViewModelProviders.of(
            servicesFragment,
            new ViewModelFactory<>(
                new ServicesViewModelImpl(
                    new ServicesUseCaseImpl(
                        errorReporter,
                        new ServicesGatewayImpl(
                            apiService,
                            new ServiceApiMapper()
                        )
                    ),
                    servicesSliderViewModel,
                    servicesListItems)
            )
        ).get(ServicesViewModelImpl.class)
    );
  }

  @Override
  public void inject(DriverOrderConfirmationFragment driverOrderConfirmationFragment) {
    driverOrderConfirmationFragment.setOrderConfirmationViewModel(
        ViewModelProviders.of(
            driverOrderConfirmationFragment,
            new ViewModelFactory<>(
                new OrderConfirmationViewModelImpl(
                    new OrderConfirmationUseCaseImpl(
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.DRIVER_ORDER_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        new OrderConfirmationGatewayImpl(stompClient),
                        loginSharer
                    )
                )
            )
        ).get(OrderConfirmationViewModelImpl.class)
    );
    driverOrderConfirmationFragment.setOrderViewModel(
        ViewModelProviders.of(
            driverOrderConfirmationFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.DRIVER_ORDER_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
  }

  @Override
  public void inject(ClientOrderConfirmationFragment clientOrderConfirmationFragment) {
    clientOrderConfirmationFragment.setOrderViewModel(
        ViewModelProviders.of(
            clientOrderConfirmationFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.CLIENT_ORDER_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
  }

  @Override
  public void inject(ClientOrderConfirmationTimeFragment clientOrderConfirmationTimeFragment) {
    clientOrderConfirmationTimeFragment.setClientOrderConfirmationTimeViewModel(
        ViewModelProviders.of(
            clientOrderConfirmationTimeFragment,
            new ViewModelFactory<>(
                new ClientOrderConfirmationTimeViewModelImpl(
                    executorStateUseCase
                )
            )
        ).get(ClientOrderConfirmationTimeViewModelImpl.class)
    );
  }

  @Override
  public void inject(MovingToClientFragment movingToClientFragment) {
    movingToClientFragment.setMovingToClientViewModel(
        ViewModelProviders.of(
            movingToClientFragment,
            new ViewModelFactory<>(
                new MovingToClientViewModelImpl(
                    new MovingToClientUseCaseImpl(
                        new MovingToClientGatewayImpl(stompClient)
                    )
                )
            )
        ).get(MovingToClientViewModelImpl.class)
    );
    movingToClientFragment.setOrderViewModel(
        ViewModelProviders.of(
            movingToClientFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.MOVING_TO_CLIENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
    movingToClientFragment.setShakeItPlayer(
        singleShakePlayer
    );
  }

  @Override
  public void inject(MovingToClientDetailsFragment movingToClientDetailsFragment) {
    movingToClientDetailsFragment.setOrderViewModel(
        ViewModelProviders.of(
            movingToClientDetailsFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.MOVING_TO_CLIENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
  }

  @Override
  public void inject(MovingToClientRouteFragment movingToClientRouteFragment) {
    movingToClientRouteFragment.setOrderRouteViewModel(
        ViewModelProviders.of(
            movingToClientRouteFragment,
            new ViewModelFactory<>(
                new OrderRouteViewModelImpl(
                    new OrderRouteUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.MOVING_TO_CLIENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(OrderRouteViewModelImpl.class)
    );
  }

  @Override
  public void inject(WaitingForClientFragment waitingForClientFragment) {
    waitingForClientFragment.setWaitingForClientViewModel(
        ViewModelProviders.of(
            waitingForClientFragment,
            new ViewModelFactory<>(
                new WaitingForClientViewModelImpl(
                    new WaitingForClientUseCaseImpl(
                        new WaitingForClientGatewayImpl(stompClient)
                    )
                )
            )
        ).get(WaitingForClientViewModelImpl.class)
    );
    waitingForClientFragment.setOrderViewModel(
        ViewModelProviders.of(
            waitingForClientFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.WAITING_FOR_CLIENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
    waitingForClientFragment.setShakeItPlayer(
        singleShakePlayer
    );
  }

  @Override
  public void inject(WaitingForClientRouteFragment waitingForClientRouteFragment) {
    waitingForClientRouteFragment.setOrderRouteViewModel(
        ViewModelProviders.of(
            waitingForClientRouteFragment,
            new ViewModelFactory<>(
                new OrderRouteViewModelImpl(
                    new OrderRouteUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.WAITING_FOR_CLIENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(OrderRouteViewModelImpl.class)
    );
  }

  @Override
  public void inject(OrderFulfillmentFragment orderFulfillmentFragment) {
    orderFulfillmentFragment.setOrderTimeViewModel(
        ViewModelProviders.of(
            orderFulfillmentFragment,
            new ViewModelFactory<>(
                new OrderTimeViewModelImpl(
                    new OrderFulfillmentTimeUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        timeUtils
                    )
                )
            )
        ).get(OrderTimeViewModelImpl.class)
    );
    orderFulfillmentFragment.setOrderCostViewModel(
        ViewModelProviders.of(
            orderFulfillmentFragment,
            new ViewModelFactory<>(
                new OrderCostViewModelImpl(
                    new OrderCurrentCostUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderCurrentCostGatewayImpl(
                            stompClient,
                            new OrderCurrentCostApiMapper()
                        )
                    )
                )
            )
        ).get(OrderCostViewModelImpl.class)
    );
    orderFulfillmentFragment.setNextRoutePointViewModel(
        ViewModelProviders.of(
            orderFulfillmentFragment,
            new ViewModelFactory<>(
                new NextRoutePointViewModelImpl(
                    new OrderRouteUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(NextRoutePointViewModelImpl.class)
    );
    orderFulfillmentFragment.setOrderRouteViewModel(
        ViewModelProviders.of(
            orderFulfillmentFragment,
            new ViewModelFactory<>(
                new OrderRouteViewModelImpl(
                    new OrderRouteUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(OrderRouteViewModelImpl.class)
    );
    orderFulfillmentFragment.setShakeItPlayer(
        singleShakePlayer
    );
  }

  @Override
  public void inject(OrderFulfillmentDetailsFragment orderFulfillmentDetailsFragment) {
    orderFulfillmentDetailsFragment.setOrderViewModel(
        ViewModelProviders.of(
            orderFulfillmentDetailsFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
  }

  @Override
  public void inject(OrderFulfillmentActionsDialogFragment orderFulfillmentActionsDialogFragment) {
    orderFulfillmentActionsDialogFragment.setNextRoutePointViewModel(
        ViewModelProviders.of(
            orderFulfillmentActionsDialogFragment,
            new ViewModelFactory<>(
                new NextRoutePointViewModelImpl(
                    new OrderRouteUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(NextRoutePointViewModelImpl.class)
    );
  }

  @Override
  public void inject(OrderRouteFragment orderRouteFragment) {
    orderRouteFragment.setOrderRouteViewModel(
        ViewModelProviders.of(
            orderRouteFragment,
            new ViewModelFactory<>(
                new OrderRouteViewModelImpl(
                    new OrderRouteUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(OrderRouteViewModelImpl.class)
    );
  }

  @Override
  public void inject(CallToClientFragment callToClientFragment) {
    callToClientFragment.setCallToClientViewModel(
        ViewModelProviders.of(
            callToClientFragment,
            new ViewModelFactory<>(
                new CallToClientViewModelImpl(
                    new CallToClientUseCaseImpl(
                        new CallToClientGatewayImpl(stompClient)
                    )
                )
            )
        ).get(CallToClientViewModelImpl.class)
    );
  }

  @Override
  public void inject(CallToOperatorFragment callToOperatorFragment) {
    callToOperatorFragment.setCallToOperatorViewModel(
        ViewModelProviders.of(
            callToOperatorFragment,
            new ViewModelFactory<>(
                new CallToOperatorViewModelImpl()
            )
        ).get(CallToOperatorViewModelImpl.class)
    );
  }

  @Override
  public void inject(CancelOrderDialogFragment cancelOrderDialogFragment) {
    cancelOrderDialogFragment.setCancelOrderViewModel(
        ViewModelProviders.of(
            cancelOrderDialogFragment,
            new ViewModelFactory<>(
                new CancelOrderViewModelImpl(
                    cancelOrderUseCase
                )
            )
        ).get(CancelOrderViewModelImpl.class)
    );
    cancelOrderDialogFragment.setCancelOrderReasonsViewModel(
        ViewModelProviders.of(
            cancelOrderDialogFragment,
            new ViewModelFactory<>(
                new CancelOrderReasonsViewModelImpl(
                    cancelOrderReasonsUseCase
                )
            )
        ).get(CancelOrderReasonsViewModelImpl.class)
    );
  }

  @Override
  public void inject(BalanceFragment balanceFragment) {
    balanceFragment.setBalanceViewModel(
        ViewModelProviders.of(
            balanceFragment,
            new ViewModelFactory<>(
                new BalanceViewModelImpl(
                    executorBalanceUseCase
                )
            )
        ).get(BalanceViewModelImpl.class)
    );
  }

  @Override
  public void inject(BalanceSummaryFragment balanceSummaryFragment) {
    balanceSummaryFragment.setBalanceViewModel(
        ViewModelProviders.of(
            balanceSummaryFragment,
            new ViewModelFactory<>(
                new BalanceViewModelImpl(
                    executorBalanceUseCase
                )
            )
        ).get(BalanceViewModelImpl.class)
    );
  }

  @Override
  public void inject(MenuFragment menuFragment) {
    menuFragment.setBalanceViewModel(
        ViewModelProviders.of(
            menuFragment,
            new ViewModelFactory<>(
                new BalanceViewModelImpl(
                    executorBalanceUseCase
                )
            )
        ).get(BalanceViewModelImpl.class)
    );
    menuFragment.setOnlineSwitchViewModel(
        ViewModelProviders.of(
            menuFragment,
            new ViewModelFactory<>(
                new OnlineSwitchViewModelImpl(
                    new ExecutorStateNotOnlineUseCaseImpl(
                        errorReporter,
                        new ExecutorStateSwitchGatewayImpl(stompClient),
                        executorStateUseCase,
                        ExecutorState.SHIFT_CLOSED,
                        ExecutorState.SHIFT_OPENED,
                        ExecutorState.ONLINE,
                        ExecutorState.DRIVER_ORDER_CONFIRMATION,
                        ExecutorState.CLIENT_ORDER_CONFIRMATION,
                        ExecutorState.MOVING_TO_CLIENT,
                        ExecutorState.WAITING_FOR_CLIENT,
                        ExecutorState.ORDER_FULFILLMENT,
                        ExecutorState.PAYMENT_CONFIRMATION
                    ),
                    executorStateUseCase)
            )
        ).get(OnlineSwitchViewModelImpl.class)
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
        ViewModelProviders.of(
            orderCostDetailsFragment,
            new ViewModelFactory<>(
                new OrderCostDetailsViewModelImpl(
                    new OrderCostDetailsUseCaseImpl(
                        errorReporter,
                        new OrderCostDetailsGatewayImpl(
                            personalQueueListener,
                            new OrderCostDetailsApiMapper()
                        )
                    )
                )
            )
        ).get(OrderCostDetailsViewModelImpl.class)
    );
    orderCostDetailsFragment.setConfirmOrderPaymentViewModel(
        ViewModelProviders.of(
            orderCostDetailsFragment,
            new ViewModelFactory<>(
                new ConfirmOrderPaymentViewModelImpl(
                    new ConfirmOrderPaymentUseCaseImpl(
                        new ConfirmOrderPaymentGatewayImpl(stompClient)
                    )
                )
            )
        ).get(ConfirmOrderPaymentViewModelImpl.class)
    );
    orderCostDetailsFragment.setShakeItPlayer(singleShakePlayer);
  }

  @Override
  public void inject(OrderCostDetailsOrderDetailsFragment orderCostDetailsOrderDetailsFragment) {
    orderCostDetailsOrderDetailsFragment.setOrderViewModel(
        ViewModelProviders.of(
            orderCostDetailsOrderDetailsFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.PAYMENT_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
  }

  @Override
  public void inject(OrderCostDetailsRouteFragment orderCostDetailsRouteFragment) {
    orderCostDetailsRouteFragment.setOrderRouteViewModel(
        ViewModelProviders.of(
            orderCostDetailsRouteFragment,
            new ViewModelFactory<>(
                new OrderRouteViewModelImpl(
                    new OrderRouteUseCaseImpl(
                        errorReporter,
                        new OrderGatewayImpl(
                            personalQueueListener,
                            ExecutorState.PAYMENT_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer,
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(OrderRouteViewModelImpl.class)
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
        ViewModelProviders.of(
            driverPreOrderConfirmationFragment,
            new ViewModelFactory<>(
                new OrderConfirmationViewModelImpl(
                    new OrderConfirmationUseCaseImpl(
                        new PreOrderGatewayImpl(
                            personalQueueListener,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        new PreOrderConfirmationGatewayImpl(
                            apiService
                        ),
                        loginSharer
                    )
                )
            )
        ).get(OrderConfirmationViewModelImpl.class)
    );
    driverPreOrderConfirmationFragment.setOrderViewModel(
        ViewModelProviders.of(
            driverPreOrderConfirmationFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        errorReporter,
                        new PreOrderGatewayImpl(
                            personalQueueListener,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        loginSharer
                    ),
                    timeUtils
                )
            )
        ).get(OrderViewModelImpl.class)
    );
  }

  private OkHttpClient initHttpClient(@NonNull Interceptor... interceptors) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS);
    for (Interceptor interceptor : interceptors) {
      builder.addInterceptor(interceptor);
    }
    // Add logging interceptor for debug build only
    if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(logging);
    }
    return builder.build();
  }

  private ApiService initApiService(OkHttpClient okHttpClient) {
    // build OkHttpClient builder
    return new Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService.class);
  }

  private StompClient initStompClient(OkHttpClient okHttpClient) {
    return Stomp.over(Stomp.ConnectionProvider.OKHTTP, BuildConfig.SOCKET_URL, null, okHttpClient);
  }
}
