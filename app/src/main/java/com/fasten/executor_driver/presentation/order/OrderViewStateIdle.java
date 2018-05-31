package com.fasten.executor_driver.presentation.order;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class OrderViewStateIdle implements ViewState<OrderViewActions> {

  @NonNull
  private final OrderItem orderItem;

  OrderViewStateIdle(@NonNull OrderItem orderItem) {
    this.orderItem = orderItem;
  }

  @Override
  public void apply(@NonNull OrderViewActions stateActions) {
    stateActions.showLoadPoint(orderItem.getLoadPointMapUrl());
    stateActions.showLoadPointCoordinates(orderItem.getCoordinatesString());
    stateActions.showDistance(orderItem.getDistance());
    stateActions.showLoadPointAddress(orderItem.getAddress());
    stateActions.showTimeout(orderItem.getSecondsToMeetClient());
    stateActions.showComment(orderItem.getOrderComment());
    stateActions.showEstimatedPrice(orderItem.getEstimatedPrice());
    stateActions.showOrderOptionsRequirements(orderItem.getOrderOptionsRequired());
    stateActions.showOrderPending(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
    long timeout[] = orderItem.getProgressLeft();
    stateActions.showTimeout((int) timeout[0], timeout[1]);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderViewStateIdle that = (OrderViewStateIdle) o;

    return orderItem.equals(that.orderItem);
  }

  @Override
  public int hashCode() {
    return orderItem.hashCode();
  }
}
