package com.fasten.executor_driver.presentation.choosevehicle;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна выбора ТС исполнителя.
 */
@StringDef({
    ChooseVehicleNavigate.VEHICLE_OPTIONS,
    ChooseVehicleNavigate.AUTO_VEHICLE_OPTIONS
})
@Retention(RetentionPolicy.SOURCE)
@interface ChooseVehicleNavigate {

  /**
   * Переход к настройке ТС исполнителя.
   */
  String VEHICLE_OPTIONS = "ChooseVehicle.to.VehicleOptions";
  String AUTO_VEHICLE_OPTIONS = "ChooseVehicle.auto.VehicleOptions";
}
