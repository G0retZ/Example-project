package com.cargopull.executor_driver.presentation.smsbutton;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки при отправке СМС.
 */
final class SmsButtonViewStateError implements ViewState<FragmentViewActions> {

  @Override
  public void apply(@NonNull FragmentViewActions stateActions) {
    stateActions.setText(R.id.sendSms, R.string.repeat_code_from_sms);
    stateActions.setEnabled(R.id.sendSms, true);
    stateActions.unblockWithPending("sms");
    stateActions.showPersistentDialog(R.string.sms_network_error, null);
  }
}
