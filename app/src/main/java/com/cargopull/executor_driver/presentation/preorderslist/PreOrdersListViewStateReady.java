package com.cargopull.executor_driver.presentation.preorderslist;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние непустого списка предзаказов.
 */
final class PreOrdersListViewStateReady implements ViewState<PreOrdersListViewActions> {

  @NonNull
  private final List<PreOrdersListItem> preOrdersListItems;

  PreOrdersListViewStateReady(@NonNull List<PreOrdersListItem> preOrdersListItems) {
    this.preOrdersListItems = preOrdersListItems;
  }

  @Override
  public void apply(@NonNull PreOrdersListViewActions stateActions) {
    stateActions.showPreOrdersList(true);
    stateActions.showEmptyPreOrdersList(false);
    stateActions.showPreOrdersListPending(false);
    stateActions.setPreOrdersListItems(preOrdersListItems);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PreOrdersListViewStateReady that = (PreOrdersListViewStateReady) o;

    return preOrdersListItems.equals(that.preOrdersListItems);
  }

  @Override
  public int hashCode() {
    return preOrdersListItems.hashCode();
  }
}
