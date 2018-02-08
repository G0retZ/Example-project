package com.fasten.executor_driver.interactor.map;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;

/**
 * Юзкейс тепловой карты
 */
public interface HeatMapUseCase {

  /**
   * Запрашивает и слушает обновления тепловой карты.
   *
   * @return {@link Flowable<String>} данные для тепловой карты в виде строки GeoJSON.
   */
  @NonNull
  Flowable<String> loadHeatMap();
}
