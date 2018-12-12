package com.cargopull.executor_driver.presentation.reportproblem;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.presentation.ViewState;
import java.util.List;

/**
 * Состояние вида списка выбора причины отказа.
 */
final class ReportProblemViewState implements ViewState<ReportProblemViewActions> {

  @NonNull
  private final List<Problem> problems;

  ReportProblemViewState(@NonNull List<Problem> problems) {
    this.problems = problems;
  }

  @Override
  public void apply(@NonNull ReportProblemViewActions stateActions) {
    stateActions.showAvailableProblems(true);
    stateActions.showReportProblemPending(false);
    stateActions.setAvailableProblems(problems);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ReportProblemViewState that = (ReportProblemViewState) o;

    return problems.equals(that.problems);
  }

  @Override
  public int hashCode() {
    return problems.hashCode();
  }
}
