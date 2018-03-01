package com.fasten.executor_driver.presentation.vehicleoptions;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна настройки опций ТС исполнителя.
 */
public interface VehicleOptionsViewModel extends ViewModel<VehicleOptionsViewActions> {

  /**
   * Передает список настроенных исполнителем опций ТС и исполнителя для занятия ТС.
   *
   * @param optionsListItems - позиция ТС в списке
   */
  void setVehicleAndDriverOptions(OptionsListItems optionsListItems);
}
