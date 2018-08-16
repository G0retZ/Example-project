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
  @SerializedName("approve")
  @Expose
  private final boolean approve;

  ApiOrderDecision(long id, boolean approve) {
    this.id = id;
    this.approve = approve;
  }

  public long getId() {
    return id;
  }

  public boolean isApprove() {
    return approve;
  }
}
