package com.fasten.executor_driver.presentation.services;

import com.fasten.executor_driver.presentation.ViewModel;
import java.util.List;

/**
 * ViewModel окна настройки услуг исполнителя.
 */
public interface ServicesViewModel extends ViewModel<ServicesViewActions> {

  /**
   * Передает список настроенных исполнителем услуг.
   *
   * @param servicesListItems - список сервисов
   */
  void setServices(List<ServicesListItem> servicesListItems);

  /**
   * Передает событие отработки ошибки.
   */
  void errorConsumed();
}
