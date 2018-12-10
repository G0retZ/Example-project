package com.cargopull.executor_driver.backend.web;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionsForOnline;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersSummary;
import com.cargopull.executor_driver.backend.web.incoming.ApiServiceItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult;
import com.cargopull.executor_driver.backend.web.outgoing.ApiLogin;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOptionItems;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;
import java.util.Map;
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
  @GET("api/public/v1/geoMap")
  Single<String> getHeatMap();

  /*
   *  Запрос ТС текущего исполнителя.
   */
  @GET("api/public/v1/mobile/car")
  Single<ApiOptionsForOnline> getOptionsForOnline();

  /*
   *  Запрос занятия выборанной ТС с перечисленными опциями ТС и исполнителя.
   */
  @PUT("api/public/v1/mobile/car/{carId}/vehicleOptionItem")
  Completable occupyCarWithOptions(
      @Path("carId") long carId,
      @NonNull @Body ApiOptionItems apiOptionItems
  );

  /*
   *  Запрос услуг текущего исполнителя.
   */
  @GET("api/public/v1/mobile/car/carSearchRequest")
  Single<List<ApiServiceItem>> getMyServices();

  /*
   *  Запрос выбранных услуг текущего исполнителя.
   */
  @GET("api/public/v1/mobile/car/carSearchRequest/assigned")
  Single<String> getMySelectedServices();

  /*
   *  Запрос закрепления выбора услуг исполнителя.
   */
  @PUT("api/public/v1/mobile/car/carSearchRequest")
  Completable setMyServices(
      @NonNull @Query("ids") String servicesIds
  );

  /*
   *  Запрос опций выбранного ТС и текущего исполнителя.
   */
  @GET("api/public/v1/mobile/car/active")
  Single<ApiOptionsForOnline> getSelectedOptionsForOnline();

  /*
   *  Запрос установки опций выбранного ТС и текущего исполнителя.
   */
  @PUT("api/public/v1/mobile/car/vehicleOptionItem")
  Completable setSelectedVehicleOptions(
      @NonNull @Body ApiOptionItems apiOptionItems
  );

  /*
   *  Запрос бронирования или отказа от предварительного заказа.
   */
  @POST("api/public/v1/mobile/order/assign")
  Single<ApiSimpleResult> sendPreOrderDecision(
      @NonNull @Body ApiOrderDecision decision
  );

  /*
   *  Запрос отправки FCM токена.
   */
  @PUT("api/public/v1/mobile/fireBase/registrationToken")
  Completable sendFcmInstanceID(
      @Query("registrationToken") String carId
  );

  /*
   *  Запрос истории заказов.
   */
  @GET("api/public/v1/orderHistoryDetalization/forPeriod")
  Single<Map<String, ApiOrdersSummary>> getOrdersHistory(
      @Query("dateFrom") long fromDate,
      @Query("dateTo") long toDate
  );

  /*
   *  Запрос принятия или отказа от срочного заказа.
   */
  @POST("api/public/v1/mobile/order/accept")
  Completable acceptOrderOffer(
      @NonNull @Body ApiOrderDecision decision
  );
}
