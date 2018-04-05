package com.fasten.executor_driver.presentation.services;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Service;
import java.util.Locale;

/**
 * Модель для отображения услуги в списке выбора услуг исполнителя. Тестируемое форматирование.
 */
public class ServicesListItem {

  @NonNull
  private Service service;

  public ServicesListItem(@NonNull Service service) {
    this.service = service;
  }

  @NonNull
  public Service getService() {
    return service;
  }

  @NonNull
  public String getName() {
    return service.getName();
  }

  @NonNull
  public String getPrice() {
    return String.format(Locale.getDefault(), "от %d рублей за первый час", service.getPrice());
  }

  public int getPriceValue() {
    return service.getPrice();
  }

  public boolean isChecked() {
    return service.isSelected();
  }

  public void setChecked(boolean checked) {
    service = service.setSelected(checked);
  }

  @Override
  public String toString() {
    return "ServicesListItem{" +
        "service=" + service +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ServicesListItem that = (ServicesListItem) o;

    return service.equals(that.service);
  }

  @Override
  public int hashCode() {
    return service.hashCode();
  }
}
