package com.fasten.executor_driver.presentation.choosevehicle;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel окна выбора ТС исполнителя.
 */
interface ChooseVehicleViewModel {

  /**
   * Возвращает состояние вида для применения.
   *
   * @return - {@link ViewState} состояние вида.
   */
  @NonNull
  LiveData<ViewState<ChooseVehicleViewActions>> getViewStateLiveData();

  /**
   * Возвращает событие навигации для перехода.
   *
   * @return - {@link ChooseVehicleNavigate} событие навигации.
   */
  @NonNull
  LiveData<String> getNavigationLiveData();

  /**
   * Передает позицию выбранного исполнителем ТС в списке отображенных ТС.
   *
   * @param index - позиция ТС в списке.
   */
  void setSelection(int index);
}
