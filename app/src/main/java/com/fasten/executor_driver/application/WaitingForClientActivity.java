package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.calltoclient.CallToClientNavigate;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientNavigate;
import com.fasten.executor_driver.view.CallToClientFragment;

public class WaitingForClientActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_waiting_for_client);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case WaitingForClientNavigate.CALL_TO_CLIENT:
        getSupportFragmentManager().beginTransaction()
            .add(R.id.callingMessage, new CallToClientFragment(), "call").commit();
        break;
      case CallToClientNavigate.FINISHED:
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("call");
        if (fragment != null) {
          getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        break;
      default:
        super.navigate(destination);
    }
  }
}
