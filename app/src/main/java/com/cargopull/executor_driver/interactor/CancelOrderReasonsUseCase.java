package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Юзкейс отмены заказа.
 */
public interface CancelOrderReasonsUseCase {

  /**
   * Запрашивает список причин отмены от заказа, выдает последний закешированный результат.
   *
   * @return {@link Flowable<CancelOrderReason>} результат запроса.
   */
  @NonNull
  Flowable<List<CancelOrderReason>> getCancelOrderReasons();
}
