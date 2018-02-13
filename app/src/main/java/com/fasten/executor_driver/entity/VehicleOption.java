package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность обобщенного параметра автомобиля. Содержит в себе ID, имя и значение.
 *
 * @param <V> тип значения.
 */
class VehicleOption<V> {

  private final int id;
  @NonNull
  private final String name;
  @NonNull
  private final V value;

  VehicleOption(int id, @NonNull String name, @NonNull V value) {
    this.id = id;
    this.name = name;
    this.value = value;
  }

  int getId() {
    return id;
  }

  @NonNull
  String getName() {
    return name;
  }

  @NonNull
  V getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "VehicleOption{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", value=" + value +
        '}';
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    VehicleOption<?> that = (VehicleOption<?>) o;

    if (id != that.id) {
      return false;
    }
    if (!name.equals(that.name)) {
      return false;
    }
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + name.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }
}
