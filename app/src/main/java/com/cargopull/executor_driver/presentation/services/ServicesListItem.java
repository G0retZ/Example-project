package com.cargopull.executor_driver.presentation.services;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.Service;
import java.text.DecimalFormat;
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
  public String getPrice(Resources resources) {
    int currentCost = service.getPrice();
    if (!resources.getBoolean(R.bool.show_cents)) {
      currentCost = Math.round(currentCost / 100f);
    }
    DecimalFormat decimalFormat = new DecimalFormat(resources.getString(R.string.currency_format));
    decimalFormat.setMaximumFractionDigits(0);
    return String.format(Locale.getDefault(), "от %s за первый час",
        decimalFormat.format(currentCost));
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
