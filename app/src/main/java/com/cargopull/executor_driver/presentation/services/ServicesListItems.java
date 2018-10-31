package com.cargopull.executor_driver.presentation.services;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Модель для фильтрации списка услуг исполнителя по ползунку.
 */
public class ServicesListItems {

  @NonNull
  private final List<ServicesListItem> servicesListItems = new ArrayList<>();
  private int minPrice = Integer.MAX_VALUE;
  private int maxPrice = 0;
  private int minSelectedPrice = Integer.MAX_VALUE;

  @Inject
  public ServicesListItems() {
  }

  /**
   * Выдает список элементов услуг учитывая текущее положение ползунка.
   *
   * @return - список элементов услуг.
   */
  @NonNull
  public List<ServicesListItem> getServicesListItems() {
    return getServicesListItems(
        (int) ((minSelectedPrice - minPrice) * 100f / (maxPrice - minPrice)));
  }

  /**
   * Задаем список элементов услуг.
   *
   * @param servicesListItems - список элементов услуг
   */
  public void setServicesListItems(@NonNull List<ServicesListItem> servicesListItems) {
    minPrice = Integer.MAX_VALUE;
    maxPrice = 0;
    minSelectedPrice = Integer.MAX_VALUE;
    this.servicesListItems.clear();
    this.servicesListItems.addAll(servicesListItems);
    for (ServicesListItem servicesListItem : servicesListItems) {
      minPrice =
          minPrice > servicesListItem.getPriceValue() ? servicesListItem.getPriceValue() : minPrice;
      minSelectedPrice =
          minSelectedPrice >= servicesListItem.getPriceValue() && servicesListItem.isChecked()
              ? servicesListItem.getPriceValue() : minSelectedPrice;
      maxPrice =
          maxPrice < servicesListItem.getPriceValue() ? servicesListItem.getPriceValue() : maxPrice;
    }
    if (minSelectedPrice == Integer.MAX_VALUE) {
      minSelectedPrice = maxPrice;
    }
  }

  /**
   * Выдает список элементов услуг учитывая заданное новое положение ползунка.
   *
   * @param position - новое положение ползунка.
   * @return - список элементов услуг.
   */
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
    if (result.size() == 1) {
      result.get(0).setChecked(true);
    }
    return result;
  }

  public int getMaxPrice() {
    return maxPrice;
  }

  public int getMinPrice() {
    return minSelectedPrice;
  }

  /**
   * текущее положение ползунка.
   */
  @IntRange(from = 0, to = 100)
  public int getCurrentPosition() {
    return (int) ((minSelectedPrice - minPrice) * 100f / (maxPrice - minPrice));
  }
}
