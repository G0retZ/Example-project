package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Offer;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import javax.inject.Inject;

class OrderConfirmationUseCaseImpl implements OrderConfirmationUseCase {

  @NonNull
  private final OfferGateway offerGateway;
  @Nullable
  private Offer lastOffer;

  @Inject
  OrderConfirmationUseCaseImpl(@NonNull OfferGateway offerGateway) {
    this.offerGateway = offerGateway;
  }

  @Override
  public Flowable<Offer> getOffers() {
    return offerGateway.getOffers().doOnNext(offer -> lastOffer = offer);
  }

  @NonNull
  @Override
  public Completable cancelOrder() {
    if (lastOffer == null) {
      return Completable.error(new NoOffersAvailableException());
    }
    return offerGateway.sendDecision(lastOffer, false);
  }
}
