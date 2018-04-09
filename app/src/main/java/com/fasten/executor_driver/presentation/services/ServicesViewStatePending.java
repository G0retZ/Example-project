package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;

/**
 * Состояние ожидания установки выбранных услуг.
 */
public final class ServicesViewStatePending extends ServicesViewState {

  ServicesViewStatePending(ServicesViewState servicesViewState) {
    super(servicesViewState);
  }

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    super.apply(stateActions);
    stateActions.enableReadyButton(false);
    stateActions.showServicesList(true);
    stateActions.showServicesPending(true);
    stateActions.showServicesListErrorMessage(false, 0);
    stateActions.showServicesListResolvableErrorMessage(false, 0);
  }
}
