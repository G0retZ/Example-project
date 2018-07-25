package com.cargopull.executor_driver.presentation.services;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel ползунка окна настройки услуг исполнителя.
 */
public interface ServicesSliderViewModel extends ViewModel<ServicesSliderViewActions> {

  /**
   * Передает событие обновления списка услуг.
   */
  void refresh();
}
