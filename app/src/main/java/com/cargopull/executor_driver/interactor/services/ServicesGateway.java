package com.cargopull.executor_driver.interactor.services;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;

/**
 * Гейтвей настройки услуг исполнителя.
 */
public interface ServicesGateway {

  /**
   * Запрашивает у сервера список доступных услуг для исполнителя на выбранном ТС. В принципе на
   * текущий момент никаких заслуживающих внимание причин для отказа нету.
   *
   * @return результат запроса
   */
  Single<List<Service>> getServices();

  /**
   * Запрашивает у сервера задать оказываемые исполнителем услуги для выхода на линию. Сервер должен
   * вернуть либо успех, либо ошибку с причиной отказа. В принципе на текущий момент никаких
   * заслуживающих внимание причин для отказа нету.
   *
   * @param services список услуг
   * @return результат запроса
   */
  @NonNull
  Completable sendSelectedServices(@NonNull List<Service> services);
}
