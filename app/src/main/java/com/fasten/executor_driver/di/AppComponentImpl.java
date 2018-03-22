package com.fasten.executor_driver.di;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.application.MainApplication;
import com.fasten.executor_driver.backend.geolocation.GeolocationCenter;
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
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.entity.LoginValidator;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.PasswordValidator;
import com.fasten.executor_driver.entity.PhoneNumberValidator;
import com.fasten.executor_driver.entity.SmsCodeExtractor;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.gateway.ErrorMapper;
import com.fasten.executor_driver.gateway.ExecutorStateApiGatewayImpl;
import com.fasten.executor_driver.gateway.GeoLocationGatewayImpl;
import com.fasten.executor_driver.gateway.GeoTrackingGatewayImpl;
import com.fasten.executor_driver.gateway.HeatMapGatewayImpl;
import com.fasten.executor_driver.gateway.PasswordGatewayImpl;
import com.fasten.executor_driver.gateway.ServiceApiMapper;
import com.fasten.executor_driver.gateway.ServicesGatewayImpl;
import com.fasten.executor_driver.gateway.SmsGatewayImpl;
import com.fasten.executor_driver.gateway.TokenKeeperImpl;
import com.fasten.executor_driver.gateway.VehicleApiMapper;
import com.fasten.executor_driver.gateway.VehicleOptionApiMapper;
import com.fasten.executor_driver.gateway.VehicleOptionsGatewayImpl;
import com.fasten.executor_driver.gateway.VehiclesAndOptionsGatewayImpl;
import com.fasten.executor_driver.interactor.ExecutorStateSharer;
import com.fasten.executor_driver.interactor.ExecutorStateUseCaseImpl;
import com.fasten.executor_driver.interactor.GeoLocationSharer;
import com.fasten.executor_driver.interactor.GeoLocationUseCaseImpl;
import com.fasten.executor_driver.interactor.GeoTrackingUseCase;
import com.fasten.executor_driver.interactor.GeoTrackingUseCaseImpl;
import com.fasten.executor_driver.interactor.MemoryDataSharer;
import com.fasten.executor_driver.interactor.UnAuthGateway;
import com.fasten.executor_driver.interactor.UnAuthUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.LoginSharer;
import com.fasten.executor_driver.interactor.auth.LoginUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.PasswordUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.SmsUseCaseImpl;
import com.fasten.executor_driver.interactor.map.HeatMapUseCaseImpl;
import com.fasten.executor_driver.interactor.services.ServicesUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.DriverOptionsSharer;
import com.fasten.executor_driver.interactor.vehicle.LastUsedVehicleSharer;
import com.fasten.executor_driver.interactor.vehicle.SelectedVehicleUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceSharer;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehiclesAndOptionsUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehiclesSharer;
import com.fasten.executor_driver.presentation.ViewModelFactory;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModelImpl;
import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.codeHeader.CodeHeaderViewModel;
import com.fasten.executor_driver.presentation.codeHeader.CodeHeaderViewModelImpl;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModelImpl;
import com.fasten.executor_driver.presentation.map.MapViewModel;
import com.fasten.executor_driver.presentation.map.MapViewModelImpl;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl;
import com.fasten.executor_driver.presentation.options.OptionsViewModel;
import com.fasten.executor_driver.presentation.persistence.PersistenceViewModelImpl;
import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModel;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModelImpl;
import com.fasten.executor_driver.presentation.services.ServicesOptionsViewModelImpl;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import com.fasten.executor_driver.presentation.splahscreen.SplashScreenViewModelImpl;
import com.fasten.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModelImpl;
import com.fasten.executor_driver.view.ChooseVehicleFragment;
import com.fasten.executor_driver.view.GoOnlineFragment;
import com.fasten.executor_driver.view.MapFragment;
import com.fasten.executor_driver.view.SelectedVehicleFragment;
import com.fasten.executor_driver.view.ServicesFragment;
import com.fasten.executor_driver.view.VehicleOptionsFragment;
import com.fasten.executor_driver.view.auth.LoginFragment;
import com.fasten.executor_driver.view.auth.PasswordFragment;
import com.fasten.executor_driver.view.auth.SmsReceiver;
import java.util.List;
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
  @SuppressWarnings("FieldCanBeLocal")
  @NonNull
  private final StompClient stompClient;
  @NonNull
  private final GeolocationCenter geolocationCenter;
  @NonNull
  private final MemoryDataSharer<String> loginSharer;
  @NonNull
  private final MemoryDataSharer<List<Vehicle>> vehiclesSharer;
  @NonNull
  private final MemoryDataSharer<List<Option>> driverOptionsSharer;
  @NonNull
  private final MemoryDataSharer<Vehicle> vehicleChoiceSharer;
  @NonNull
  private final MemoryDataSharer<Vehicle> lastUsedVehiclesSharer;
  @NonNull
  private final MemoryDataSharer<ExecutorState> executorStateSharer;
  @NonNull
  private final MemoryDataSharer<GeoLocation> geoLocationSharer;
  @NonNull
  private final UnAuthGateway unAuthGateway;
  /**
   * Это будет висеть в памяти постоянно. Запускается при инжекте в Application.
   */
  @NonNull
  private final GeoTrackingUseCase geoTrackingUseCase;

  public AppComponentImpl(@NonNull Context appContext) {
    appContext = appContext.getApplicationContext();
    appSettingsService = new AppPreferences(appContext);
    TokenKeeper tokenKeeper = new TokenKeeperImpl(appSettingsService);
    AuthorizationInterceptor authorizationInterceptor = new AuthorizationInterceptor();
    unAuthGateway = authorizationInterceptor;
    OkHttpClient okHttpClient = initHttpClient(
        new ConnectivityInterceptor(appContext),
        authorizationInterceptor,
        new SendTokenInterceptor(tokenKeeper),
        new ReceiveTokenInterceptor(tokenKeeper)
    );
    apiService = initApiService(okHttpClient);
    stompClient = initStompClient(okHttpClient);
    geolocationCenter = new GeolocationCenterImpl(appContext);
    loginSharer = new LoginSharer(appSettingsService);
    vehiclesSharer = new VehiclesSharer();
    driverOptionsSharer = new DriverOptionsSharer();
    vehicleChoiceSharer = new VehicleChoiceSharer();
    lastUsedVehiclesSharer = new LastUsedVehicleSharer(appSettingsService);
    executorStateSharer = new ExecutorStateSharer();
    geoLocationSharer = new GeoLocationSharer();
    geoTrackingUseCase = new GeoTrackingUseCaseImpl(
        new GeoTrackingGatewayImpl(
            stompClient
        ),
        geoLocationSharer
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
    mainApplication.setUnAuthUseCase(new UnAuthUseCaseImpl(unAuthGateway));
    mainApplication.setPersistenceViewModel(new PersistenceViewModelImpl(executorStateSharer));
    mainApplication.setSplashScreenViewModel(
        new SplashScreenViewModelImpl(
            new ExecutorStateUseCaseImpl(
                new ExecutorStateApiGatewayImpl(
                    apiService
                ),
                executorStateSharer
            )
        )
    );
    mainApplication.setGeoLocationDataReceiver(geoLocationSharer);
    mainApplication.setExecutorStateDataReceiver(executorStateSharer);
    mainApplication.setGeoLocationUseCase(new GeoLocationUseCaseImpl(
        new GeoLocationGatewayImpl(geolocationCenter), executorStateSharer, geoLocationSharer
    ));
    geoTrackingUseCase.reload().subscribe(() -> {
    }, throwable -> {
    });
  }

  @Override
  public void inject(LoginFragment loginFragment) {
    loginFragment.setAppSettings(appSettingsService);
    loginFragment.setPhoneViewModel(
        ViewModelProviders.of(
            loginFragment,
            new ViewModelFactory<PhoneViewModel>(
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
            new ViewModelFactory<SmsButtonViewModel>(
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
            new ViewModelFactory<CodeHeaderViewModel>(
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
            new ViewModelFactory<CodeViewModel>(
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
            new SmsCodeExtractor()
        )
    );
  }

  @Override
  public void inject(MapFragment mapFragment) {
    mapFragment.setMapViewModel(
        ViewModelProviders.of(
            mapFragment,
            new ViewModelFactory<MapViewModel>(
                new MapViewModelImpl(
                    new HeatMapUseCaseImpl(
                        new HeatMapGatewayImpl(apiService)
                    )
                )
            )).get(MapViewModelImpl.class)
    );
    mapFragment.setGeoLocationViewModel(
        new GeoLocationViewModelImpl(
            geoLocationSharer
        )
    );
  }

  @Override
  public void inject(GoOnlineFragment goOnlineFragment) {
    goOnlineFragment.setOnlineButtonViewModel(
        ViewModelProviders.of(
            goOnlineFragment,
            new ViewModelFactory<OnlineButtonViewModel>(
                new OnlineButtonViewModelImpl(
                    new VehiclesAndOptionsUseCaseImpl(
                        new VehiclesAndOptionsGatewayImpl(
                            apiService,
                            new VehicleOptionApiMapper(),
                            new VehicleApiMapper(
                                new VehicleOptionApiMapper()
                            ),
                            new ErrorMapper()
                        ),
                        vehiclesSharer,
                        driverOptionsSharer,
                        vehicleChoiceSharer,
                        lastUsedVehiclesSharer
                    )
                )
            )
        ).get(OnlineButtonViewModelImpl.class)
    );
  }

  @Override
  public void inject(ChooseVehicleFragment chooseVehicleFragment) {
    chooseVehicleFragment.setChooseVehicleViewModel(
        ViewModelProviders.of(
            chooseVehicleFragment,
            new ViewModelFactory<ChooseVehicleViewModel>(
                new ChooseVehicleViewModelImpl(
                    new VehicleChoiceUseCaseImpl(vehiclesSharer, vehicleChoiceSharer)
                )
            )
        ).get(ChooseVehicleViewModelImpl.class)
    );
  }

  @Override
  public void inject(VehicleOptionsFragment vehicleOptionsFragment) {
    vehicleOptionsFragment.setVehicleOptionsViewModel(
        ViewModelProviders.of(
            vehicleOptionsFragment,
            new ViewModelFactory<OptionsViewModel>(
                new VehicleOptionsViewModelImpl(
                    new VehicleOptionsUseCaseImpl(
                        new VehicleOptionsGatewayImpl(
                            apiService
                        ),
                        vehicleChoiceSharer,
                        lastUsedVehiclesSharer,
                        driverOptionsSharer
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
            new ViewModelFactory<SelectedVehicleViewModel>(
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
    servicesFragment.setServicesViewModel(
        ViewModelProviders.of(
            servicesFragment,
            new ViewModelFactory<>(
                new ServicesOptionsViewModelImpl(
                    new ServicesUseCaseImpl(
                        new ServicesGatewayImpl(
                            apiService,
                            new ServiceApiMapper()
                        )
                    )
                )
            )
        ).get(ServicesOptionsViewModelImpl.class)

    );
  }
}
