package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки доступности зака вида заказа.
 */
final class OfferViewStateUnavailableError extends OfferViewState {

  OfferViewStateUnavailableError(@Nullable OfferItem offerItem) {
    super(offerItem);
  }

  @Override
  public void apply(@NonNull OfferViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOfferPending(false);
    stateActions.showOfferAvailabilityError(true);
    stateActions.showOfferNetworkErrorMessage(false);
  }
}
