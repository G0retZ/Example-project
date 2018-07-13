package com.cargopull.executor_driver.presentation.services;

import android.support.annotation.IntRange;
import com.cargopull.executor_driver.presentation.ViewModel;
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
   * задает позицию ползунка.
   *
   * @param position - позиция ползунка от 0 до 100
   */
  void setSliderPosition(@IntRange(from = 0, to = 100) int position);

  /**
   * Передает событие отработки ошибки.
   */
  void errorConsumed();
}
