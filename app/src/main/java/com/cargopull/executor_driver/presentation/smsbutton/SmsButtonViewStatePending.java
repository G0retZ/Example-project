package com.cargopull.executor_driver.presentation.smsbutton;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания во время запроса отправки СМС.
 */
public final class SmsButtonViewStatePending implements ViewState<FragmentViewActions> {

  @Override
  public void apply(@NonNull FragmentViewActions stateActions) {
    stateActions.setEnabled(R.id.sendSms, false);
    stateActions.setText(R.id.sendSms, R.string.repeat_code_from_sms);
    stateActions.blockWithPending("sms");
  }
}
