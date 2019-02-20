package com.cargopull.executor_driver.presentation.menu;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class MenuViewStateFilterAvailable implements ViewState<FragmentViewActions> {

  @NonNull
  private final Runnable consumeAction;

  MenuViewStateFilterAvailable(@NonNull Runnable consumeAction) {
    this.consumeAction = consumeAction;
  }

  @Override
  public void apply(@NonNull FragmentViewActions stateActions) {
    stateActions.setEnabled(R.id.filter, true);
    stateActions.setClickAction(R.id.filter, consumeAction);
  }
}
