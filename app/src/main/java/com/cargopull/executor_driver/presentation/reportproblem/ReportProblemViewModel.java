package com.cargopull.executor_driver.presentation.reportproblem;

import com.cargopull.executor_driver.entity.Problem;
import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна выбора причины отказа.
 */
public interface ReportProblemViewModel extends ViewModel<ReportProblemViewActions> {

  /**
   * Передает выбранную причину отказа.
   *
   * @param problem - эелемент списка причин отказа
   */
  void selectItem(Problem problem);
}
