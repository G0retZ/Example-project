package com.cargopull.executor_driver.presentation.code;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Начальное состояние ввода кода.
 */
final class CodeViewStateInitial implements ViewState<CodeViewActions> {

  @Override
  public void apply(@NonNull CodeViewActions stateActions) {
    stateActions.enableInputField(true);
    stateActions.showCodeCheckPending(false);
    stateActions.showCodeCheckError(false);
    stateActions.showCodeCheckNetworkErrorMessage(false);
    stateActions.setUnderlineImage(R.drawable.ic_code_input_activated);
  }
}
