package com.fasten.executor_driver.presentation.services;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна настройки опций ТС исполнителя.
 */
@StringDef({
    ServicesOptionsNavigate.READY_FOR_ORDERS
})
@Retention(RetentionPolicy.SOURCE)
public @interface ServicesOptionsNavigate {

  // Переход к режиму ожидания заказа.
  String READY_FOR_ORDERS = "VehicleOptions.to.ReadyForOrders";
}
