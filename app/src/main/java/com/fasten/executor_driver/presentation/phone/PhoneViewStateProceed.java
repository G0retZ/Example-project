package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Финальное состояние при вводе кода по прозвону, которое отправит пользователя далее.
 */
public final class PhoneViewStateProceed implements ViewState<PhoneViewActions> {

  @Override
  public void apply(@NonNull PhoneViewActions stateActions) {
    stateActions.enableButton(false);
    stateActions.proceedNext();
  }
}
