package com.cargopull.executor_driver.backend.websocket.incoming;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiOrderOptionCost {

  @Nullable
  @SerializedName("optionName")
  private String optionName;
  @SerializedName("optionPrice")
  private long optionPrice;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrderOptionCost() {
  }

  @SuppressWarnings("unused")
  public ApiOrderOptionCost(@Nullable String optionName, long optionPrice) {
    this.optionName = optionName;
    this.optionPrice = optionPrice;
  }

  @Nullable
  public String getOptionName() {
    return optionName;
  }

  public long getOptionPrice() {
    return optionPrice;
  }
}
