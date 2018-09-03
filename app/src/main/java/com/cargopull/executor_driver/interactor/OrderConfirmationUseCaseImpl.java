package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

public class OrderConfirmationUseCaseImpl implements OrderConfirmationUseCase {

  @NonNull
  private final OrderUseCase orderUseCase;
  @NonNull
  private final OrderConfirmationGateway orderConfirmationGateway;
  @NonNull
  private final Consumer<Order> acceptOrderConsumer;

  @Inject
  public OrderConfirmationUseCaseImpl(@NonNull OrderUseCase orderUseCase,
      @NonNull OrderConfirmationGateway orderConfirmationGateway,
      @NonNull Consumer<Order> acceptOrderConsumer) {
    this.orderUseCase = orderUseCase;
    this.orderConfirmationGateway = orderConfirmationGateway;
    this.acceptOrderConsumer = acceptOrderConsumer;
  }

  @NonNull
  @Override
  public Single<String> sendDecision(boolean confirmed) {
    return orderUseCase.getOrders()
        .observeOn(Schedulers.single())
        .flatMapSingle(new Function<Order, SingleSource<? extends String>>() {
          boolean orderDecisionMade;

          @Override
          public SingleSource<? extends String> apply(Order order) throws Exception {
            if (orderDecisionMade) {
              throw new OrderOfferDecisionException();
            }
            orderDecisionMade = true;
            return orderConfirmationGateway.sendDecision(order, confirmed)
                .observeOn(Schedulers.single())
                .doOnSuccess(str -> {
                  if (confirmed) {
                    acceptOrderConsumer.accept(order);
                  }
                });
          }
        })
        .firstOrError()
        .doOnSuccess(s -> orderUseCase.setOrderOfferDecisionMade());
  }
}
