package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.PreOrderExpiredException;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderConfirmationUseCaseImpl implements OrderConfirmationUseCase {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final OrderConfirmationGateway orderConfirmationGateway;

  @Inject
  public OrderConfirmationUseCaseImpl(@NonNull OrderUseCase orderUseCase,
      @NonNull OrderConfirmationGateway orderConfirmationGateway) {
    this.orderUseCase = orderUseCase;
    this.orderConfirmationGateway = orderConfirmationGateway;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(boolean confirmed) {
    return orderUseCase.getOrders()
        .observeOn(Schedulers.single())
        .flatMapSingle(new Function<Order, SingleSource<? extends String>>() {
          boolean orderExpired;

          @Override
          public SingleSource<? extends String> apply(Order order) throws Exception {
            if (orderExpired) {
              throw new PreOrderExpiredException();
            }
            orderExpired = true;
            return orderConfirmationGateway.sendDecision(order, confirmed);
          }
        }).observeOn(Schedulers.single())
        .firstOrError()
        .doOnSuccess(s -> orderUseCase.setOrderExpired());
  }
}
