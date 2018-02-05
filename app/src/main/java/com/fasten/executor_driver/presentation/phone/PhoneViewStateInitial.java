package com.fasten.executor_driver.presentation.phone;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Начальное состояние ввода номера телефона
 */
public final class PhoneViewStateInitial implements ViewState<PhoneViewActions> {

  @Override
  public void apply(@NonNull PhoneViewActions stateActions) {
    stateActions.enableButton(false);
  }
}
