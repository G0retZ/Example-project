package com.cargopull.executor_driver.backend.websocket.incoming;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiCancelOrderReason {

  @SerializedName("id")
  private int id;
  @SerializedName("description")
  private String description;
  @SerializedName("name")
  private String name;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiCancelOrderReason() {
  }

  ApiCancelOrderReason(int id, @Nullable String description, @Nullable String name) {
    this.id = id;
    this.description = description;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }
}
