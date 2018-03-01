package com.fasten.executor_driver.presentation.options;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна настройки опций ТС исполнителя.
 */
public interface OptionsViewModel extends ViewModel<OptionsViewActions> {

  /**
   * Передает список настроенных исполнителем опций ТС и исполнителя для занятия ТС.
   *
   * @param optionsListItems - позиция ТС в списке
   */
  void setOptions(OptionsListItems optionsListItems);
}
