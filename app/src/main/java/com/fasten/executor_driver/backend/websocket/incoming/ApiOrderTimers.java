package com.fasten.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiOrderTimers {

  @Nullable
  @SerializedName("overPackageTimer")
  private Long overPackageTimer;
  @Nullable
  @SerializedName("overPackagePeriod")
  private Long overPackagePeriod;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrderTimers() {
  }

  @SuppressWarnings("SameParameterValue")
  ApiOrderTimers(@Nullable Long overPackageTimer, @Nullable Long overPackagePeriod) {
    this.overPackageTimer = overPackageTimer;
    this.overPackagePeriod = overPackagePeriod;
  }

  @Nullable
  public Long getOverPackageTimer() {
    return overPackageTimer;
  }

  @Nullable
  public Long getOverPackagePeriod() {
    return overPackagePeriod;
  }
}
