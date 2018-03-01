package com.fasten.executor_driver.interactor.services;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;

/**
 * Юзкейс выбора услуг исполнителя.
 */
@SuppressWarnings("unused")
public interface ServicesUseCase {

  /**
   * Запрашивает список услуг, доступных для изменения исполнителем.
   *
   * @return {@link Observable} результат запроса
   */
  @NonNull
  Observable<List<Service>> getVehicleOptions();

  /**
   * Задает опции выбранного ТС для сохранения и выхода на линию.
   *
   * @param services список услуг
   * @return {@link Completable} результат сохранения и выхода на линию
   */
  Completable setSelectedVehicleOptions(List<Service> services);
}
