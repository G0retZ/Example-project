package com.fasten.executor_driver.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;

public class OfferActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_offer);
  }

  @Override
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case ExecutorStateNavigate.OFFER_CONFIRMATION:
        break;
      default:
        super.navigate(destination);
    }
  }
}
