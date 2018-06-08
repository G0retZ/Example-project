package com.fasten.executor_driver.presentation.coreBalance;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при получении баланса.
 */
@StringDef({
    CoreBalanceNavigate.SERVER_DATA_ERROR
})
@Retention(RetentionPolicy.SOURCE)
@interface CoreBalanceNavigate {

  // Переход к проблеме совместимости формата данных с сервером.
  String SERVER_DATA_ERROR = "to.ServerDataError";
}
