package com.cargopull.executor_driver.presentation.code;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Начальное состояние ввода кода.
 */
final class CodeViewStateActive implements ViewState<CodeViewActions> {

  @Override
  public void apply(@NonNull CodeViewActions stateActions) {
    stateActions.setEnabled(R.id.codeInput, true);
    stateActions.unblockWithPending("password");
    stateActions.setBackground(R.id.codeInput, R.drawable.ic_code_input_activated);
  }
}
