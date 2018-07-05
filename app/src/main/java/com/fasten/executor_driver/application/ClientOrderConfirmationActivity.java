package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;

public class ClientOrderConfirmationActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client_order_confirmation);
    findViewById(R.id.appBar).setOnClickListener(
        v -> startActivity(new Intent(this, MenuActivity.class))
    );
  }
}
