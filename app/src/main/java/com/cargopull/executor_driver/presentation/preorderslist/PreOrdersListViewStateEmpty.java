package com.cargopull.executor_driver.presentation.preorderslist;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.ArrayList;

/**
 * Состояние пустого списка предзаказов.
 */
final class PreOrdersListViewStateEmpty implements ViewState<PreOrdersListViewActions> {

  @Override
  public void apply(@NonNull PreOrdersListViewActions viewActions) {
    viewActions.showPreOrdersList(false);
    viewActions.showEmptyPreOrdersList(true);
    viewActions.showPreOrdersListPending(false);
    viewActions.setPreOrdersListItems(new ArrayList<>());
  }
}
