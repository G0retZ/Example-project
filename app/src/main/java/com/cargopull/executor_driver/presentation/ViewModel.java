package com.cargopull.executor_driver.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

/**
 * ViewModel который представляет собой модель вида на экране. Выделен для высушивания (DRY) кода.
 */
public interface ViewModel<A> {

  /**
   * Возвращает состояние вида для применения.
   *
   * @return - {@link ViewState} состояние вида
   */
  @NonNull
  LiveData<ViewState<A>> getViewStateLiveData();

  /**
   * Возвращает событие навигации для перехода.
   *
   * @return - {@link String} событие и направление навигации
   */
  @NonNull
  LiveData<String> getNavigationLiveData();
}
