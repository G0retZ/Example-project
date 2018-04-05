package com.fasten.executor_driver.entity;

import android.support.annotation.NonNull;

/**
 * Неизменная бизнес сущность услуги. Содержит в себе ID, имя, цену и выбор пользователя.
 */
public class Service {

  private final long id;
  @NonNull
  private final String name;
  private final int price;
  private final boolean selected;

  public Service(long id, @NonNull String name, int price, boolean selected) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.selected = selected;
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  public int getPrice() {
    return price;
  }

  public boolean isSelected() {
    return selected;
  }

  public Service setSelected(boolean selected) {
    return new Service(id, name, price, selected);
  }

  @Override
  public String toString() {
    return "Service{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", price=" + price +
        ", selected=" + selected +
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
    if (selected != service.selected) {
      return false;
    }
    return name.equals(service.name);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + price;
    result = 31 * result + (selected ? 1 : 0);
    return result;
  }
}
