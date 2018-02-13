package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Бизнес сущность двоичного параметра автомобиля.
 */
class VehicleOptionBoolean extends VehicleOption<Boolean> {

  VehicleOptionBoolean(long id, @NonNull String name, @NonNull Boolean value) {
    super(id, name, value);
  }

  /**
   * Возвращает новый объект с заданным значением.
   *
   * @param value значение
   */
  VehicleOptionBoolean setValue(@NonNull Boolean value) {
    return new VehicleOptionBoolean(getId(), getName(), value);
  }
}
