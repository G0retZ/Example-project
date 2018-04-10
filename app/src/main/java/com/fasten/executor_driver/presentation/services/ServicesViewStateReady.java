package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Состояние гоновности списка услуг.
 */
public final class ServicesViewStateReady extends ServicesViewState {

  ServicesViewStateReady(@NonNull List<ServicesListItem> servicesListItems) {
    super(servicesListItems);
  }

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    super.apply(stateActions);
    stateActions.enableReadyButton(true);
    stateActions.showServicesList(true);
    stateActions.showServicesPending(false);
    stateActions.showServicesListErrorMessage(false, 0);
    stateActions.showServicesListResolvableErrorMessage(false, 0);
  }
}
