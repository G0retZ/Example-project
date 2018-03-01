package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность услуги. Содержит в себе ID, имя, цену и значение.
 */
@SuppressWarnings("unused")
public class Service {

  private final long id;
  @NonNull
  private final String name;
  private final long price;
  private final boolean value;

  private Service(long id, @NonNull String name, long price, boolean value) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.value = value;
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public long getPrice() {
    return price;
  }

  public boolean isValue() {
    return value;
  }

  /**
   * Возвращает новый объект с заданным значением.
   *
   * @param value значение
   */
  @NonNull
  public Service setValue(boolean value) {
    return new Service(id, name, price, value);
  }

  @Override
  public String toString() {
    return "Service{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", price=" + price +
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

    Service service = (Service) o;

    if (id != service.id) {
      return false;
    }
    if (price != service.price) {
      return false;
    }
    if (value != service.value) {
      return false;
    }
    return name.equals(service.name);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + (int) (price ^ (price >>> 32));
    result = 31 * result + (value ? 1 : 0);
    return result;
  }
}
