package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.CancelOrderReason;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;

/**
 * Юзкейс отмены заказа.
 */
public interface CancelOrderUseCase {

  /**
   * Запрашивает список причин отмены от заказа, выдает последний закешированный результат, если не сброшен.
   *
   * @param reset - сбросить ли кеш? Сбрасывает кеш для всех последующих запросов к юзкейсу.
   * @return {@link Flowable<CancelOrderReason>} результат запроса.
   */
  @NonNull
  Flowable<List<CancelOrderReason>> getCancelOrderReasons(boolean reset);

  /**
   * Отменить заказ с причиной.
   *
   * @param cancelOrderReason - причина отмены
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable cancelOrder(@NonNull CancelOrderReason cancelOrderReason);
}
