package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.ViewState;

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
    stateActions.showDescriptiveHeader(true);
    stateActions.showInputField(true);
    stateActions.showUnderlineImage(true);
    stateActions.setUnderlineImage(R.drawable.ic_code_input_default);
  }
}
