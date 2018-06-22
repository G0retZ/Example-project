package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние вида ошибки следующей маршрутной точки заказа.
 */
final class NextRoutePointViewStateServerDataError implements ViewState<NextRoutePointViewActions> {

  @Nullable
  private final ViewState<NextRoutePointViewActions> parentViewState;

  NextRoutePointViewStateServerDataError(
      @Nullable ViewState<NextRoutePointViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull NextRoutePointViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showNextRoutePointServerDataError();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NextRoutePointViewStateServerDataError that = (NextRoutePointViewStateServerDataError) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
