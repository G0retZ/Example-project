package com.cargopull.executor_driver.presentation.onlineswitch;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации по переключателю выхода на линию.
 */
@StringDef({
    OnlineSwitchNavigate.VEHICLE_OPTIONS
})
@Retention(RetentionPolicy.SOURCE)
public @interface OnlineSwitchNavigate {

  // Переход к настройке опций ТС.
  String VEHICLE_OPTIONS = "OnlineSwitch.to.VehicleOptions";
}
