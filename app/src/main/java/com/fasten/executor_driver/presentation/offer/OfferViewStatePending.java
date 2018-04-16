package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class OfferViewStatePending extends OfferViewState {

  OfferViewStatePending(@Nullable OfferItem offerItem) {
    super(offerItem);
  }

  @Override
  public void apply(@NonNull OfferViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOfferPending(true);
    stateActions.showOfferAvailabilityError(false);
    stateActions.showOfferNetworkErrorMessage(false);
  }
}
