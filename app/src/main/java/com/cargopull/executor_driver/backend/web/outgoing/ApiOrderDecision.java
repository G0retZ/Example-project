package com.cargopull.executor_driver.backend.web.outgoing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Объект подтверждения/отказа от заказа для сериализации в JSON и отправки в АПИ.
 */
public class ApiOrderDecision {

  @SerializedName("id")
  @Expose
  private final long id;
  @SerializedName("approved")
  @Expose
  private final boolean approved;

  public ApiOrderDecision(long id, boolean approved) {
    this.id = id;
    this.approved = approved;
  }

  public long getId() {
    return id;
  }

  public boolean isApproved() {
    return approved;
  }
}
