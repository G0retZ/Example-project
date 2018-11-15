package com.cargopull.executor_driver.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Бизнес сущность обобщенного параметра.
 *
 * @param <V> тип значения
 */
public interface Option<V> {

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
   * Возвращает детальное описание опции.
   *
   * @return детальное описание опции.
   */
  @Nullable
  String getDescription();

  /**
   * Возвращает значение опции.
   *
   * @return значение опции.
   */
  @NonNull
  V getValue();

  /**
   * Возвращает новый объект с заданным значением.
   *
   * @param value значение
   */
  @NonNull
  Option<V> setValue(@NonNull V value);

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
