package com.cargopull.executor_driver.application;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.R;

public class SplashScreenActivity extends BaseActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    TextView textView = findViewById(R.id.versionText);
    textView.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
  }
}
