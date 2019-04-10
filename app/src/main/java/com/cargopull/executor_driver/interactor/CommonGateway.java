package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Общий гейтвей получения любых данных потоком.
 */
public interface CommonGateway<D> {

  /**
   * Ожидает данные.
   *
   * @return {@link Flowable<D>} данные.
   */
  @NonNull
  Flowable<D> getData();
}
