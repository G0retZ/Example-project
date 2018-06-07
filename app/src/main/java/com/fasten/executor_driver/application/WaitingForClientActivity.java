package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.calltoclient.CallToClientNavigate;
import com.fasten.executor_driver.presentation.waitingforclient.WaitingForClientNavigate;
import com.fasten.executor_driver.view.CallToClientFragment;
import com.fasten.executor_driver.view.CancelOrderDialogFragment;

public class WaitingForClientActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_waiting_for_client);
    findViewById(R.id.cancelOrder).setOnClickListener(v -> {
      if (getSupportFragmentManager().findFragmentByTag("cancelOrder") == null) {
        new CancelOrderDialogFragment().show(getSupportFragmentManager(), "cancelOrder");
      }
      v.setEnabled(false);
      v.postDelayed(() -> v.setEnabled(true), 10_000);
    });
  }

  @Override
  public void navigate(@NonNull String destination) {
    Fragment fragment;
    switch (destination) {
      case WaitingForClientNavigate.CALL_TO_CLIENT:
        fragment = getSupportFragmentManager().findFragmentByTag("callToClient");
        if (fragment == null) {
          getSupportFragmentManager().beginTransaction()
              .add(R.id.callingMessage, new CallToClientFragment(), "callToClient").commit();
        }
        break;
      case CallToClientNavigate.FINISHED:
        fragment = getSupportFragmentManager().findFragmentByTag("callToClient");
        if (fragment != null) {
          getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        break;
      default:
        super.navigate(destination);
    }
  }
}
