package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.Offer;
import java.util.Locale;

/**
 * Модель для отображения предложения заказа. Тестируемое форматирование.
 */
public class OfferItem {

  @NonNull
  private final Offer offer;

  OfferItem(@NonNull Offer offer) {
    this.offer = offer;
  }

  @SuppressWarnings("SpellCheckingInspection")
  @NonNull
  public String getLoadPointMapUrl() {
    return "https://maps.googleapis.com/maps/api/staticmap?"
        + "center="
          + offer.getRoutePoint().getLongitude()
          + ","
          + offer.getRoutePoint().getLatitude()
        + "&zoom=16"
        + "&size=288x352"
        + "&maptype=roadmap"
        + "&key=" + BuildConfig.STATIC_MAP_KEY;
  }

  public long getDistance() {
    return offer.getDistance();
  }

  @NonNull
  public String getAddress() {
    return offer.getRoutePoint().getAddress();
  }

  public long getPortersCount() {
    return offer.getPorters();
  }

  public long getPassengersCount() {
    return offer.getPassengers();
  }

  @NonNull
  public String getOfferComment() {
    return offer.getComment();
  }

  @NonNull
  public String getPrice() {
    return String.format(Locale.getDefault(), "от %d рублей", offer.getEstimatedPrice());
  }

  @Override
  public String toString() {
    return "OfferItem{" +
        "offer=" + offer +
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

    OfferItem offerItem = (OfferItem) o;

    return offer.equals(offerItem.offer);
  }

  @Override
  public int hashCode() {
    return offer.hashCode();
  }
}
