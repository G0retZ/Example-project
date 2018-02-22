package com.fasten.executor_driver.presentation.selectedvehicle;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна информации о выбранном ТС.
 */
public interface SelectedVehicleViewModel extends ViewModel<SelectedVehicleViewActions> {

  /**
   * Запрашивает смену ТС.
   */
  void changeVehicle();
}
