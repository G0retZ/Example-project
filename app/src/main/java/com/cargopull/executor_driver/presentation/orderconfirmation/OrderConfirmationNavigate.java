package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации с карточки предзаказа.
 */
@StringDef({
    OrderConfirmationNavigate.CLOSE
})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderConfirmationNavigate {

  // Переход к настройке опций ТС.
  String CLOSE = "OrderConfirmation.to.Close";
}
