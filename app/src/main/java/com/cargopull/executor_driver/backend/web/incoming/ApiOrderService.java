package com.cargopull.executor_driver.backend.web.incoming;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiOrderService {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("name")
  private String name;
  @SerializedName("price")
  private long price;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrderService() {
  }

  @SuppressWarnings("SameParameterValue")
  ApiOrderService(long id, @Nullable String name, long price) {
    this.id = id;
    this.name = name;
    this.price = price;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public long getPrice() {
    return price;
  }
}
