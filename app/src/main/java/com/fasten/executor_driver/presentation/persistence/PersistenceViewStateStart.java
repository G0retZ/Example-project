package com.fasten.executor_driver.presentation.persistence;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние запущенного сервиса.
 */
final class PersistenceViewStateStart implements ViewState<PersistenceViewActions> {

  @StringRes
  private final int titleMessage;
  @StringRes
  private final int textMessage;

  PersistenceViewStateStart(int titleMessage, int textMessage) {
    this.titleMessage = titleMessage;
    this.textMessage = textMessage;
  }


  @Override
  public void apply(@NonNull PersistenceViewActions stateActions) {
    stateActions.startService(titleMessage, textMessage);
  }

  @Override
  public String toString() {
    return "PersistenceViewStateStart{" +
        "titleMessage=" + titleMessage +
        ", textMessage=" + textMessage +
        '}';
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PersistenceViewStateStart that = (PersistenceViewStateStart) o;

    if (titleMessage != that.titleMessage) {
      return false;
    }
    return textMessage == that.textMessage;
  }

  @Override
  public int hashCode() {
    int result = titleMessage;
    result = 31 * result + textMessage;
    return result;
  }
}
