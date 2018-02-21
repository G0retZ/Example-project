package com.fasten.executor_driver.presentation.choosevehicle;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна выбора ТС исполнителя.
 */
public interface ChooseVehicleViewModel extends ViewModel<ChooseVehicleViewActions> {

  /**
   * Передает позицию выбранного исполнителем ТС в списке отображенных ТС.
   *
   * @param index - позиция ТС в списке
   */
  void setSelection(int index);
}
