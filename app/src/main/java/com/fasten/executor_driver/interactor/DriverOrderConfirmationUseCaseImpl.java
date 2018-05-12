package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.entity.NoOffersAvailableException;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import javax.inject.Inject;

public class DriverOrderConfirmationUseCaseImpl implements DriverOrderConfirmationUseCase {

  @NonNull
  private final OrderGateway orderGateway;
  @Nullable
  private Order lastOrder;

  @Inject
  public DriverOrderConfirmationUseCaseImpl(@NonNull OrderGateway orderGateway) {
    this.orderGateway = orderGateway;
  }

  @Override
  public Flowable<Order> getOffers() {
    return orderGateway.getOffers().doOnNext(offer -> lastOrder = offer);
  }

  @NonNull
  @Override
  public Completable sendDecision(boolean confirmed) {
    if (lastOrder == null) {
      return Completable.error(new NoOffersAvailableException());
    }
    return orderGateway.sendDecision(lastOrder, confirmed);
  }
}
