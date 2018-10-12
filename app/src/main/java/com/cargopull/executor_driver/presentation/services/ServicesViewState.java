package com.cargopull.executor_driver.presentation.services;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние списка услуг.
 */
public class ServicesViewState implements ViewState<ServicesViewActions> {

  @NonNull
  private final List<ServicesListItem> servicesListItems;

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
