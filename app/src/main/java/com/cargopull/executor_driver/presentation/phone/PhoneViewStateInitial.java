package com.cargopull.executor_driver.presentation.phone;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Начальное состояние ввода номера телефона.
 */
public final class PhoneViewStateInitial implements ViewState<PhoneViewActions> {

  @Override
  public void apply(@NonNull PhoneViewActions stateActions) {
    stateActions.enableButton(false);
  }
}
