package com.cargopull.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiOrder {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("estimatedAmountText")
  private String estimatedAmountText;
  @SerializedName("estimatedAmount")
  private long estimatedAmount;
  @SerializedName("overPackageStartCalculationTime")
  private long estimatedTime;
  @SerializedName("estimatedRouteDistance")
  private int estimatedRouteDistance;
  @SerializedName("totalAmount")
  private long totalAmount;
  @Nullable
  @SerializedName("comment")
  private String comment;
  @SerializedName("timeOut")
  private long timeout;
  @SerializedName("etaToStartPoint")
  private long etaToStartPoint;
  @SerializedName("confirmationTime")
  private long confirmationTime;
  @SerializedName("startDate")
  private long orderStartTime;
  @Nullable
  @SerializedName("executorDistance")
  private ApiDriverDistancePair executorDistance;
  @Nullable
  @SerializedName("carSearchRequest")
  private ApiOrderService apiOrderService;
  @Nullable
  @SerializedName("route")
  private List<ApiRoutePoint> route;
  @Nullable
  @SerializedName("optionsMobile")
  private List<ApiOptionItem> options;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection", "WeakerAccess"})
  public ApiOrder() {
  }

  @SuppressWarnings("SameParameterValue")
  ApiOrder(long id, @Nullable String estimatedAmountText, long estimatedAmount, long estimatedTime,
      int estimatedRouteDistance, long totalAmount, @Nullable String comment, long timeout,
      long etaToStartPoint, long confirmationTime, long orderStartTime,
      @Nullable ApiDriverDistancePair executorDistance,
      @Nullable ApiOrderService apiOrderService,
      @Nullable List<ApiRoutePoint> route,
      @Nullable List<ApiOptionItem> options) {
    this.id = id;
    this.estimatedAmountText = estimatedAmountText;
    this.estimatedAmount = estimatedAmount;
    this.estimatedRouteDistance = estimatedRouteDistance;
    this.estimatedTime = estimatedTime;
    this.totalAmount = totalAmount;
    this.comment = comment;
    this.timeout = timeout;
    this.etaToStartPoint = etaToStartPoint;
    this.confirmationTime = confirmationTime;
    this.orderStartTime = orderStartTime;
    this.executorDistance = executorDistance;
    this.apiOrderService = apiOrderService;
    this.route = route;
    this.options = options;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getEstimatedAmountText() {
    return estimatedAmountText;
  }

  public long getEstimatedAmount() {
    return estimatedAmount;
  }

  public long getEstimatedTime() {
    return estimatedTime;
  }

  public int getEstimatedRouteDistance() {
    return estimatedRouteDistance;
  }

  public long getTotalAmount() {
    return totalAmount;
  }

  @Nullable
  public String getComment() {
    return comment;
  }

  public long getTimeout() {
    return timeout;
  }

  public long getEtaToStartPoint() {
    return etaToStartPoint;
  }

  public long getConfirmationTime() {
    return confirmationTime;
  }

  public long getOrderStartTime() {
    return orderStartTime;
  }

  @Nullable
  public ApiDriverDistancePair getExecutorDistance() {
    return executorDistance;
  }

  @Nullable
  public ApiOrderService getApiOrderService() {
    return apiOrderService;
  }

  @Nullable
  public List<ApiRoutePoint> getRoute() {
    return route;
  }

  @Nullable
  public List<ApiOptionItem> getOptions() {
    return options;
  }
}
