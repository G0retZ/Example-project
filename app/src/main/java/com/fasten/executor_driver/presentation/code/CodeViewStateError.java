package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.ViewState;

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
    stateActions.showDescriptiveHeader(true);
    stateActions.showInputField(true);
    stateActions.showUnderlineImage(true);
    stateActions.setUnderlineImage(R.drawable.ic_code_input_error);
  }
}
