package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.stomp.StompFrame;
import com.cargopull.executor_driver.entity.ExecutorState;
import io.reactivex.functions.Predicate;
import java.util.Arrays;
import java.util.List;

public class OrderFilter implements Predicate<StompFrame> {

  @NonNull
  private final List<String> allowedStates = Arrays.asList(
      ExecutorState.DRIVER_ORDER_CONFIRMATION.toString(),
      ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION.toString(),
      ExecutorState.CLIENT_ORDER_CONFIRMATION.toString(),
      ExecutorState.MOVING_TO_CLIENT.toString(),
      ExecutorState.WAITING_FOR_CLIENT.toString(),
      ExecutorState.ORDER_FULFILLMENT.toString(),
      ExecutorState.PAYMENT_CONFIRMATION.toString()
  );

  @Override
  public boolean test(StompFrame stompFrame) {
    return allowedStates.contains(stompFrame.getHeaders().get("Status"));
  }
}
