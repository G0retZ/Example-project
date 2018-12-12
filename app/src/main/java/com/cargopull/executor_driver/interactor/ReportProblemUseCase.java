package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Problem;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;

/**
 * Юзкейс решения проблем с заказом.
 */
public interface ReportProblemUseCase {

  /**
   * Запрашивает список проблем с заказом, выдает последний закешированный результат.
   *
   * @return {@link Single<Problem>} результат запроса.
   */
  @NonNull
  Single<List<Problem>> getAvailableProblems();

  /**
   * Сообщить о проблеме с заказом.
   *
   * @param problem - проблема
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable reportProblem(@NonNull Problem problem);
}
