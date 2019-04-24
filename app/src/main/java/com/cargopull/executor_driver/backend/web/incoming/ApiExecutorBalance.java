package com.cargopull.executor_driver.backend.web.incoming;

import com.google.gson.annotations.SerializedName;

public class ApiExecutorBalance {

  @SerializedName("mainAccount")
  private long mainAccount;
  @SerializedName("bonusAccount")
  private long bonusAccount;
  @SerializedName("nonCashAccount")
  private long nonCashAccount;

  /**
   * Конструктор без параметров желателен для безопасной работы Gson.
   */
  @SuppressWarnings({"unused", "SpellCheckingInspection"})
  public ApiExecutorBalance() {
  }

  ApiExecutorBalance(long mainAccount, long bonusAccount, long nonCashAccount) {
    this.mainAccount = mainAccount;
    this.bonusAccount = bonusAccount;
    this.nonCashAccount = nonCashAccount;
  }

  public long getMainAccount() {
    return mainAccount;
  }

  public long getBonusAccount() {
    return bonusAccount;
  }

  public long getNonCashAccount() {
    return nonCashAccount;
  }
}
