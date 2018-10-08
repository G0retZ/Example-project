package com.cargopull.executor_driver.backend.web;

import android.support.annotation.NonNull;
import java.io.IOException;

/**
 * Исключение об ошибке сервера с кодом ошибки. Для последующего преобразования кода ошибки в
 * конкретный {@link Exception} для бизнес слоя.
 */

public class ServerResponseException extends IOException {

  @NonNull
  private final String code;

  public ServerResponseException(@NonNull String code, @NonNull String message) {
    super(message);
    this.code = code;
  }

  @NonNull
  public String getCode() {
    return code;
  }
}
