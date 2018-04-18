package com.fasten.executor_driver.presentation.phone;

/**
 * Действия для смены состояния вида окна входа.
 */
public interface PhoneViewActions {

  /**
   * Сделать кнопку "Далее" нажимаемой.
   *
   * @param enable - нажимаема или нет?
   */
  void enableButton(boolean enable);
}
