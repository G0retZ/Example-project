package com.fasten.executor_driver.di;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.application.AutoRouterImpl;
import com.fasten.executor_driver.application.MainApplication;
import com.fasten.executor_driver.backend.geolocation.GeolocationCenterImpl;
import com.fasten.executor_driver.backend.settings.AppPreferences;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.AuthorizationInterceptor;
import com.fasten.executor_driver.backend.web.ConnectivityInterceptor;
import com.fasten.executor_driver.backend.web.ReceiveTokenInterceptor;
import com.fasten.executor_driver.backend.web.SendTokenInterceptor;
import com.fasten.executor_driver.backend.web.TokenKeeper;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.LoginValidator;
import com.fasten.executor_driver.entity.PasswordValidator;
import com.fasten.executor_driver.entity.PhoneNumberValidator;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.gateway.CallToClientGatewayImpl;
import com.fasten.executor_driver.gateway.CancelOrderGatewayImpl;
import com.fasten.executor_driver.gateway.CancelOrderReasonApiMapper;
import com.fasten.executor_driver.gateway.CurrentCostPollingGatewayImpl;
import com.fasten.executor_driver.gateway.CurrentCostPollingTimersApiMapper;
import com.fasten.executor_driver.gateway.CurrentVehicleOptionsGatewayImpl;
import com.fasten.executor_driver.gateway.ErrorMapper;
import com.fasten.executor_driver.gateway.ExecutorBalanceApiMapper;
import com.fasten.executor_driver.gateway.ExecutorBalanceGatewayImpl;
import com.fasten.executor_driver.gateway.ExecutorStateApiMapper;
import com.fasten.executor_driver.gateway.ExecutorStateGatewayImpl;
import com.fasten.executor_driver.gateway.ExecutorStateSwitchGatewayImpl;
import com.fasten.executor_driver.gateway.GeoLocationGatewayImpl;
import com.fasten.executor_driver.gateway.GeoTrackingGatewayImpl;
import com.fasten.executor_driver.gateway.HeatMapGatewayImpl;
import com.fasten.executor_driver.gateway.LastUsedVehicleGatewayImpl;
import com.fasten.executor_driver.gateway.MissedOrderGatewayImpl;
import com.fasten.executor_driver.gateway.MovingToClientApiMapper;
import com.fasten.executor_driver.gateway.MovingToClientGatewayImpl;
import com.fasten.executor_driver.gateway.OrderApiMapper;
import com.fasten.executor_driver.gateway.OrderConfirmationGatewayImpl;
import com.fasten.executor_driver.gateway.OrderCurrentCostApiMapper;
import com.fasten.executor_driver.gateway.OrderCurrentCostGatewayImpl;
import com.fasten.executor_driver.gateway.OrderFulfillmentApiMapper;
import com.fasten.executor_driver.gateway.OrderGatewayImpl;
import com.fasten.executor_driver.gateway.OrderRouteGatewayImpl;
import com.fasten.executor_driver.gateway.PasswordGatewayImpl;
import com.fasten.executor_driver.gateway.RoutePointApiMapper;
import com.fasten.executor_driver.gateway.SelectedVehicleAndOptionsGatewayImpl;
import com.fasten.executor_driver.gateway.ServerConnectionGatewayImpl;
import com.fasten.executor_driver.gateway.ServiceApiMapper;
import com.fasten.executor_driver.gateway.ServicesGatewayImpl;
import com.fasten.executor_driver.gateway.SmsCodeMapper;
import com.fasten.executor_driver.gateway.SmsGatewayImpl;
import com.fasten.executor_driver.gateway.TokenKeeperImpl;
import com.fasten.executor_driver.gateway.VehicleApiMapper;
import com.fasten.executor_driver.gateway.VehicleOptionApiMapper;
import com.fasten.executor_driver.gateway.VehicleOptionsGatewayImpl;
import com.fasten.executor_driver.gateway.VehiclesAndOptionsGatewayImpl;
import com.fasten.executor_driver.gateway.WaitingForClientApiMapper;
import com.fasten.executor_driver.gateway.WaitingForClientGatewayImpl;
import com.fasten.executor_driver.interactor.CallToClientUseCaseImpl;
import com.fasten.executor_driver.interactor.CancelOrderUseCase;
import com.fasten.executor_driver.interactor.CancelOrderUseCaseImpl;
import com.fasten.executor_driver.interactor.CurrentCostPollingUseCaseImpl;
import com.fasten.executor_driver.interactor.ExecutorBalanceUseCase;
import com.fasten.executor_driver.interactor.ExecutorBalanceUseCaseImpl;
import com.fasten.executor_driver.interactor.ExecutorStateNotOnlineUseCaseImpl;
import com.fasten.executor_driver.interactor.ExecutorStateUseCase;
import com.fasten.executor_driver.interactor.ExecutorStateUseCaseImpl;
import com.fasten.executor_driver.interactor.GeoLocationUseCase;
import com.fasten.executor_driver.interactor.GeoLocationUseCaseImpl;
import com.fasten.executor_driver.interactor.MemoryDataSharer;
import com.fasten.executor_driver.interactor.MissedOrderUseCaseImpl;
import com.fasten.executor_driver.interactor.MovingToClientUseCaseImpl;
import com.fasten.executor_driver.interactor.OrderConfirmationUseCaseImpl;
import com.fasten.executor_driver.interactor.OrderCurrentCostUseCaseImpl;
import com.fasten.executor_driver.interactor.OrderFulfillmentTimeUseCaseImpl;
import com.fasten.executor_driver.interactor.OrderRouteUseCaseImpl;
import com.fasten.executor_driver.interactor.OrderUseCaseImpl;
import com.fasten.executor_driver.interactor.ServerConnectionUseCaseImpl;
import com.fasten.executor_driver.interactor.WaitingForClientUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.LoginSharer;
import com.fasten.executor_driver.interactor.auth.LoginUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.PasswordUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.SmsUseCaseImpl;
import com.fasten.executor_driver.interactor.map.HeatMapUseCaseImpl;
import com.fasten.executor_driver.interactor.services.ServicesUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.LastUsedVehicleGateway;
import com.fasten.executor_driver.interactor.vehicle.SelectedVehicleUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceSharer;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehiclesAndOptionsGateway;
import com.fasten.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCaseImpl;
import com.fasten.executor_driver.presentation.ViewModelFactory;
import com.fasten.executor_driver.presentation.balance.BalanceViewModelImpl;
import com.fasten.executor_driver.presentation.calltoclient.CallToClientViewModelImpl;
import com.fasten.executor_driver.presentation.calltooperator.CallToOperatorViewModelImpl;
import com.fasten.executor_driver.presentation.cancelorder.CancelOrderViewModelImpl;
import com.fasten.executor_driver.presentation.cancelorderreasons.CancelOrderReasonsViewModelImpl;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModelImpl;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.codeheader.CodeHeaderViewModelImpl;
import com.fasten.executor_driver.presentation.corebalance.CoreBalanceViewModelImpl;
import com.fasten.executor_driver.presentation.currentcostpolling.CurrentCostPollingViewModelImpl;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewModelImpl;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModelImpl;
import com.fasten.executor_driver.presentation.map.MapViewModelImpl;
import com.fasten.executor_driver.presentation.missedorder.MissedOrderViewModelImpl;
import com.fasten.executor_driver.presentation.movingtoclient.MovingToClientViewModelImpl;
import com.fasten.executor_driver.presentation.nextroutepoint.NextRoutePointViewModelImpl;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl;
import com.fasten.executor_driver.presentation.onlineswitch.OnlineSwitchViewModelImpl;
import com.fasten.executor_driver.presentation.order.OrderViewModelImpl;
import com.fasten.executor_driver.presentation.orderconfirmation.OrderConfirmationViewModelImpl;
import com.fasten.executor_driver.presentation.ordercost.OrderCostViewModelImpl;
import com.fasten.executor_driver.presentation.orderroute.OrderRouteViewModelImpl;
import com.fasten.executor_driver.presentation.ordertime.OrderTimeViewModelImpl;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModelImpl;
import com.fasten.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.fasten.executor_driver.presentation.serverconnection.ServerConnectionViewModelImpl;
import com.fasten.executor_driver.presentation.services.ServicesListItems;
import com.fasten.executor_driver.presentation.services.ServicesSliderViewModelImpl;
import com.fasten.executor_driver.presentation.services.ServicesViewModelImpl;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import com.fasten.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModelImpl;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientViewModelImpl;
import com.fasten.executor_driver.utils.TimeUtilsImpl;
import com.fasten.executor_driver.view.BalanceFragment;
import com.fasten.executor_driver.view.BalanceSummaryFragment;
import com.fasten.executor_driver.view.CallToClientFragment;
import com.fasten.executor_driver.view.CallToOperatorFragment;
import com.fasten.executor_driver.view.CancelOrderDialogFragment;
import com.fasten.executor_driver.view.ChooseVehicleFragment;
import com.fasten.executor_driver.view.ClientOrderConfirmationFragment;
import com.fasten.executor_driver.view.DriverOrderConfirmationFragment;
import com.fasten.executor_driver.view.GoOnlineFragment;
import com.fasten.executor_driver.view.MapFragment;
import com.fasten.executor_driver.view.MenuFragment;
import com.fasten.executor_driver.view.MovingToClientFragment;
import com.fasten.executor_driver.view.OnlineFragment;
import com.fasten.executor_driver.view.OrderFulfillmentDetailsFragment;
import com.fasten.executor_driver.view.OrderFulfillmentFragment;
import com.fasten.executor_driver.view.OrderRouteFragment;
import com.fasten.executor_driver.view.SelectedVehicleFragment;
import com.fasten.executor_driver.view.SelectedVehicleOptionsFragment;
import com.fasten.executor_driver.view.ServerConnectionFragment;
import com.fasten.executor_driver.view.ServicesFragment;
import com.fasten.executor_driver.view.VehicleOptionsFragment;
import com.fasten.executor_driver.view.WaitingForClientFragment;
import com.fasten.executor_driver.view.WaitingForClientRouteFragment;
import com.fasten.executor_driver.view.auth.LoginFragment;
import com.fasten.executor_driver.view.auth.PasswordFragment;
import com.fasten.executor_driver.view.auth.SmsReceiver;
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
  private final AppSettingsService appSettingsService;
  @NonNull
  private final ApiService apiService;
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final ServerConnectionViewModel serverConnectionViewModel;
  @NonNull
  private final ExecutorStateUseCase executorStateUseCase;
  @NonNull
  private final CancelOrderUseCase cancelOrderUseCase;
  @NonNull
  private final ExecutorBalanceUseCase executorBalanceUseCase;
  @NonNull
  private final GeoLocationUseCase geoLocationUseCase;
  @NonNull
  private final MemoryDataSharer<String> loginSharer;
  @NonNull
  private final MemoryDataSharer<Vehicle> vehicleChoiceSharer;
  @NonNull
  private final LastUsedVehicleGateway lastUsedVehicleGateway;
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
        new AuthorizationInterceptor(),
        new SendTokenInterceptor(tokenKeeper),
        new ReceiveTokenInterceptor(tokenKeeper)
    );
    apiService = initApiService(okHttpClient);
    stompClient = initStompClient(okHttpClient);
    loginSharer = new LoginSharer(appSettingsService);
    vehicleChoiceSharer = new VehicleChoiceSharer();
    lastUsedVehicleGateway = new LastUsedVehicleGatewayImpl(appSettingsService);
    serverConnectionViewModel = new ServerConnectionViewModelImpl(
        new ServerConnectionUseCaseImpl(
            new ServerConnectionGatewayImpl(
                stompClient
            )
        )
    );
    cancelOrderUseCase = new CancelOrderUseCaseImpl(
        new CancelOrderGatewayImpl(
            stompClient,
            new CancelOrderReasonApiMapper()
        ),
        loginSharer
    );
    executorBalanceUseCase = new ExecutorBalanceUseCaseImpl(
        new ExecutorBalanceGatewayImpl(
            stompClient,
            new ExecutorBalanceApiMapper()
        ),
        loginSharer
    );
    executorStateUseCase = new ExecutorStateUseCaseImpl(
        new ExecutorStateGatewayImpl(stompClient, new ExecutorStateApiMapper()),
        loginSharer
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
  }

  private OkHttpClient initHttpClient(
      @NonNull Interceptor connectivityInterceptor,
      @NonNull Interceptor authorizationInterceptor,
      @NonNull Interceptor sendTokenInterceptor,
      @NonNull Interceptor receiveTokenInterceptor) {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .addInterceptor(connectivityInterceptor)
        .addInterceptor(authorizationInterceptor)
        .addInterceptor(receiveTokenInterceptor)
        .addInterceptor(sendTokenInterceptor);
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

  @Override
  public void inject(MainApplication mainApplication) {
    mainApplication.setServerConnectionViewModel(
        serverConnectionViewModel
    );
    mainApplication.setCancelOrderReasonsViewModel(
        new CancelOrderReasonsViewModelImpl(
            cancelOrderUseCase
        )
    );
    mainApplication.setCoreBalanceViewModel(
        new CoreBalanceViewModelImpl(
            executorBalanceUseCase
        )
    );
    mainApplication.setExecutorStateViewModel(
        new ExecutorStateViewModelImpl(
            executorStateUseCase
        )
    );
    mainApplication.setGeoLocationViewModel(
        new GeoLocationViewModelImpl(
            geoLocationUseCase
        )
    );
    mainApplication.setMissedOrderViewModel(
        new MissedOrderViewModelImpl(
            new MissedOrderUseCaseImpl(
                new MissedOrderGatewayImpl(
                    stompClient
                ),
                loginSharer
            )
        )
    );
    mainApplication.setCurrentCostPollingViewModel(
        new CurrentCostPollingViewModelImpl(
            new CurrentCostPollingUseCaseImpl(
                new CurrentCostPollingGatewayImpl(
                    stompClient,
                    new CurrentCostPollingTimersApiMapper()
                ),
                loginSharer
            )
        )
    );
    AutoRouterImpl autoRouter = new AutoRouterImpl();
    mainApplication.setAutoRouter(autoRouter);
    mainApplication.setExecutorStateViewActions(autoRouter);
    mainApplication.setLifeCycleCallbacks(autoRouter);
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
                    new VehiclesAndOptionsUseCaseImpl(
                        selectedVehiclesAndOptionsGateway,
                        vehicleChoiceSharer,
                        lastUsedVehicleGateway
                    ),
                    new ExecutorStateNotOnlineUseCaseImpl(
                        new ExecutorStateSwitchGatewayImpl(stompClient),
                        executorStateUseCase
                    )
                )
            )
        ).get(OnlineSwitchViewModelImpl.class)
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
                    new VehicleChoiceUseCaseImpl(vehiclesAndOptionsGateway, vehicleChoiceSharer)
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
                            executorStateUseCase,
                            ExecutorState.DRIVER_ORDER_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        new OrderConfirmationGatewayImpl(stompClient)
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.DRIVER_ORDER_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        )
                    ),
                    new TimeUtilsImpl()
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.CLIENT_ORDER_CONFIRMATION,
                            new OrderApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        )
                    ),
                    new TimeUtilsImpl()
                )
            )
        ).get(OrderViewModelImpl.class)
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.MOVING_TO_CLIENT,
                            new MovingToClientApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        )
                    ),
                    new TimeUtilsImpl()
                )
            )
        ).get(OrderViewModelImpl.class)
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.WAITING_FOR_CLIENT,
                            new WaitingForClientApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        )
                    ),
                    new TimeUtilsImpl()
                )
            )
        ).get(OrderViewModelImpl.class)
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderFulfillmentApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        new TimeUtilsImpl()
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderFulfillmentApiMapper(
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderFulfillmentApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.ORDER_FULFILLMENT,
                            new OrderFulfillmentApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
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
  public void inject(OrderFulfillmentDetailsFragment orderFulfillmentDetailsFragment) {
    orderFulfillmentDetailsFragment.setOrderViewModel(
        ViewModelProviders.of(
            orderFulfillmentDetailsFragment,
            new ViewModelFactory<>(
                new OrderViewModelImpl(
                    new OrderUseCaseImpl(
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.ORDER_FULFILLMENT,
                            new WaitingForClientApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        )
                    ),
                    new TimeUtilsImpl()
                )
            )
        ).get(OrderViewModelImpl.class)
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
  }

  @Override
  public void inject(ServerConnectionFragment serverConnectionFragment) {
    serverConnectionFragment.setServerConnectionViewModel(
        serverConnectionViewModel
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
                        new OrderGatewayImpl(
                            executorStateUseCase,
                            ExecutorState.WAITING_FOR_CLIENT,
                            new OrderFulfillmentApiMapper(
                                new VehicleOptionApiMapper(),
                                new RoutePointApiMapper()
                            )
                        ),
                        new OrderRouteGatewayImpl(stompClient)
                    )
                )
            )
        ).get(OrderRouteViewModelImpl.class)
    );
  }
}
