package com.cargopull.executor_driver.application;

import android.content.Context;
import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.cargopull.executor_driver.presentation.geolocation.GeoLocationNavigate;
import com.cargopull.executor_driver.utils.Consumer;

/**
 * Маппер направлений перехода в обработчики передаваемой активити.
 */
public interface NavigationMapper {

  /**
   * Вернуть обработчик перехода с активити в аргументе, соответствующий направлению навигации.
   *
   * @param destination направление навигации.
   */
  @NonNull
  Consumer<Context> navigateTo(
      @NonNull @ExecutorStateNavigate @GeoLocationNavigate String destination);

  /**
   * Вернуть обработчик перехода с активити в аргументе, соответствующий последнему направлению
   * навигации.
   */
  @NonNull
  Consumer<Context> navigateToRecent();
}
