package com.fasten.executor_driver.di;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.TokenKeeper;
import com.fasten.executor_driver.gateway.HeatMapGatewayImpl;
import com.fasten.executor_driver.gateway.PasswordGatewayImpl;
import com.fasten.executor_driver.gateway.SmsGatewayImpl;
import com.fasten.executor_driver.gateway.TokenKeeperImpl;
import com.fasten.executor_driver.interactor.auth.PasswordGateway;
import com.fasten.executor_driver.interactor.auth.SmsGateway;
import com.fasten.executor_driver.interactor.map.HeatMapGateway;
import dagger.Module;
import dagger.Provides;

@Module
class GatewayModule {

  @Provides
  @NonNull
  TokenKeeper provideTokenKeeper(@NonNull TokenKeeperImpl tokenKeeper) {
    return tokenKeeper;
  }

  @Provides
  PasswordGateway providePasswordGateway(PasswordGatewayImpl passwordGateway) {
    return passwordGateway;
  }

  @Provides
  SmsGateway provideSmsGateway(SmsGatewayImpl smsGateway) {
    return smsGateway;
  }

  @Provides
  HeatMapGateway provideHeatMapGateway(HeatMapGatewayImpl smsGateway) {
    return smsGateway;
  }
}
