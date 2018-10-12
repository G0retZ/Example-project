package com.cargopull.executor_driver.presentation;

import androidx.annotation.NonNull;

/**
 * Состояние, которое применяется к View используя предоставленные действия. Может быть использован
 * как для восстановления последнего состояния, так и для разностных изменений.
 *
 * @param <A> тип, предоставляющий действия над View
 */
public interface ViewState<A> {

  /**
   * Применяет определенное состояние ко View используя предоставленные действия.
   *
   * @param a Действия над View, позволяющие применить состояние
   */
  void apply(@NonNull A a);
}
