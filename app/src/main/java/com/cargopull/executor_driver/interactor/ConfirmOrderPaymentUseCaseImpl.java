package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class ConfirmOrderPaymentUseCaseImpl implements ConfirmOrderPaymentUseCase {

  @NonNull
  private final ConfirmOrderPaymentGateway confirmOrderPaymentGateway;

  @Inject
  public ConfirmOrderPaymentUseCaseImpl(
      @NonNull ConfirmOrderPaymentGateway confirmOrderPaymentGateway) {
    this.confirmOrderPaymentGateway = confirmOrderPaymentGateway;
  }

  @NonNull
  @Override
  public Completable confirmPayment() {
    return confirmOrderPaymentGateway.confirmOrderPayment().observeOn(Schedulers.single());
  }
}
