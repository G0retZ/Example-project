package com.fasten.executor_driver.presentation.selectedvehicle;

/**
 * Действия для смены состояния вида окна информации о выбранном ТС.
 */
public interface SelectedVehicleViewActions {

  /**
   * Указать имя ТС.
   *
   * @param name - имя ТС
   */
  void setVehicleName(String name);

  /**
   * /**
   * Сделать кнопку "Сменить" нажимаемой.
   *
   * @param enable - нажимаема или нет?
   */
  void enableChangeButton(boolean enable);
}
