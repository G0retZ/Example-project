package com.cargopull.executor_driver.backend.web.incoming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * ответ от API содержащий данные для инициализации приложения.
 */
public class ApiInitialState {

  @Nullable
  @SerializedName("currentOrder")
  private ApiOrder currentOrder;
  @Nullable
  @SerializedName("preliminaryOrders")
  private List<ApiOrder> preliminaryOrders;
  @Nullable
  @SerializedName("status")
  private String executorStatus;
  @Nullable
  @SerializedName("balance")
  private ApiExecutorBalance balance;
  @Nullable
  @SerializedName("appVersionMessage")
  private String appVersionMessage;
  @SerializedName("value")
  private long customerConfirmationTimer;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiInitialState() {
  }

  public ApiInitialState(
      @NonNull ApiOrder currentOrder,
      @NonNull List<ApiOrder> preliminaryOrders, @NonNull String executorStatus,
      @NonNull ApiExecutorBalance balance, @Nullable String appVersionMessage,
      long customerConfirmationTimer) {
    this.currentOrder = currentOrder;
    this.preliminaryOrders = preliminaryOrders;
    this.executorStatus = executorStatus;
    this.balance = balance;
    this.appVersionMessage = appVersionMessage;
    this.customerConfirmationTimer = customerConfirmationTimer;
  }

  @Nullable
  public ApiOrder getCurrentOrder() {
    return currentOrder;
  }

  @Nullable
  public List<ApiOrder> getPreliminaryOrders() {
    return preliminaryOrders;
  }

  @Nullable
  public String getExecutorStatus() {
    return executorStatus;
  }

  @Nullable
  public ApiExecutorBalance getBalance() {
    return balance;
  }

  @Nullable
  public String getAppVersionMessage() {
    return appVersionMessage;
  }

  public long getCustomerConfirmationTimer() {
    return customerConfirmationTimer;
  }
}
