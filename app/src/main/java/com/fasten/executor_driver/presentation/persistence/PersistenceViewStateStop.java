package com.fasten.executor_driver.presentation.persistence;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние остановленного сервиса.
 */
final class PersistenceViewStateStop implements ViewState<PersistenceViewActions> {

  @Override
  public void apply(@NonNull PersistenceViewActions stateActions) {
    stateActions.stopService();
  }
}
