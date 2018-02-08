package com.fasten.executor_driver.presentation.map;

import android.support.annotation.Nullable;

/**
 * Действия для смены состояния вида карты
 */
public interface MapViewActions {

  /**
   * Обновить отображение тепловой карты
   *
   * @param geoJson - данные тепловой карты в виде GeoJSON. Если null, значит данных нет -
   * использовать последние имеющиеся данные.
   */
  void updateHeatMap(@Nullable String geoJson);
}
