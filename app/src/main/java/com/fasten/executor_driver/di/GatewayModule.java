package com.fasten.executor_driver.di;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.TokenKeeper;
import com.fasten.executor_driver.gateway.LoginGatewayImpl;
import com.fasten.executor_driver.gateway.PasswordGatewayImpl;
import com.fasten.executor_driver.gateway.PhoneCallGatewayImpl;
import com.fasten.executor_driver.gateway.SmsGatewayImpl;
import com.fasten.executor_driver.gateway.TokenKeeperImpl;
import com.fasten.executor_driver.interactor.auth.LoginGateway;
import com.fasten.executor_driver.interactor.auth.PasswordGateway;
import com.fasten.executor_driver.interactor.auth.PhoneCallGateway;
import com.fasten.executor_driver.interactor.auth.SmsGateway;
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
  LoginGateway provideLoginGateway(LoginGatewayImpl loginGateway) {
    return loginGateway;
  }

  @Provides
  PasswordGateway providePasswordGateway(PasswordGatewayImpl passwordGateway) {
    return passwordGateway;
  }

  @Provides
  PhoneCallGateway providePhoneCallGateway(PhoneCallGatewayImpl phoneCallGateway) {
    return phoneCallGateway;
  }

  @Provides
  SmsGateway provideSmsGateway(SmsGatewayImpl smsGateway) {
    return smsGateway;
  }
}
