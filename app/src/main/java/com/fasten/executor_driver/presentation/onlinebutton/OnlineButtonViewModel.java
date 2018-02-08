package com.fasten.executor_driver.presentation.onlinebutton;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel кнопки выхода на линию.
 */
public interface OnlineButtonViewModel {

  /**
   * Возвращает состояние вида для применения.
   *
   * @return - {@link ViewState} состояние вида.
   */
  @NonNull
  LiveData<ViewState<OnlineButtonViewActions>> getViewStateLiveData();

  /**
   * Запрашивает выход на линию.
   */
  void goOnline();
}
