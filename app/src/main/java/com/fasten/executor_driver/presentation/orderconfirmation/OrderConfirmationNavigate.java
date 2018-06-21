package com.fasten.executor_driver.presentation.orderconfirmation;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна звонка клиенту.
 */
@StringDef({
    OrderConfirmationNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
@interface OrderConfirmationNavigate {

  // Переход к ошибке соединения.
  String NO_CONNECTION = "OrderConfirmation.to.NoConnection";
}
