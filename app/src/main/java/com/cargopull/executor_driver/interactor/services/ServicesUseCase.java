package com.cargopull.executor_driver.interactor.services;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;

/**
 * Юзкейс выбора услуг исполнителя.
 */
public interface ServicesUseCase {

  /**
   * Запрашивает список услуг, доступных для изменения исполнителем.
   *
   * @return {@link Single} результат запроса
   */
  @NonNull
  Single<List<Service>> loadServices();

  /**
   * Задает услуги выбранные исполнителем для выхода на линию.
   *
   * @param services список услуг
   * @return {@link Completable} результат сохранения и выхода на линию
   */
  Completable setSelectedServices(List<Service> services);
}
