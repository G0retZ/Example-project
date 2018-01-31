package com.fasten.executor_driver.presentation.timeoutbutton;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel кнопки с таймером.
 */
public interface TimeoutButtonViewModel {

  /**
   * Возвращает состояние вида для применения.
   *
   * @return - {@link ViewState} состояние вида.
   */
  @NonNull
  LiveData<ViewState<TimeoutButtonViewActions>> getViewStateLiveData();

  /**
   * Передает событие нажатия кнопки.
   *
   * @return возвращает, если нажатие было легальным и на него можно реагировать.
   */
  boolean buttonClicked();
}
