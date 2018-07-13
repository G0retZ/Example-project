package com.cargopull.executor_driver.backend.websocket.incoming;

import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public class ApiOrderService {

  @SerializedName("id")
  private long id;
  @Nullable
  @SerializedName("name")
  private String name;
  @SerializedName("price")
  private int price;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiOrderService() {
  }

  @SuppressWarnings("SameParameterValue")
  ApiOrderService(long id, @Nullable String name, int price) {
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

  public int getPrice() {
    return price;
  }
}
