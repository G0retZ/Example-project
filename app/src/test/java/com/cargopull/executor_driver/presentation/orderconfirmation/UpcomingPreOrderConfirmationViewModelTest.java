package com.cargopull.executor_driver.presentation.orderconfirmation;

import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.utils.TimeUtils;

public class UpcomingPreOrderConfirmationViewModelTest extends OrderConfirmationViewModelTest {

  public UpcomingPreOrderConfirmationViewModelTest(ExecutorState conditions) {
    super(conditions);
  }

  @Override
  protected boolean getPrimeCondition(ExecutorState executorState) {
    return executorState == ExecutorState.ONLINE || executorState == ExecutorState.SHIFT_OPENED;
  }

  @Override
  public OrderConfirmationViewModel getViewModel(ErrorReporter errorReporter,
      ExecutorStateUseCase sUseCase, OrderConfirmationUseCase useCase, TimeUtils timeUtils,
      EventLogger eventLogger) {
    return new UpcomingPreOrderConfirmationViewModel(errorReporter, sUseCase, useCase, timeUtils,
        eventLogger);
  }
}