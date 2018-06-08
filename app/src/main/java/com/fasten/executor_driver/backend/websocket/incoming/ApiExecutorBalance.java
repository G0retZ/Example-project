package com.fasten.executor_driver.backend.websocket.incoming;

import com.google.gson.annotations.SerializedName;

public class ApiExecutorBalance {

  @SerializedName("mainAccount")
  private int mainAccount;
  @SerializedName("bonusAccount")
  private int bonusAccount;
  @SerializedName("nonCashAccount")
  private int nonCashAccount;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiExecutorBalance() {
  }

  ApiExecutorBalance(int mainAccount, int bonusAccount, int nonCashAccount) {
    this.mainAccount = mainAccount;
    this.bonusAccount = bonusAccount;
    this.nonCashAccount = nonCashAccount;
  }

  public int getMainAccount() {
    return mainAccount;
  }

  public int getBonusAccount() {
    return bonusAccount;
  }

  public int getNonCashAccount() {
    return nonCashAccount;
  }
}
