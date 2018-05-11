package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.BuildConfig;
import com.fasten.executor_driver.entity.Offer;
import com.fasten.executor_driver.entity.Option;
import com.fasten.executor_driver.entity.OptionBoolean;
import com.fasten.executor_driver.entity.OptionNumeric;
import com.fasten.executor_driver.utils.TimeUtils;
import java.util.Locale;

/**
 * Модель для отображения предложения заказа. Тестируемое форматирование.
 */
class OfferItem {

  @NonNull
  private final Offer offer;
  @NonNull
  private final TimeUtils timeUtils;
  private final long timestamp;

  OfferItem(@NonNull Offer offer, TimeUtils timeUtils) {
    this.offer = offer;
    this.timeUtils = timeUtils;
    timestamp = timeUtils.currentTimeMillis();
  }

  @SuppressWarnings("SpellCheckingInspection")
  @NonNull
  public String getLoadPointMapUrl() {
    return "https://maps.googleapis.com/maps/api/staticmap?"
        + "center="
        + offer.getRoutePoint().getLatitude()
        + ","
        + offer.getRoutePoint().getLongitude()
        + "&zoom=16"
        + "&size=360x304"
        + "&maptype=roadmap"
        + "&key=" + BuildConfig.STATIC_MAP_KEY;
  }

  public String getDistance() {
    return String.format(Locale.getDefault(), "%.2f", offer.getDistance() / 1000d);
  }

  @NonNull
  public String getAddress() {
    return offer.getRoutePoint().getAddress();
  }

  public String getEstimatedPrice() {
    return offer.getEstimatedPrice();
  }

  public String getOfferOptionsRequired() {
    StringBuilder result = new StringBuilder();
    for (Option option : offer.getOptions()) {
      if (option instanceof OptionNumeric) {
        result.append(option.getName()).append(": ").append(option.getValue());
      } else if (option instanceof OptionBoolean) {
        if (!(boolean) option.getValue()) {
          continue;
        }
        result.append(option.getName());
      }
      result.append("\n");
    }
    return result.toString().trim();
  }

  @NonNull
  public String getOfferComment() {
    return offer.getComment();
  }

  @NonNull
  public String getPrice() {
    return offer.getEstimatedPrice();
  }

  public long[] getProgressLeft() {
    long[] res = new long[2];
    res[1] = timeUtils.currentTimeMillis() - timestamp;
    res[1] = offer.getTimeout() * 1000 - res[1];
    res[0] = res[1] / (10L * offer.getTimeout());
    return res;
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
