package com.cargopull.executor_driver.entity;

import androidx.annotation.Nullable;

/**
 * Валидатор входных данных.
 *
 * @param <T> тип для валидации
 */
public interface Validator<T> {

  /**
   * @param data входные данные
   * @throws ValidationException исключение валидации
   */
  void validate(@Nullable T data) throws Exception;
}
