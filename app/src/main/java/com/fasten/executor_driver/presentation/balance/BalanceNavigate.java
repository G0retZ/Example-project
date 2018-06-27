package com.fasten.executor_driver.presentation.balance;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из баланса.
 */
@StringDef({
    BalanceNavigate.PAYMENT_OPTIONS,
    BalanceNavigate.SERVER_DATA_ERROR
})
@Retention(RetentionPolicy.SOURCE)
public @interface BalanceNavigate {

  // Переход к выбору способа оплаты.
  String PAYMENT_OPTIONS = "Balance.to.PaymentOptions";

  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "Balance.to.ServerDataError";
}
