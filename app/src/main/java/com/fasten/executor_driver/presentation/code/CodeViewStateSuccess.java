package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * Финальное состояние при вводе кода, которое впустит пользователя.
 */
final class CodeViewStateSuccess implements ViewState<CodeViewActions> {

  @Override
  public void apply(@NonNull CodeViewActions stateActions) {
    stateActions.showPending(false);
    stateActions.showError(null);
    stateActions.letIn();
  }
}
