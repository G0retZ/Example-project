package com.cargopull.executor_driver.presentation.orderconfirmation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.analytics.ErrorReporter;
import com.cargopull.executor_driver.backend.analytics.EventLogger;
import com.cargopull.executor_driver.backend.ringtone.RingTonePlayer;
import com.cargopull.executor_driver.backend.vibro.ShakeItPlayer;
import com.cargopull.executor_driver.entity.ExecutorState;
import com.cargopull.executor_driver.interactor.ExecutorStateUseCase;
import com.cargopull.executor_driver.interactor.OrderConfirmationUseCase;
import com.cargopull.executor_driver.utils.TimeUtils;

public class PreOrderBookingViewModel extends OrderConfirmationViewModelImpl {

  public PreOrderBookingViewModel(
      @NonNull ErrorReporter errorReporter,
      @NonNull ExecutorStateUseCase executorStateUseCase,
      @NonNull OrderConfirmationUseCase orderConfirmationUseCase,
      @NonNull ShakeItPlayer shakeItPlayer,
      @NonNull RingTonePlayer ringTonePlayer,
      @NonNull TimeUtils timeUtils,
      @Nullable EventLogger eventLogger) {
    super(errorReporter, executorStateUseCase, orderConfirmationUseCase, shakeItPlayer,
        ringTonePlayer, timeUtils, eventLogger);
  }

  @Override
  protected boolean isExecutorStateAllowed(ExecutorState executorState) {
    return executorState != ExecutorState.BLOCKED && executorState != ExecutorState.SHIFT_CLOSED;
  }
}
