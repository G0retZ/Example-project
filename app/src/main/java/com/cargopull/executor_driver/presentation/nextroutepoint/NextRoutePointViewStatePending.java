package com.cargopull.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида ожидания.
 */
final class NextRoutePointViewStatePending implements ViewState<NextRoutePointViewActions> {

  @Nullable
  private final ViewState<NextRoutePointViewActions> parentViewState;

  NextRoutePointViewStatePending(@Nullable ViewState<NextRoutePointViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showNextRoutePointPending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NextRoutePointViewStatePending that = (NextRoutePointViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
