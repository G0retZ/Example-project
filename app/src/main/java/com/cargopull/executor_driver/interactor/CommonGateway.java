package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Общий гейтвей получения любых данных.
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
