package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ServerResponseException;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import javax.inject.Inject;

/**
 * Преобразуем ошибку от сервера в ошибку бизнес логики.
 */
public class VehiclesAndOptionsErrorMapper implements Mapper<Throwable, Throwable> {

  @Inject
  public VehiclesAndOptionsErrorMapper() {
  }

  @NonNull
  @Override
  public Throwable map(@NonNull Throwable from) {
    if (from instanceof ServerResponseException) {
      if ("422.1".equals(((ServerResponseException) from).getCode())) {
        return new DriverBlockedException(from.getMessage());
      }
    }
    return from;
  }
}
