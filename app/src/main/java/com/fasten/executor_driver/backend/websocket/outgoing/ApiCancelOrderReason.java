package com.fasten.executor_driver.backend.websocket.outgoing;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.CancelOrderReason;
import com.google.gson.annotations.SerializedName;

public class ApiCancelOrderReason {

  @SerializedName("id")
  private final int id;
  @SerializedName("description")
  @NonNull
  private final String description;

  public ApiCancelOrderReason(@NonNull CancelOrderReason cancelOrderReason) {
    this.id = cancelOrderReason.getId();
    this.description = cancelOrderReason.getName();
  }

  public int getId() {
    return id;
  }

  @NonNull
  public String getDescription() {
    return description;
  }
}
