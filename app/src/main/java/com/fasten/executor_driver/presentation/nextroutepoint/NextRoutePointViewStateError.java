package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние вида ошибки следующей маршрутной точки заказа.
 */
final class NextRoutePointViewStateError implements ViewState<NextRoutePointViewActions> {

  @Nullable
  private final ViewState<NextRoutePointViewActions> parentViewState;

  NextRoutePointViewStateError(@Nullable ViewState<NextRoutePointViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showNextRoutePointNetworkErrorMessage(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NextRoutePointViewStateError that = (NextRoutePointViewStateError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
