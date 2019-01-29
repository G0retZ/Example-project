package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.utils.TimeUtils;

public class PreOrderConfirmationViewModel extends OrderConfirmationViewModelImpl {

  public PreOrderConfirmationViewModel(
      @NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull OrderConfirmationUseCase orderConfirmationUseCase,
      @NonNull TimeUtils timeUtils,
      @Nullable EventLogger eventLogger) {
    super(errorReporter, executorStateUseCase, orderConfirmationUseCase, timeUtils, eventLogger);
  }

  @Override
  protected boolean isExecutorStateAllowed(ExecutorState executorState) {
    return executorState == ExecutorState.DRIVER_PRELIMINARY_ORDER_CONFIRMATION;
  }
}
