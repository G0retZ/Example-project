package com.cargopull.executor_driver.presentation.smsbutton;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Начальное состояние готовой кнопки.
 */
public final class SmsButtonViewStateReady implements ViewState<FragmentViewActions> {

  @Override
  public void apply(@NonNull FragmentViewActions stateActions) {
    stateActions.setEnabled(R.id.sendSms, true);
    stateActions.setText(R.id.sendSms, R.string.repeat_code_from_sms);
    stateActions.unblockWithPending("sms");
  }
}
