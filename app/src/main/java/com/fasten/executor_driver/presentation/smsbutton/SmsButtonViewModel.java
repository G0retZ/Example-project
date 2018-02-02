package com.fasten.executor_driver.presentation.smsbutton;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel кнопки с таймером.
 */
public interface SmsButtonViewModel {

  /**
   * Возвращает состояние вида для применения.
   *
   * @return - {@link ViewState} состояние вида.
   */
  @NonNull
  LiveData<ViewState<SmsButtonViewActions>> getViewStateLiveData();

  /**
   * Передает событие нажатия кнопки.
   *
   * @return возвращает, если нажатие было легальным и на него можно реагировать.
   */
  boolean buttonClicked();
}
