package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.backend.web.outgoing.ApiLogin;
import com.fasten.executor_driver.backend.web.outgoing.ApiVehicleOptionItem;
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
  Single<List<ApiVehicle>> getCars();

  /*
   *  Запрос занятия выборанной ТС с перечисленными опциями.
   */
  @PUT("api/public/v1/car/{carId}/vehicleOptionItem")
  Completable selectCarWithOptions(
      @Path("carId") long carId,
      @NonNull @Body List<ApiVehicleOptionItem> vehicleOptionItems
  );
}
