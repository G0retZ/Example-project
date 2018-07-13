package com.cargopull.executor_driver.interactor.map;

import android.support.annotation.NonNull;
import io.reactivex.Single;

/**
 * Гейтвей тепловой карты.
 */
public interface HeatMapGateway {

  /**
   * Запрашивает тепловую карту с сервера.
   *
   * @return {@link Single<String>} данные для тепловой карты в виде строки GeoJSON
   */
  @NonNull
  Single<String> getHeatMap();
}
