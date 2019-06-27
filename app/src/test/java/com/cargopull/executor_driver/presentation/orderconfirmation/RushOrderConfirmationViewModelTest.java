package com.cargopull.executor_driver.presentation.orderconfirmation;

import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.utils.TimeUtils;

public class RushOrderConfirmationViewModelTest extends OrderConfirmationViewModelTest {

  public RushOrderConfirmationViewModelTest(ExecutorState conditions) {
    super(conditions);
  }

  @Override
  protected boolean getPrimeCondition(ExecutorState executorState) {
    return executorState == ExecutorState.DRIVER_ORDER_CONFIRMATION;
  }

  @Override
  public OrderConfirmationViewModel getViewModel(ErrorReporter errorReporter,
      ExecutorStateUseCase sUseCase, OrderConfirmationUseCase useCase, ShakeItPlayer shakeItPlayer,
      RingTonePlayer ringTonePlayer, TimeUtils timeUtils, EventLogger eventLogger) {
    return new RushOrderConfirmationViewModel(errorReporter, sUseCase, useCase, shakeItPlayer, ringTonePlayer, timeUtils, eventLogger);
  }
}