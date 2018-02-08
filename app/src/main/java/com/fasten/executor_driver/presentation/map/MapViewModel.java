package com.fasten.executor_driver.presentation.map;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel окна карты
 */
public interface MapViewModel {

  /**
   * Возвращает состояние вида для применения
   *
   * @return - {@link ViewState} состояние вида
   */
  @NonNull
  LiveData<ViewState<MapViewActions>> getViewStateLiveData();
}
