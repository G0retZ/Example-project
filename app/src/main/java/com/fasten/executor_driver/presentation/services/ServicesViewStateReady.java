package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние гоновности списка услуг.
 */
public final class ServicesViewStateReady implements ViewState<ServicesViewActions> {

  @NonNull
  private final List<ServicesListItem> servicesListItems;

  ServicesViewStateReady(@NonNull List<ServicesListItem> servicesListItems) {
    this.servicesListItems = servicesListItems;
  }

  @Override
  public void apply(@NonNull ServicesViewActions stateActions) {
    stateActions.enableReadyButton(true);
    stateActions.showServicesList(true);
    stateActions.showServicesPending(false);
    stateActions.showServicesListErrorMessage(false);
    stateActions.setServicesListItems(servicesListItems);
  }

  @Override
  public String toString() {
    return "ServicesViewStateReady{" +
        "servicesListItems=" + servicesListItems +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ServicesViewStateReady that = (ServicesViewStateReady) o;

    return servicesListItems.equals(that.servicesListItems);
  }

  @Override
  public int hashCode() {
    return servicesListItems.hashCode();
  }
}
