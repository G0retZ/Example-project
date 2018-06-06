package com.fasten.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiCancelOrderReason {

  @SerializedName("id")
  private int id;
  @SerializedName("description")
  private String description;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiCancelOrderReason() {
  }

  ApiCancelOrderReason(int id, @Nullable String description) {
    this.id = id;
    this.description = description;
  }

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }
}
