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
    stateActions.setEnabled(R.id.codeInput, false);
    stateActions.blockWithPending("password");
    stateActions.setBackground(R.id.codeInput, R.drawable.ic_code_input_default);
  }
}
