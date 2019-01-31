package com.cargopull.executor_driver.presentation.reportproblem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания загрузки списка ТС.
 */
final class ReportProblemViewStatePending implements ViewState<ReportProblemViewActions> {

  @Nullable
  private final ViewState<ReportProblemViewActions> parentViewState;

  ReportProblemViewStatePending(@Nullable ViewState<ReportProblemViewActions> parentViewState) {
    this.parentViewState = parentViewState;
  }

  @Override
  public void apply(@NonNull ReportProblemViewActions stateActions) {
    if (parentViewState != null) {
      parentViewState.apply(stateActions);
    }
    stateActions.showAvailableProblems(parentViewState != null);
    stateActions.showReportProblemPending(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ReportProblemViewStatePending that = (ReportProblemViewStatePending) o;

    return parentViewState != null ? parentViewState.equals(that.parentViewState)
        : that.parentViewState == null;
  }

  @Override
  public int hashCode() {
    return parentViewState != null ? parentViewState.hashCode() : 0;
  }
}
