package com.cargopull.executor_driver.presentation.onlinebutton;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации по кнопке выхода на линию.
 */
@StringDef({
    OnlineButtonNavigate.VEHICLE_OPTIONS,
    OnlineButtonNavigate.DRIVER_BLOCKED,
    OnlineButtonNavigate.NO_VEHICLES,
    OnlineButtonNavigate.NO_FREE_VEHICLES
})
@Retention(RetentionPolicy.SOURCE)
public @interface OnlineButtonNavigate {

  // Переход к выбору опций ТС.
  String VEHICLE_OPTIONS = "OnlineButton.to.VehicleOptions";

  // Переход к решению блокировки водителя.
  String DRIVER_BLOCKED = "OnlineButton.to.DriverBlocked";

  // Переход к решению отсутствия ТС.
  String NO_VEHICLES = "OnlineButton.to.NoVehicles";

  // Переход к решению отсутствия свободных ТС.
  String NO_FREE_VEHICLES = "OnlineButton.to.NoFreeVehicles";
}
