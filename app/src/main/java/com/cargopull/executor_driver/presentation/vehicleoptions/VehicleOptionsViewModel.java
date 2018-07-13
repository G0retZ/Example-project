package com.cargopull.executor_driver.presentation.vehicleoptions;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна настройки опций ТС исполнителя.
 */
public interface VehicleOptionsViewModel extends ViewModel<VehicleOptionsViewActions> {

  /**
   * Передает список настроенных исполнителем опций ТС и исполнителя для занятия ТС.
   *
   * @param vehicleOptionsListItems - список настроенных опций
   */
  void setOptions(VehicleOptionsListItems vehicleOptionsListItems);
}
