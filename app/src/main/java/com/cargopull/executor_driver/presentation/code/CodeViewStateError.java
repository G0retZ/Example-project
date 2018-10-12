package com.cargopull.executor_driver.presentation.code;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при вводе кода.
 */
final class CodeViewStateError implements ViewState<CodeViewActions> {

  @Override
  public void apply(@NonNull CodeViewActions stateActions) {
    stateActions.enableInputField(true);
    stateActions.showCodeCheckPending(false);
    stateActions.showCodeCheckError(true);
    stateActions.showCodeCheckNetworkErrorMessage(false);
    stateActions.setUnderlineImage(R.drawable.ic_code_input_error);
  }
}
