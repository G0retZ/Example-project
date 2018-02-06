package com.fasten.executor_driver.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.backend.web.TokenKeeper;
import javax.inject.Inject;

public class MapActivity extends BaseActivity {

  private TokenKeeper tokenKeeper;

  @Inject
  public void setTokenKeeper(TokenKeeper tokenKeeper) {
    this.tokenKeeper = tokenKeeper;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getDiComponent().inject(this);
    if (tokenKeeper.getToken() == null) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
    } else {
      setContentView(R.layout.activity_map);
    }
  }
}
