package com.cargopull.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiOrderOverPackage {

  @SerializedName("estimatedCsrPackageCost")
  private long estimatedPackageCost;
  @SerializedName("overPackageTime")
  private long overPackageTime;
  @SerializedName("overPackageCsrCost")
  private long overPackageCost;
  @Nullable
  @SerializedName("moverOverPackageCostName")
  private String overPackageMoverCostName;
  @SerializedName("moverOverPackageCost")
  private long overPackageMoverCost;
  @SerializedName("overPackageCsrPrice")
  private long overPackageTariff;
  @Nullable
  @SerializedName("overPackageMoverPriceName")
  private String overPackageMoverTariffName;
  @SerializedName("overPackageMoverPrice")
  private long overPackageMoverTariff;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrderOverPackage() {
  }

  @SuppressWarnings("unused")
  public ApiOrderOverPackage(long estimatedPackageCost, long overPackageTime,
      long overPackageCost,
      @Nullable String overPackageMoverCostName, long overPackageMoverCost,
      long overPackageTariff,
      @Nullable String overPackageMoverTariffName, long overPackageMoverTariff) {
    this.estimatedPackageCost = estimatedPackageCost;
    this.overPackageTime = overPackageTime;
    this.overPackageCost = overPackageCost;
    this.overPackageMoverCostName = overPackageMoverCostName;
    this.overPackageMoverCost = overPackageMoverCost;
    this.overPackageTariff = overPackageTariff;
    this.overPackageMoverTariffName = overPackageMoverTariffName;
    this.overPackageMoverTariff = overPackageMoverTariff;
  }

  public long getEstimatedPackageCost() {
    return estimatedPackageCost;
  }

  public long getOverPackageTime() {
    return overPackageTime;
  }

  public long getOverPackageCost() {
    return overPackageCost;
  }

  @Nullable
  public String getOverPackageMoverCostName() {
    return overPackageMoverCostName;
  }

  public long getOverPackageMoverCost() {
    return overPackageMoverCost;
  }

  public long getOverPackageTariff() {
    return overPackageTariff;
  }

  @Nullable
  public String getOverPackageMoverTariffName() {
    return overPackageMoverTariffName;
  }

  public long getOverPackageMoverTariff() {
    return overPackageMoverTariff;
  }
}
