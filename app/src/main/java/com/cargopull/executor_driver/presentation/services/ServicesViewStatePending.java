package com.cargopull.executor_driver.presentation.services;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания установки выбранных услуг.
 */
public final class ServicesViewStatePending implements ViewState<ServicesViewActions> {

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showServicesList(true);
    stateActions.showServicesPending(true);
    stateActions.showServicesListErrorMessage(false, 0);
    stateActions.showServicesListResolvableErrorMessage(false, 0);
  }
}
