package com.cargopull.executor_driver.backend.websocket.outgoing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import com.google.gson.annotations.SerializedName;

public class ApiCancelOrderReason {

  @SerializedName("id")
  private final int id;
  @SerializedName("description")
  @NonNull
  private final String description;
  @SerializedName("name")
  @Nullable
  private final String name;

  public ApiCancelOrderReason(@NonNull CancelOrderReason cancelOrderReason) {
    this.id = cancelOrderReason.getId();
    this.description = cancelOrderReason.getName();
    this.name = cancelOrderReason.getUnusedName();
  }

  public int getId() {
    return id;
  }

  @NonNull
  public String getDescription() {
    return description;
  }

  @Nullable
  public String getName() {
    return name;
  }
}
