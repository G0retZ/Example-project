package com.cargopull.executor_driver.presentation.codeheader;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Общее состояние ввода кода.
 */
final class CodeHeaderViewState implements ViewState<CodeHeaderViewActions> {

  @NonNull
  private final String phoneNumber;

  CodeHeaderViewState(@NonNull String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  @CallSuper
  public void apply(@NonNull CodeHeaderViewActions stateActions) {
    stateActions.setDescriptiveHeaderText(R.string.sms_code_message, phoneNumber);
  }

  @Override
  public String toString() {
    return "CodeHeaderViewState{" +
        "phoneNumber='" + phoneNumber + '\'' +
        '}';
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CodeHeaderViewState that = (CodeHeaderViewState) o;

    return phoneNumber.equals(that.phoneNumber);
  }

  @Override
  public int hashCode() {
    return phoneNumber.hashCode();
  }
}
