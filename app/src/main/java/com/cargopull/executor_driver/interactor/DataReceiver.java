package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Observable;

/**
 * Для обмена данными типа {@link D} в слое ЮзКейсов.
 *
 * @param <D> - тип данных
 */
public interface DataReceiver<D> {

  /**
   * Возвращает {@link Observable<D>} для подписки на расшаренные данные. При первой подписке
   * возвращает последнее сохраненное значение.
   *
   * @return объект для подписки
   */
  @NonNull
  Observable<D> get();
}
