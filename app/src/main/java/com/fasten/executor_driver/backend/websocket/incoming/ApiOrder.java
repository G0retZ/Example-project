package com.fasten.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.web.incoming.ApiOptionItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiOrder {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("estimatedAmount")
  private String estimatedAmount = "";
  @SerializedName("orderCost")
  private int orderCost;
  @SerializedName("excessCost")
  private int excessCost;
  @Nullable
  @SerializedName("comment")
  private String comment;
  @SerializedName("timeOut")
  private int timeout;
  @SerializedName("etaToStartPoint")
  private long etaToStartPoint;
  @SerializedName("confirmationTime")
  private long confirmationTime;
  @SerializedName("orderStartTime")
  private long orderStartTime;
  @Nullable
  @SerializedName("executorDistance")
  private ApiDriverDistancePair executorDistance;
  @Nullable
  @SerializedName("route")
  private List<ApiRoutePoint> route;
  @Nullable
  @SerializedName("optionsMobile")
  private List<ApiOptionItem> options;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrder() {
  }

  @SuppressWarnings("SameParameterValue")
  ApiOrder(long id, @Nullable String estimatedAmount,
      int orderCost, int excessCost, @Nullable String comment, int timeout,
      long etaToStartPoint, long confirmationTime,
      long orderStartTime, @Nullable ApiDriverDistancePair executorDistance,
      @Nullable List<ApiRoutePoint> route,
      @Nullable List<ApiOptionItem> options) {
    this.id = id;
    this.estimatedAmount = estimatedAmount;
    this.orderCost = orderCost;
    this.excessCost = excessCost;
    this.comment = comment;
    this.timeout = timeout;
    this.etaToStartPoint = etaToStartPoint;
    this.confirmationTime = confirmationTime;
    this.orderStartTime = orderStartTime;
    this.executorDistance = executorDistance;
    this.route = route;
    this.options = options;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getEstimatedAmount() {
    return estimatedAmount;
  }

  public int getOrderCost() {
    return orderCost;
  }

  public int getExcessCost() {
    return excessCost;
  }

  @Nullable
  public String getComment() {
    return comment;
  }

  public int getTimeout() {
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
  public List<ApiRoutePoint> getRoute() {
    return route;
  }

  @Nullable
  public List<ApiOptionItem> getOptions() {
    return options;
  }
}
