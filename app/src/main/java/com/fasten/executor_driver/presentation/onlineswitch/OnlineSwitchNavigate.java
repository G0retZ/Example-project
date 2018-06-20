package com.fasten.executor_driver.presentation.onlineswitch;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации при старте приложения.
 */
@StringDef({
    OnlineSwitchNavigate.SERVICES,
    OnlineSwitchNavigate.VEHICLE_OPTIONS,
    OnlineSwitchNavigate.NO_CONNECTION
})
@Retention(RetentionPolicy.SOURCE)
public @interface OnlineSwitchNavigate {

  // Переход к выбору услуг.
  String SERVICES = "OnlineSwitch.to.Services";

  // Переход к настройке опций ТС.
  String VEHICLE_OPTIONS = "OnlineButton.to.VehicleOptions";

  // Переход к ошибке соединения.
  String NO_CONNECTION = "OnlineButton.to.NoConnection";
}
