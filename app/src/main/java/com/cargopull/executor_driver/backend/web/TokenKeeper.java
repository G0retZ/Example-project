package com.cargopull.executor_driver.backend.web;

public interface TokenKeeper {

  /**
   * Сохраняет {@link String} токен в постоянном хранилище.
   *
   * @param token {@link String} сожержащая только токен
   */
  void saveToken(String token);

  /**
   * Берет токен из постоянного хранилища.
   *
   * @return {@link String} сожержащую только токен
   */
  String getToken();
}
