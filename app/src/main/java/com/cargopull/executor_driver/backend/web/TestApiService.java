package com.cargopull.executor_driver.backend.web;

import com.cargopull.executor_driver.backend.ApiOrderTestFactory;
import com.cargopull.executor_driver.backend.stomp.Command;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionsForOnline;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrder;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrderCostDetails;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrderOptionsCostDetails;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrderOverPackage;
import com.cargopull.executor_driver.backend.web.incoming.ApiOrdersSummary;
import com.cargopull.executor_driver.backend.web.incoming.ApiProblem;
import com.cargopull.executor_driver.backend.web.incoming.ApiRoutePoint;
import com.cargopull.executor_driver.backend.web.incoming.ApiServiceItem;
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult;
import com.cargopull.executor_driver.backend.web.incoming.ApiVehicle;
import com.cargopull.executor_driver.backend.web.outgoing.ApiLogin;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOptionItems;
import com.cargopull.executor_driver.backend.web.outgoing.ApiOrderDecision;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.entity.PaymentType;
import com.cargopull.executor_driver.entity.RouteType;
import com.cargopull.executor_driver.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.Single;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class TestApiService implements ApiService {

  private final TimeUtils timeUtils;
  private final Observer<StompFrame> observer;

  public TestApiService(TimeUtils timeUtils,
      Observer<StompFrame> observer) {
    this.timeUtils = timeUtils;
    this.observer = observer;
  }

  @NotNull
  @Override
  public Single<String> getHeatMap() {
    return Single.just("{\n" +
        "  \"type\": \"FeatureCollection\",\n" +
        "  \"features\": [\n" +
        "    {\n" +
        "      \"type\": \"Feature\",\n" +
        "      \"properties\": {},\n" +
        "      \"geometry\": {\n" +
        "        \"type\": \"Polygon\",\n" +
        "        \"coordinates\": [\n" +
        "          [\n" +
        "            [\n" +
        "              -9.216413497924805,\n" +
        "              38.708687468078985\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.214954376220703,\n" +
        "              38.71237109395681\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.211692810058594,\n" +
        "              38.71290687827259\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.207830429077148,\n" +
        "              38.7134426585726\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.206199645996094,\n" +
        "              38.71498300456587\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.202337265014648,\n" +
        "              38.71223714725042\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.200019836425781,\n" +
        "              38.71384449116226\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.194097518920898,\n" +
        "              38.71411237830043\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.19255256652832,\n" +
        "              38.71150043587885\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.188947677612305,\n" +
        "              38.70661115894562\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.179677963256834,\n" +
        "              38.7062092856972\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.177274703979492,\n" +
        "              38.71551876930462\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.176931381225586,\n" +
        "              38.71960384338371\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.175214767456055,\n" +
        "              38.72415742123496\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.172897338867186,\n" +
        "              38.72737153678216\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.172639846801758,\n" +
        "              38.73232634831353\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.172639846801758,\n" +
        "              38.73714691617483\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.178133010864258,\n" +
        "              38.74056128823605\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.185600280761719,\n" +
        "              38.74397549705874\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.193840026855469,\n" +
        "              38.74337300148123\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.198217391967772,\n" +
        "              38.74250272111668\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.20156478881836,\n" +
        "              38.74036044733935\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.20431137084961,\n" +
        "              38.73915539009763\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.209203720092773,\n" +
        "              38.73158984401968\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.209203720092773,\n" +
        "              38.72864375091052\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.208087921142578,\n" +
        "              38.72489400215132\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.210491180419922,\n" +
        "              38.721679775156424\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.21675682067871,\n" +
        "              38.719135076250566\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.221563339233397,\n" +
        "              38.714380264434624\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.222078323364258,\n" +
        "              38.71076371691501\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.221649169921875,\n" +
        "              38.70721396458306\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.225082397460938,\n" +
        "              38.70373101742003\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.22645568847656,\n" +
        "              38.70118567951188\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.225168228149414,\n" +
        "              38.700582823053374\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.22079086303711,\n" +
        "              38.701587580994214\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.218387603759766,\n" +
        "              38.70359705452563\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.21701431274414,\n" +
        "              38.7062092856972\n" +
        "            ],\n" +
        "            [\n" +
        "              -9.216413497924805,\n" +
        "              38.708687468078985\n" +
        "            ]\n" +
        "          ]\n" +
        "        ]\n" +
        "      }\n" +
        "    }\n" +
        "  ]\n" +
        "}");
  }

  @NotNull
  @Override
  public Single<ApiOptionsForOnline> getOptionsForOnline() {
    return Single.just(new ApiOptionsForOnline(
        Collections.singletonList(
            new ApiVehicle(
                0,
                "Toyota",
                "Wish",
                "XX-VV-XX",
                "blue",
                false,
                Collections.singletonList(
                    new ApiOptionItem(
                        0,
                        "opt 1",
                        "desc 1",
                        false,
                        true,
                        "true",
                        null,
                        null
                    )
                )
            )
        ),
        Collections.singletonList(
            new ApiOptionItem(
                0,
                "opt 1",
                "desc 1",
                false,
                true,
                "true",
                null,
                null
            )
        )
    ));
  }

  @NotNull
  @Override
  public Single<List<ApiServiceItem>> getMyServices() {
    return Single.just(
        Collections.singletonList(
            new ApiServiceItem(
                4,
                "Transportation",
                35
            )
        )
    );
  }

  @NotNull
  @Override
  public Single<String> getMySelectedServices() {
    return Single.just("Transportation");
  }

  @NotNull
  @Override
  public Single<ApiOptionsForOnline> getSelectedOptionsForOnline() {
    return Single.just(new ApiOptionsForOnline(
        Collections.singletonList(
            new ApiVehicle(
                0,
                "Toyota",
                "Wish",
                "XX-VV-XX",
                "blue",
                false,
                Collections.singletonList(
                    new ApiOptionItem(
                        0,
                        "opt 1",
                        "desc 1",
                        false,
                        true,
                        "true",
                        null,
                        null
                    )
                )
            )
        ),
        Collections.singletonList(
            new ApiOptionItem(
                0,
                "opt 1",
                "desc 1",
                false,
                true,
                "true",
                null,
                null
            )
        )
    ));
  }

  @NotNull
  @Override
  public Single<List<ApiProblem>> getReportProblems() {
    return Single.just(Arrays.asList(
        new ApiProblem(0, "Car is broken", "Broken"),
        new ApiProblem(1, "Out of gas", "No gas")
    ));
  }

  @NotNull
  @Override
  public Completable sendMeCode(@NotNull String phoneNumber) {
    return Completable.complete();
  }

  @NotNull
  @Override
  public Completable authorize(@NotNull ApiLogin apiLogin) {
    return Completable.complete();
  }

  @NotNull
  @Override
  public Completable occupyCarWithOptions(long carId, @NotNull ApiOptionItems apiOptionItems) {
    return Completable.complete();
  }

  @NotNull
  @Override
  public Completable setMyServices(@NotNull String servicesIds) {
    return Completable.fromAction(() -> observer.onNext(
        new StompFrame(
            Command.MESSAGE,
            Collections.singletonMap("Status", "ONLINE"),
            ""
        )
    ));
  }

  @NotNull
  @Override
  public Completable setSelectedVehicleOptions(@NotNull ApiOptionItems apiOptionItems) {
    return Completable.complete();
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<Void>> acceptOrderOffer(@NotNull ApiOrderDecision decision) {
    return Single.fromCallable(() -> {
      Type response = new TypeToken<ApiSimpleResult<Void>>() {
      }.getType();
      String state = decision.isApproved() ? "MOVING_TO_CLIENT" : "ONLINE";
      return new Gson()
          .fromJson("{\"message\":\"ROGER THAT\",\"status\":\"" + state + "\"}",
              response);
    });
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<String>> sendPreOrderDecision(
      @NotNull ApiOrderDecision decision) {
    return Single.fromCallable(() -> {
      Type response = new TypeToken<ApiSimpleResult<String>>() {
      }.getType();
      return new Gson().fromJson("{\"code\":\"200\",\"message\":\"ROGER THIS\"}", response);
    });
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<ApiOrder>> sendPreOrderProcess(
      @NotNull ApiOrderDecision decision) {
    return Single.fromCallable(() -> {
      Type response = new TypeToken<ApiSimpleResult<ApiOrder>>() {
      }.getType();
      String state = decision.isApproved() ? "MOVING_TO_CLIENT" : "ONLINE";
      return new Gson().fromJson("{\"code\":\"200\",\"status\":\"" + state + "\","
          + "\"data\":" + ApiOrderTestFactory
          .getOrder(decision.getId(),
              timeUtils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30),
              timeUtils.currentTimeMillis() + TimeUnit.HOURS.toMillis(5),
              PaymentType.CASH, RouteType.ORDER_ZONE) + "}", response);
    });
  }

  @NotNull
  @Override
  public Completable sendFcmInstanceID(@NotNull String carId) {
    return Completable.complete();
  }

  @NotNull
  @Override
  public Single<Map<String, ApiOrdersSummary>> getOrdersHistory(long fromDate, long toDate) {
    return Single.just(
        new HashMap<String, ApiOrdersSummary>() {{
          put("successOrders", new ApiOrdersSummary(45, 1846));
          put("refusedOrders", new ApiOrdersSummary(5, 157));
          put("cancelledOrders", new ApiOrdersSummary(2, 48));
          put("skippedOrders", new ApiOrdersSummary(8, 286));
        }}
    );
  }

  @NotNull
  @Override
  public Completable callToClient(@NotNull Map<String, String> params) {
    return Completable.complete();
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<List<ApiRoutePoint>>> reportArrived(
      @NotNull Map<String, String> params) {
    return Single.fromCallable(() -> {
      Type response = new TypeToken<ApiSimpleResult<List<ApiRoutePoint>>>() {
      }.getType();
      return new Gson().fromJson("{\"code\":\"200\",\"message\":\"ROGER THIS\","
          + "\"status\":\"WAITING_FOR_CLIENT\",\"data\":" + ApiOrderTestFactory.getRoute()
          + "}", response);
    });
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<ApiOrder>> startOrder(@NotNull Map<String, String> params) {
    return Single.fromCallable(() -> {
      Type response = new TypeToken<ApiSimpleResult<ApiOrder>>() {
      }.getType();
      ExecutorState state = ExecutorState.ORDER_FULFILLMENT;
      ApiSimpleResult<ApiOrder> result = new Gson()
          .fromJson("{\"code\":\"200\",\"status\":\"" + state + "\","
              + "\"data\":" + ApiOrderTestFactory
              .getOrder(0, timeUtils.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30),
                  timeUtils.currentTimeMillis() + TimeUnit.HOURS.toMillis(5),
                  PaymentType.CASH, RouteType.ORDER_ZONE) + "}", response);
      result.getData().getRoute().clear();
      result.getData().getRoute().addAll(
          Arrays.asList(
              new ApiRoutePoint(7, 38.70118567951188, -9.207830429077148, "Client",
                  "Av. do Restelo 4, Lisboa", "COMPLETED"),
              new ApiRoutePoint(8, 38.750235247633604, -9.256976132829008, "Cargo",
                  "Largo Palácio 11, 2745-011 Queluz", "IN_PROGRESS"),
              new ApiRoutePoint(9, 38.69251655457379, -9.314235207074216, "Destination",
                  "Largo Marquês Pombal 21, 2780-289 Oeiras", "WAITING")
          )
      );
      return result;
    });
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<ApiOrderCostDetails>> completeOrder(
      @NotNull Map<String, String> params) {
    return Single.just(new ApiSimpleResult<>(
        "200", null, "PAYMENT_CONFIRMATION",
        new ApiOrderCostDetails(47, 42, 3678000, 11500,
            new ApiOrderOptionsCostDetails(
                Collections.emptyList()
            ),
            new ApiOrderOverPackage(25000, 1200000, 500, "moverName", 500, 25, "moverTariffName",
                25)
        )
    ));
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<Void>> completeOrderPayment(@NotNull Map<String, String> params) {
    return Single.just(new ApiSimpleResult<>(
        "200", null, "ONLINE",
        null
    ));
  }

  String switchToPoint(long myIndex, long nextIndex) {
    if (myIndex == nextIndex) {
      return "COMPLETED";
    }
    if (myIndex < nextIndex) {
      return "IN_PROGRESS";
    }
    return "WAITING";
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<List<ApiRoutePoint>>> makeRoutePointNext(long routePointId) {
    return Single.just(new ApiSimpleResult<>(
        "200", null, "ORDER_FULFILLMENT",
        Arrays.asList(
            new ApiRoutePoint(7, 38.70118567951188, -9.207830429077148, "Client",
                "Av. do Restelo 4, Lisboa", switchToPoint(7, routePointId)),
            new ApiRoutePoint(8, 38.750235247633604, -9.256976132829008, "Cargo",
                "Largo Palácio 11, 2745-011 Queluz", switchToPoint(8, routePointId)),
            new ApiRoutePoint(9, 38.69251655457379, -9.314235207074216, "Destination",
                "Largo Marquês Pombal 21, 2780-289 Oeiras", switchToPoint(9, routePointId))
        )
    ));
  }

  String closePoint(long myIndex, long nextIndex) {
    if (myIndex == nextIndex + 1) {
      return "IN_PROGRESS";
    }
    if (myIndex <= nextIndex) {
      return "COMPLETED";
    }
    return "WAITING";
  }

  @NotNull
  @Override
  public Single<ApiSimpleResult<List<ApiRoutePoint>>> completeRoutePoint(long routePointId) {
    return Single.just(new ApiSimpleResult<>(
        "200", null, routePointId == 9 ? "PAYMENT_CONFIRMATION" : "ORDER_FULFILLMENT",
        Arrays.asList(
            new ApiRoutePoint(7, 38.70118567951188, -9.207830429077148, "Client",
                "Av. do Restelo 4, Lisboa", closePoint(7, routePointId)),
            new ApiRoutePoint(8, 38.750235247633604, -9.256976132829008, "Cargo",
                "Largo Palácio 11, 2745-011 Queluz", closePoint(8, routePointId)),
            new ApiRoutePoint(9, 38.69251655457379, -9.314235207074216, "Destination",
                "Largo Marquês Pombal 21, 2780-289 Oeiras", closePoint(9, routePointId))
        )
    ));
  }

  @NotNull
  @Override
  public Completable reportProblem(@NotNull ApiProblem apiProblem) {
    return Completable.complete();
  }

  @NotNull
  @Override
  public Completable switchStatus(@NotNull ExecutorState executorState) {
    return Completable.fromAction(() -> observer.onNext(
        new StompFrame(
            Command.MESSAGE,
            Collections.singletonMap("Status", executorState.toString()),
            ""
        )
    ));
  }
}
