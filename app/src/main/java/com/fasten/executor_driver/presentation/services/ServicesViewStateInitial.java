package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import java.util.ArrayList;

/**
 * Начальное состояние списка услуг.
 */
public final class ServicesViewStateInitial implements ViewState<ServicesViewActions> {

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    stateActions.enableReadyButton(false);
    stateActions.showServicesList(true);
    stateActions.showServicesPending(false);
    stateActions.showServicesListErrorMessage(false);
    stateActions.setServicesListItems(new ArrayList<>());
  }
}
