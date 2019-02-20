package com.cargopull.executor_driver.presentation.codeheader;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.TextViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Общее состояние ввода кода.
 */
final class CodeHeaderViewState implements ViewState<TextViewActions> {

  @NonNull
  private final String phoneNumber;

  CodeHeaderViewState(@NonNull String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  @CallSuper
  public void apply(@NonNull TextViewActions stateActions) {
    stateActions.setText(R.id.codeHeaderPhone, phoneNumber);
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
