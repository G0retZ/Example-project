package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Problem;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;

/**
 * Гейтвей проблем заказа.
 */
public interface ReportProblemGateway {

  /**
   * Отправить проблему с причиной.
   *
   * @return {@link Single<Problem>} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Single<List<Problem>> getProblems();

  /**
   * Отправить проблему с причиной.
   *
   * @param problem - проблема
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable reportProblem(@NonNull Problem problem);
}
