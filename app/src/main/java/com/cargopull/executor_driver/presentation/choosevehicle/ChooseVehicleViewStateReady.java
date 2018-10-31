package com.cargopull.executor_driver.presentation.choosevehicle;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние готовности списка ТС к выбору ТС.
 */
final class ChooseVehicleViewStateReady implements ViewState<ChooseVehicleViewActions> {

  @NonNull
  private final List<ChooseVehicleListItem> chooseVehicleListItems;

  ChooseVehicleViewStateReady(@NonNull List<ChooseVehicleListItem> chooseVehicleListItems) {
    this.chooseVehicleListItems = chooseVehicleListItems;
  }

  @Override
  public void apply(@NonNull ChooseVehicleViewActions stateActions) {
    stateActions.showVehicleList(true);
    stateActions.showVehicleListPending(false);
    stateActions.showVehicleListErrorMessage(false);
    stateActions.setVehicleListItems(chooseVehicleListItems);
  }

  @Override
  public String toString() {
    return "ChooseVehicleViewStateReady{" +
        "chooseVehicleListItems=" + chooseVehicleListItems +
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

    ChooseVehicleViewStateReady that = (ChooseVehicleViewStateReady) o;

    return chooseVehicleListItems.equals(that.chooseVehicleListItems);
  }

  @Override
  public int hashCode() {
    return chooseVehicleListItems.hashCode();
  }
}
