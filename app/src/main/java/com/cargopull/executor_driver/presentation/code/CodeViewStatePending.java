package com.cargopull.executor_driver.presentation.code;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидание при проверке введенного кода.
 */
final class CodeViewStatePending implements ViewState<CodeViewActions> {

  @Override
  public void apply(@NonNull CodeViewActions stateActions) {
    stateActions.enableInputField(false);
    stateActions.showCodeCheckPending(true);
    stateActions.showCodeCheckError(false);
    stateActions.showCodeCheckNetworkErrorMessage(false);
    stateActions.setUnderlineImage(R.drawable.ic_code_input_default);
  }
}
