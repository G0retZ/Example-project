package com.cargopull.executor_driver.presentation.movingtoclienttimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class MovingToClientTimerViewStatePending implements ViewState<FragmentViewActions> {

  @Nullable
  private final ViewState<FragmentViewActions> parentViewState;

  MovingToClientTimerViewStatePending(@Nullable ViewState<FragmentViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull FragmentViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.blockWithPending("MovingToClientTimerViewState");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MovingToClientTimerViewStatePending that = (MovingToClientTimerViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
