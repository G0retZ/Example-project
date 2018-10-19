package com.cargopull.executor_driver.di;

import android.content.Context;
import android.net.ConnectivityManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.ErrorReporterImpl;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.backend.analytics.EventLoggerImpl;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenter;
import com.cargopull.executor_driver.backend.geolocation.GeolocationCenterImpl;
import com.cargopull.executor_driver.backend.settings.AppPreferences;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.web.ApiService;
import com.cargopull.executor_driver.backend.web.AuthorizationInterceptor;
import com.cargopull.executor_driver.backend.web.ConnectivityInterceptor;
import com.cargopull.executor_driver.backend.web.DeprecatedVersionInterceptor;
import com.cargopull.executor_driver.backend.web.ReceiveTokenInterceptor;
import com.cargopull.executor_driver.backend.web.SendTokenInterceptor;
import com.cargopull.executor_driver.backend.web.SendVersionInterceptor;
import com.cargopull.executor_driver.backend.web.ServerResponseInterceptor;
import com.cargopull.executor_driver.backend.web.TokenKeeper;
import com.cargopull.executor_driver.backend.websocket.PersonalQueueListener;
import com.cargopull.executor_driver.backend.websocket.TopicListener;
import com.cargopull.executor_driver.gateway.TokenKeeperImpl;
import com.cargopull.executor_driver.interactor.DataReceiver;
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

class BackendComponentImpl implements BackendComponent {

  @NonNull
  private final EventLogger eventLogger;
  @NonNull
  private final ErrorReporter errorReporter;
  @NonNull
  private final Interceptor[] interceptors;
  @NonNull
  private final AppSettingsService appSettingsService;
  @NonNull
  private final GeolocationCenter geolocationCenter;
  @Nullable
  private ApiService apiService;
  @Nullable
  private StompClient stompClient;
  @Nullable
  private PersonalQueueListener personalQueueListener;
  @Nullable
  private OkHttpClient okHttpClient;

  BackendComponentImpl(@NonNull Context appContext) {
    appSettingsService = new AppPreferences(appContext);
    eventLogger = new EventLoggerImpl(appSettingsService, appContext);
    errorReporter = new ErrorReporterImpl(appSettingsService);
    this.geolocationCenter = new GeolocationCenterImpl(appContext);
    TokenKeeper tokenKeeper = new TokenKeeperImpl(appSettingsService);
    this.interceptors = new Interceptor[]{
        new ConnectivityInterceptor(
            (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
        ),
        new SendVersionInterceptor(),
        new DeprecatedVersionInterceptor(),
        new AuthorizationInterceptor(),
        new ServerResponseInterceptor(),
        new SendTokenInterceptor(tokenKeeper),
        new ReceiveTokenInterceptor(tokenKeeper)
    };
  }

  @Override
  @NonNull
  public EventLogger getEventLogger() {
    return eventLogger;
  }

  @Override
  @NonNull
  public ErrorReporter getErrorReporter() {
    return errorReporter;
  }

  @Override
  @NonNull
  public ApiService getApiService() {
    if (apiService == null) {
      // build OkHttpClient builder
      apiService = new Retrofit.Builder()
          .baseUrl(BuildConfig.BASE_URL)
          .client(getOkHttpClient(interceptors))
          .addConverterFactory(ScalarsConverterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build()
          .create(ApiService.class);
    }
    return apiService;
  }

  @Override
  @NonNull
  public StompClient getStompClient() {
    if (stompClient == null) {
      stompClient = Stomp.over(
          Stomp.ConnectionProvider.OKHTTP,
          BuildConfig.SOCKET_URL,
          null,
          getOkHttpClient(interceptors)
      );
    }
    return stompClient;
  }

  @Override
  @NonNull
  public TopicListener getPersonalTopicListener(@NonNull DataReceiver<String> loginReceiver) {
    if (personalQueueListener == null) {
      personalQueueListener = new PersonalQueueListener(
          getStompClient(),
          loginReceiver
      );
    }
    return personalQueueListener;
  }

  @Override
  @NonNull
  public AppSettingsService getAppSettingsService() {
    return appSettingsService;
  }

  @Override
  @NonNull
  public GeolocationCenter getGeolocationCenter() {
    return geolocationCenter;
  }

  @NonNull
  private OkHttpClient getOkHttpClient(@NonNull Interceptor... interceptors) {
    if (okHttpClient == null) {
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
      okHttpClient = builder.build();
    }
    return okHttpClient;
  }
}
