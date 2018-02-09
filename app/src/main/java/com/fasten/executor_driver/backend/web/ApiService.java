package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.model.ApiLogin;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Общение с сервером API
 */
public interface ApiService {

  /*
   *  Запрос СМС с кодом
   */
  @GET("api/public/v1/login/password/sms")
  Completable sendMeCode(@NonNull @Query("login") String phoneNumber);

  /*
   *  Авторизация
   */
  @POST("api/public/v1/login")
  Completable authorize(@NonNull @Body ApiLogin apiLogin);

  /*
   *  Запрос тепловой карты
   */
  @GET("geoMap")
  Single<String> getHeatMap();
}
