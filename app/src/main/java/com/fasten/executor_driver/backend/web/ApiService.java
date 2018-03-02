package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.incoming.ApiOptionsForOnline;
import com.fasten.executor_driver.backend.web.incoming.ApiServiceItem;
import com.fasten.executor_driver.backend.web.outgoing.ApiLogin;
import com.fasten.executor_driver.backend.web.outgoing.ApiOptionItems;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Общение с сервером API.
 */
public interface ApiService {

  /*
   *  Запрос СМС с кодом.
   */
  @GET("api/public/v1/login/password/sms")
  Completable sendMeCode(@NonNull @Query("login") String phoneNumber);

  /*
   *  Авторизация.
   */
  @POST("api/public/v1/login")
  Completable authorize(@NonNull @Body ApiLogin apiLogin);

  /*
   *  Запрос тепловой карты.
   */
  @GET("geoMap")
  Single<String> getHeatMap();

  /*
   *  Запрос ТС текущего исполнителя.
   */
  @GET("api/public/v1/car")
  Single<ApiOptionsForOnline> getOptionsForOnline();

  /*
   *  Запрос занятия выборанной ТС с перечисленными опциями ТС и исполнителя.
   */
  @PUT("api/public/v1/car/{carId}/vehicleOptionItem")
  Completable occupyCarWithOptions(
      @Path("carId") long carId,
      @NonNull @Body ApiOptionItems apiOptionItems
  );

  /*
   *  Запрос услуг текущего исполнителя.
   */
  @GET("api/public/v1/car/mobile")
  Single<List<ApiServiceItem>> getMyServices();

  /*
   *  Запрос закрепления выбора услуг исполнителя.
   */
  @PUT("api/public/v1/car/mobile")
  Completable setMyServices(
      @NonNull @Query("ids") String servicesIds
  );
}
