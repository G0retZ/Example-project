package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания установки выбранных услуг.
 */
public final class ServicesViewStatePending implements ViewState<ServicesViewActions> {

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showServicesList(true);
    stateActions.showServicesPending(true);
    stateActions.showServicesListErrorMessage(false);
  }
}
