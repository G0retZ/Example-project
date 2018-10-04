package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cargopull.executor_driver.entity.Order;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.utils.Pair;
import io.reactivex.Flowable;
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
  @Nullable
  private final OrderDecisionUseCase orderDecisionUseCase;
  @Nullable
  private final OrdersUseCase ordersUseCase;

  @Inject
  public OrderConfirmationUseCaseImpl(
      @NonNull OrderUseCase orderUseCase,
      @NonNull OrderConfirmationGateway orderConfirmationGateway,
      @Nullable OrderDecisionUseCase orderDecisionUseCase,
      @Nullable OrdersUseCase ordersUseCase) {
    this.orderUseCase = orderUseCase;
    this.orderConfirmationGateway = orderConfirmationGateway;
    this.orderDecisionUseCase = orderDecisionUseCase;
    this.ordersUseCase = ordersUseCase;
  }

  @NonNull
  @Override
  public Flowable<Pair<Long, Long>> getOrderDecisionTimeout() {
    return orderUseCase.getOrders()
        .map(order -> new Pair<>(order.getId(), order.getTimeout()));
  }

  @NonNull
  @Override
  public Single<String> sendDecision(boolean confirmed) {
    return orderUseCase.getOrders()
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
                .doAfterSuccess(str -> {
                  if (ordersUseCase != null) {
                    if (confirmed) {
                      ordersUseCase.addOrder(order);
                    } else {
                      ordersUseCase.removeOrder(order);
                    }
                  }
                });
          }
        })
        .firstOrError()
        .doOnSuccess(s -> {
          if (orderDecisionUseCase != null) {
            orderDecisionUseCase.setOrderOfferDecisionMade();
          }
        });
  }
}
