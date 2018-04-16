package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки сети вида заказа.
 */
final class OfferViewStateNetworkError extends OfferViewState {

  OfferViewStateNetworkError(@Nullable OfferItem offerItem) {
    super(offerItem);
  }

  @Override
  public void apply(@NonNull OfferViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOfferPending(false);
    stateActions.showOfferAvailabilityError(false);
    stateActions.showOfferNetworkErrorMessage(true);
  }
}
