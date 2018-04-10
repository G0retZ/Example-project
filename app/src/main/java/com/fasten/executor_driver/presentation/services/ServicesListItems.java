package com.fasten.executor_driver.presentation.services;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель для фильтрации списка услуг исполнителя по ползунку.
 */
public class ServicesListItems {

  @NonNull
  private final List<ServicesListItem> servicesListItems = new ArrayList<>();
  private int minPrice = Integer.MAX_VALUE;
  private int maxPrice = 0;
  private int minSelectedPrice = Integer.MAX_VALUE;

  @IntRange(from = 0, to = 100)
  public int setServicesListItems(@NonNull List<ServicesListItem> servicesListItems) {
    this.servicesListItems.clear();
    this.servicesListItems.addAll(servicesListItems);
    for (ServicesListItem servicesListItem : servicesListItems) {
      minPrice =
          minPrice > servicesListItem.getPriceValue() ? servicesListItem.getPriceValue() : minPrice;
      minSelectedPrice =
          minSelectedPrice > servicesListItem.getPriceValue() && servicesListItem.isChecked()
              ? servicesListItem.getPriceValue() : minSelectedPrice;
      maxPrice =
          maxPrice < servicesListItem.getPriceValue() ? servicesListItem.getPriceValue() : maxPrice;
    }
    return (int) ((minSelectedPrice - minPrice) * 100f / (maxPrice - minPrice));
  }

  @NonNull
  public List<ServicesListItem> getServicesListItems(@IntRange(from = 0, to = 100) int position) {
    int newMinSelectedPrice = minPrice + (int) ((maxPrice - minPrice) * position / 100f);
    List<ServicesListItem> result = new ArrayList<>();
    for (ServicesListItem servicesListItem : servicesListItems) {
      if (servicesListItem.getPriceValue() >= newMinSelectedPrice) {
        if (servicesListItem.getPriceValue() < minSelectedPrice) {
          servicesListItem.setChecked(true);
        }
        result.add(servicesListItem);
      }
    }
    minSelectedPrice = newMinSelectedPrice;
    return result;
  }
}
