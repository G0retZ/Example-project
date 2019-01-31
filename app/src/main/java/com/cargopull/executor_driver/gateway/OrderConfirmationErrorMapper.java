package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.ServerResponseException;
import com.cargopull.executor_driver.entity.OrderConfirmationFailedException;
import javax.inject.Inject;

/**
 * Преобразуем ошибку от сервера в ошибку бизнес логики.
 */
public class OrderConfirmationErrorMapper implements Mapper<Throwable, Throwable> {

  @Inject
  public OrderConfirmationErrorMapper() {
  }

  @NonNull
  @Override
  public Throwable map(@NonNull Throwable from) {
    if (from instanceof ServerResponseException) {
      if ("410".equals(((ServerResponseException) from).getCode())) {
        return new OrderConfirmationFailedException(from.getMessage());
      }
    }
    return from;
  }
}
