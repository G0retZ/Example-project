package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Общее состояние вида заказа.
 */
class OfferViewState implements ViewState<OfferViewActions> {

  @Nullable
  private final OfferItem offerItem;

  OfferViewState(@Nullable OfferItem offerItem) {
    this.offerItem = offerItem;
  }

  @Override
  @CallSuper
  public void apply(@NonNull OfferViewActions stateActions) {
    if (offerItem == null) {
      return;
    }
    stateActions.showLoadPoint(offerItem.getLoadPointMapUrl());
    stateActions.showDistance(offerItem.getDistance());
    stateActions.showLoadPointAddress(offerItem.getAddress());
    stateActions.showOfferOptionsRequirements(offerItem.getOfferOptionsRequired());
    stateActions.showEstimatedPrice(offerItem.getEstimatedPrice());
    stateActions.showOfferComment(offerItem.getOfferComment());
    long timeout[] = offerItem.getProgressLeft();
    stateActions.showTimeout((int) timeout[0], timeout[1]);
  }

  @Override
  public String toString() {
    return "OfferViewState{" +
        "offerItem=" + offerItem +
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

    OfferViewState that = (OfferViewState) o;

    return offerItem != null ? offerItem.equals(that.offerItem) : that.offerItem == null;
  }

  @Override
  public int hashCode() {
    return offerItem != null ? offerItem.hashCode() : 0;
  }
}
