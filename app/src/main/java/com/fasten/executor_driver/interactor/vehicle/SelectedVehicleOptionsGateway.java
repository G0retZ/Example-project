package com.fasten.executor_driver.interactor.vehicle;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Option;
import io.reactivex.Single;
import java.util.List;

/**
 * Гейтвей опций текущего ТС и исполнителя, кэширует полученные данные, и выдает из кэша всегда.
 */
public interface SelectedVehicleOptionsGateway {

  /**
   * Запрашивает у сервера опции текущего ТС. Сервер должен вернуть либо список, либо
   * ошибку с причиной отказа.
   *
   * @return {@link Single} результат запроса списка опций текущего ТС
   */
  @NonNull
  Single<List<Option>> getVehicleOptions();

  /**
   * Запрашивает у сервера список опций Исполнителя. Сервер должен вернуть либо список, либо
   * ошибку с причиной отказа.
   *
   * @return {@link Single} результат запроса списка опций Исполнителя
   */
  @SuppressWarnings("unused")
  @NonNull
  Single<List<Option>> getExecutorOptions();
}
