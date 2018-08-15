package com.cargopull.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.cargopull.executor_driver.backend.web.incoming.ApiOptionItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiOrderCostDetails extends ApiOrder {

  @Nullable
  @SerializedName("estimatedAmountDetalization")
  private ApiOrderOptionsCostDetails apiOrderOptionsCostDetails;
  @Nullable
  @SerializedName("mobileDetalization")
  private ApiOrderOverPackage apiOrderOverPackage;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrderCostDetails() {
  }

  public ApiOrderCostDetails(long id, @Nullable String estimatedAmountText, int estimatedAmount,
      long estimatedTime, int estimatedRouteDistance, int totalAmount,
      @Nullable String comment, long timeout, long etaToStartPoint, long confirmationTime,
      long startTime,
      long scheduledStartTime,
      @Nullable ApiDriverDistancePair executorDistance,
      @Nullable ApiOrderService apiOrderService,
      @Nullable List<ApiRoutePoint> route,
      @Nullable List<ApiOptionItem> options,
      @Nullable ApiOrderOptionsCostDetails apiOrderOptionsCostDetails,
      @Nullable ApiOrderOverPackage apiOrderOverPackage) {
    super(id, estimatedAmountText, estimatedAmount, estimatedTime, estimatedRouteDistance,
        totalAmount, comment, timeout, etaToStartPoint, confirmationTime, startTime,
        scheduledStartTime, executorDistance, apiOrderService, route, options);
    this.apiOrderOptionsCostDetails = apiOrderOptionsCostDetails;
    this.apiOrderOverPackage = apiOrderOverPackage;
  }

  @Nullable
  public ApiOrderOptionsCostDetails getApiOrderOptionsCostDetails() {
    return apiOrderOptionsCostDetails;
  }

  @Nullable
  public ApiOrderOverPackage getApiOrderOverPackage() {
    return apiOrderOverPackage;
  }
}
