package com.cargopull.executor_driver.backend.web.incoming;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiOrderCostDetails {

  @SerializedName("totalAmount")
  private long totalAmount;
  @SerializedName("estimatedAmount")
  private long estimatedAmount;
  @SerializedName(value = "overPackageStartCalculationTime", alternate = {"estimatedTime"})
  private long estimatedTime;
  @SerializedName("estimatedRouteDistance")
  private int estimatedRouteDistance;
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

  public ApiOrderCostDetails(
      long totalAmount, long estimatedAmount,
      long estimatedTime, int estimatedRouteDistance,
      @Nullable ApiOrderOptionsCostDetails apiOrderOptionsCostDetails,
      @Nullable ApiOrderOverPackage apiOrderOverPackage) {
    this.totalAmount = totalAmount;
    this.estimatedAmount = estimatedAmount;
    this.estimatedTime = estimatedTime;
    this.estimatedRouteDistance = estimatedRouteDistance;
    this.apiOrderOptionsCostDetails = apiOrderOptionsCostDetails;
    this.apiOrderOverPackage = apiOrderOverPackage;
  }

  public long getTotalAmount() {
    return totalAmount;
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

  @Nullable
  public ApiOrderOptionsCostDetails getApiOrderOptionsCostDetails() {
    return apiOrderOptionsCostDetails;
  }

  @Nullable
  public ApiOrderOverPackage getApiOrderOverPackage() {
    return apiOrderOverPackage;
  }
}
