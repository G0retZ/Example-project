package com.fasten.executor_driver.presentation.vehicleoptions;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Направления навигации из окна настройки опций ТС исполнителя.
 */
@StringDef({
    VehicleOptionsNavigate.SERVICES
})
@Retention(RetentionPolicy.SOURCE)
public @interface VehicleOptionsNavigate {

  // Переход к выбору услуг.
  String SERVICES = "VehicleOptions.to.Services";
}
