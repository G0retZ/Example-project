package com.fasten.executor_driver.presentation.services;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние списка услуг.
 */
public class ServicesViewState implements ViewState<ServicesViewActions> {

  @NonNull
  private final List<ServicesListItem> servicesListItems;

  ServicesViewState(ServicesViewState servicesViewState) {
    servicesListItems = servicesViewState.servicesListItems;
  }

  ServicesViewState(@NonNull List<ServicesListItem> servicesListItems) {
    this.servicesListItems = servicesListItems;
  }

  @Override
  @CallSuper
  public void apply(@NonNull ServicesViewActions stateActions) {
    stateActions.setServicesListItems(servicesListItems);
  }

  @Override
  public String toString() {
    return "ServicesViewState{" +
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

    ServicesViewState that = (ServicesViewState) o;

    return servicesListItems.equals(that.servicesListItems);
  }

  @Override
  public int hashCode() {
    return servicesListItems.hashCode();
  }
}
