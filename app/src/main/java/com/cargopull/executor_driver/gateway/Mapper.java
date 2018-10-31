package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;

/**
 * Однонаправленный преобразователь данных из формы {@link F} в форму {@link T}.
 *
 * @param <F> тип исходных данных
 * @param <T> тип выходных данных
 */
public interface Mapper<F, T> {

  /**
   * Преобразует отдельный элемент типа {@link F} в тип {@link T}.
   *
   * @param from исходный элемент
   * @return преобразованный элемент
   * @throws Exception если возникает какое-либо несовместимое с преобразованием обстоятельство
   */
  @NonNull
  T map(@NonNull F from) throws Exception;
}
