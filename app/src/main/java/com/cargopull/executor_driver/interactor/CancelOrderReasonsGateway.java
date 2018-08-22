package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Гейтвей отмены заказа.
 */
public interface CancelOrderReasonsGateway {

  /**
   * Получать список причин отмены от заказа.
   *
   * @return {@link Flowable<CancelOrderReason>} результат запроса.
   */
  @NonNull
  Flowable<List<CancelOrderReason>> loadCancelOrderReasons();
}
