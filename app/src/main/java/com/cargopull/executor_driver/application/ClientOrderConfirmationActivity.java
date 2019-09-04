package com.cargopull.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.R;

public class ClientOrderConfirmationActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client_order_confirmation);

    OnClickListener clickListener = v -> startActivity(new Intent(this, MenuActivity.class));
    findViewById(R.id.menuButtonDesc).setOnClickListener(clickListener);
    findViewById(R.id.menuButton).setOnClickListener(clickListener);
  }
}
