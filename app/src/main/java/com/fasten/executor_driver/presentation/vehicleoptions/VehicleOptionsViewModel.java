package com.fasten.executor_driver.presentation.vehicleoptions;

import com.fasten.executor_driver.presentation.ViewModel;
import java.util.List;

/**
 * ViewModel окна настройки опций ТС исполнителя.
 */
public interface VehicleOptionsViewModel extends ViewModel<VehicleOptionsViewActions> {

  /**
   * Передает список настроенных исполнителем опций ТС для занятия ТС.
   *
   * @param index - позиция ТС в списке
   */
  void setVehicleOptions(List<VehicleOptionsListItem<?>> index);
}
