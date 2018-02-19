package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна настройки опций ТС исполнителя.
 */
@StringDef({
    VehicleOptionsNavigate.READY_FOR_ORDERS
})
@Retention(RetentionPolicy.SOURCE)
public @interface VehicleOptionsNavigate {

  /**
   * Переход к настройке к режиму ожидания заказа.
   */
  String READY_FOR_ORDERS = "VehicleOptions.to.ReadyForOrders";
}
