package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.backend.web.model.ApiLogin;

import io.reactivex.Completable;
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
  @GET("drivers/send_me_code")
  Completable sendMeCode(@NonNull @Query("pn") String phoneNumber);

  /*
   *  Запрос входящего звонка
   */
  @GET("drivers/call_me_code")
  Completable callMeCode(@NonNull @Query("pn") String phoneNumber);

  /*
   *  Проверка логина
   */
  @GET("drivers/checkLogin")
  Completable checkLogin(@NonNull @Query("pn") String phoneNumber);

  /*
   *  Авторизация
   */
  @POST("drivers/auth")
  Completable authorize(@NonNull @Body ApiLogin apiLogin);
}
