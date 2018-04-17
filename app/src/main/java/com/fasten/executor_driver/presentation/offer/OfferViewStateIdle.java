package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние бездействия вида заказа.
 */
final class OfferViewStateIdle extends OfferViewState {

  OfferViewStateIdle(@Nullable OfferItem offerItem) {
    super(offerItem);
  }

  @Override
  public void apply(@NonNull OfferViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showOfferPending(false);
    stateActions.enableAcceptButton(true);
    stateActions.enableDeclineButton(true);
    stateActions.showOfferAvailabilityError(false);
    stateActions.showOfferNetworkErrorMessage(false);
  }
}
