package com.cargopull.executor_driver.gateway;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.DriverBlockedException;
import javax.inject.Inject;
import retrofit2.HttpException;

/**
 * Преобразуем ошибку от сервера в ошибку бизнес логики.
 */
public class ErrorMapper implements Mapper<Throwable, Throwable> {

  private static final String ERROR_CODE_HEADER_NAME = "Code";

  @Inject
  public ErrorMapper() {
  }

  @NonNull
  @Override
  public Throwable map(@NonNull Throwable from) {
    if (from instanceof HttpException) {
      if (((HttpException) from).code() == 422) {
        String code = ((HttpException) from).response().headers().get(ERROR_CODE_HEADER_NAME);
        if (code != null) {
          switch (code) {
            case "422.1":
              return new DriverBlockedException("Водитель заблокирован!");
          }
        }
      }
    }
    return from;
  }
}