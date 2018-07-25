package com.cargopull.executor_driver.presentation.choosevehicle;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна выбора ТС исполнителя.
 */
@StringDef({
    ChooseVehicleNavigate.VEHICLE_OPTIONS
})
@Retention(RetentionPolicy.SOURCE)
public @interface ChooseVehicleNavigate {

  // Переход к настройке ТС исполнителя.
  String VEHICLE_OPTIONS = "ChooseVehicle.to.VehicleOptions";
}
