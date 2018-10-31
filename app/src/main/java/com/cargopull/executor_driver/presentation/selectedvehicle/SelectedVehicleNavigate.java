package com.cargopull.executor_driver.presentation.selectedvehicle;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна информации о выбранном ТС.
 */
@StringDef({
    SelectedVehicleNavigate.VEHICLES
})
@Retention(RetentionPolicy.SOURCE)
public @interface SelectedVehicleNavigate {

  // Переход к выбору ТС.
  String VEHICLES = "SelectedVehicle.to.Vehicles";
}
