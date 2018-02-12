package com.fasten.executor_driver.di;

import android.content.Context;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.backend.settings.AppPreferences;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.backend.web.ApiService;
import com.fasten.executor_driver.backend.web.AuthorizationInterceptor;
import com.fasten.executor_driver.backend.web.ConnectivityInterceptor;
import com.fasten.executor_driver.backend.web.ReceiveTokenInterceptor;
import com.fasten.executor_driver.backend.web.SendTokenInterceptor;
import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.Subject;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class AppModule {

  @NonNull
  private final Context appContext;
  @NonNull
  private final Subject<String> logoutEventSubject;

  public AppModule(@NonNull Context context, @NonNull Subject<String> logoutEventSubject) {
    this.appContext = context.getApplicationContext();
    this.logoutEventSubject = logoutEventSubject;
  }

  @Provides
  @NonNull
  @Singleton
  Context provideAppContext() {
    return appContext;
  }

  @Provides
  @NonNull
  @Singleton
  Subject<String> provideLogoutSubject() {
    return logoutEventSubject;
  }

  @Provides
  @NonNull
  @Singleton
  AppSettingsService provideAppSettingsService(@NonNull AppPreferences appPreferences) {
    return appPreferences;
  }

  @Provides
  @NonNull
  @Singleton
  ApiService provideApiService(@NonNull ConnectivityInterceptor connectivityInterceptor,
      @NonNull AuthorizationInterceptor authorizationInterceptor,
      @NonNull SendTokenInterceptor sendTokenInterceptor,
      @NonNull ReceiveTokenInterceptor receiveTokenInterceptor) {
    // build OkHttpClient builder
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
    return new Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(builder.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService.class);
  }
}
