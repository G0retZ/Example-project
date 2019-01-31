package com.cargopull.executor_driver.presentation.code;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при вводе кода.
 */
final class CodeViewStateError implements ViewState<FragmentViewActions> {

  @Override
  public void apply(@NonNull FragmentViewActions stateActions) {
    stateActions.setEnabled(R.id.codeInput, true);
    stateActions.unblockWithPending("password");
    stateActions.setBackground(R.id.codeInput, R.drawable.ic_code_input_error);
  }
}
