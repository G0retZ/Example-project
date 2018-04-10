package com.fasten.executor_driver.presentation.services;

import android.support.annotation.IntRange;

/**
 * Действия для смены состояния вида ползунка окна списка услуг исполнителя.
 */
public interface ServicesSliderViewActions {

  /**
   * Задать позицию ползунка цены.
   *
   * @param position - позиция от 0 до 100
   */
  void setSliderPosition(@IntRange(from = 0, to = 100) int position);

  /**
   * Задать минимальную цену ползунка.
   *
   * @param minPrice - минимальная цена
   */
  void setMinPrice(int minPrice);

  /**
   * Задать максимальную цену ползунка.
   *
   * @param maxPrice - максимальная цена
   */
  void setMaxPrice(int maxPrice);
}
