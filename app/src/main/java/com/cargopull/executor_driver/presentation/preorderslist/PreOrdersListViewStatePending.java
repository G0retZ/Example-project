package com.cargopull.executor_driver.presentation.preorderslist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания списка предзаказов.
 */
final class PreOrdersListViewStatePending implements ViewState<PreOrdersListViewActions> {

  @Nullable
  private final ViewState<PreOrdersListViewActions> parentViewState;

  PreOrdersListViewStatePending(
      @Nullable ViewState<PreOrdersListViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull PreOrdersListViewActions viewActions) {
    if (parentViewState != null) {
      parentViewState.apply(viewActions);
    }
    viewActions.showPreOrdersListPending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PreOrdersListViewStatePending that = (PreOrdersListViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
