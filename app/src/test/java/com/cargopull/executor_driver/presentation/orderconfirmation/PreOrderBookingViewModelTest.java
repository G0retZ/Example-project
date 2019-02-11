package com.cargopull.executor_driver.presentation.orderconfirmation;

import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.utils.TimeUtils;

public class PreOrderBookingViewModelTest extends OrderConfirmationViewModelTest {

  public PreOrderBookingViewModelTest(ExecutorState condition) {
    super(condition);
  }

  @Override
  protected boolean getPrimeCondition(ExecutorState executorState) {
    return executorState != ExecutorState.BLOCKED && executorState != ExecutorState.SHIFT_CLOSED;
  }

  @Override
  public OrderConfirmationViewModel getViewModel(ErrorReporter errorReporter,
      ExecutorStateUseCase sUseCase, OrderConfirmationUseCase useCase, TimeUtils timeUtils,
      EventLogger eventLogger) {
    return new PreOrderBookingViewModel(errorReporter, sUseCase, useCase, timeUtils, eventLogger);
  }
}