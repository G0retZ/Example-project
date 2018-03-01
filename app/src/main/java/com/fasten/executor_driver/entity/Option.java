package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность обобщенного параметра.
 *
 * @param <V> тип значения
 */
public interface Option<V> {

  /**
   * Возвращает новый объект с заданным значением.
   *
   * @param value значение
   */
  @NonNull
  Option<V> setValue(@NonNull V value);

  /**
   * Возвращает ID опции.
   *
   * @return ID опции.
   */
  long getId();

  /**
   * Возвращает имя опции.
   *
   * @return имя опции.
   */
  @NonNull
  String getName();

  /**
   * Возвращает изменяемость опции.
   *
   * @return true если опция доступна для изменения, false в иных случаях.
   */
  boolean isVariable();

  /**
   * Возвращает значение опции.
   *
   * @return значение опции.
   */
  @NonNull
  V getValue();

  /**
   * Возвращает минимальное значение опции.
   *
   * @return минимальное значение опции.
   */
  @NonNull
  V getMinValue();


  /**
   * Возвращает максимальное значение опции.
   *
   * @return максимальное значение опции.
   */
  @NonNull
  V getMaxValue();
}
