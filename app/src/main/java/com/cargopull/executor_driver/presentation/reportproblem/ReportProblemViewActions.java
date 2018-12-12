package com.cargopull.executor_driver.presentation.reportproblem;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Problem;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка ТС исполнителя.
 */
public interface ReportProblemViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showReportProblemPending(boolean pending);

  /**
   * Показать список причин для отказа.
   *
   * @param show - показать или нет?
   */
  void showAvailableProblems(boolean show);

  /**
   * Передать список ТС.
   *
   * @param problems - список ТС
   */
  void setAvailableProblems(@NonNull List<Problem> problems);
}
