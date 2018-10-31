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
  private final Context appContext;
  @Nullable
  private EventLogger eventLogger;
  @Nullable
  private ErrorReporter errorReporter;
  @Nullable
  private Interceptor[] interceptors;
  @Nullable
  private AppSettingsService appSettingsService;
  @Nullable
  private GeolocationCenter geolocationCenter;
  @Nullable
  private ApiService apiService;
  @Nullable
  private StompClient stompClient;
  @Nullable
  private PersonalQueueListener personalQueueListener;
  @Nullable
  private OkHttpClient okHttpClient;

  BackendComponentImpl(@NonNull Context appContext) {
    this.appContext = appContext;
  }

  @Override
  @NonNull
  public EventLogger getEventLogger() {
    if (eventLogger == null) {
      eventLogger = new EventLoggerImpl(getAppSettingsService(), appContext);
    }
    return eventLogger;
  }

  @Override
  @NonNull
  public ErrorReporter getErrorReporter() {
    if (errorReporter == null) {
      errorReporter = new ErrorReporterImpl(getAppSettingsService());
    }
    return errorReporter;
  }

  @Override
  @NonNull
  public ApiService getApiService() {
    if (apiService == null) {
      // build OkHttpClient builder
      apiService = new Retrofit.Builder()
          .baseUrl(BuildConfig.BASE_URL)
          .client(getOkHttpClient(getInterceptors()))
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
          getOkHttpClient(getInterceptors())
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
    if (appSettingsService == null) {
      appSettingsService = new AppPreferences(appContext);
    }
    return appSettingsService;
  }

  @Override
  @NonNull
  public GeolocationCenter getGeolocationCenter() {
    if (geolocationCenter == null) {
      this.geolocationCenter = new GeolocationCenterImpl(appContext);
    }
    return geolocationCenter;
  }

  @NonNull
  private Interceptor[] getInterceptors() {
    if (interceptors == null) {
      TokenKeeper tokenKeeper = new TokenKeeperImpl(getAppSettingsService());
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
    return interceptors;
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
