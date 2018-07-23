package com.cargopull.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiOrderOptionsCostDetails {

  @Nullable
  @SerializedName("payOptionDetails")
  private List<ApiOrderOptionCost> optionsCosts;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrderOptionsCostDetails() {
  }

  @SuppressWarnings("unused")
  public ApiOrderOptionsCostDetails(@Nullable List<ApiOrderOptionCost> optionsCosts) {
    this.optionsCosts = optionsCosts;
  }

  @Nullable
  public List<ApiOrderOptionCost> getOptionsCosts() {
    return optionsCosts;
  }
}
