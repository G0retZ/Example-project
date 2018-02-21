package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Observable;

/**
 * Для обмена данными типа {@link D} в слое ЮзКейсов.
 *
 * @param <D> - тип данных
 */
public interface DataSharer<D> {

  /**
   * Шарит данные типа {@link D}.
   *
   * @param data данные
   */
  void share(@Nullable D data);

  /**
   * Возвращает {@link Observable<D>} для подписки на расшаренные данные. При первой подписке
   * возвращает последнее сохраненное значение.
   *
   * @return объект для подписки
   */
  @NonNull
  Observable<D> get();
}
