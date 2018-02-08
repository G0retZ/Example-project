package com.fasten.executor_driver.di;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.DataSharer;
import com.fasten.executor_driver.interactor.auth.LoginSharer;
import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.interactor.auth.LoginUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.PasswordUseCase;
import com.fasten.executor_driver.interactor.auth.PasswordUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.SmsUseCase;
import com.fasten.executor_driver.interactor.auth.SmsUseCaseImpl;
import com.fasten.executor_driver.interactor.map.HeatMapUseCase;
import com.fasten.executor_driver.interactor.map.HeatMapUseCaseImpl;
import com.fasten.executor_driver.interactor.online.OnlineUseCase;
import com.fasten.executor_driver.interactor.online.OnlineUseCaseImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module
class UseCaseModule {

  @Provides
  LoginUseCase provideLoginUseCase(LoginUseCaseImpl loginUseCase) {
    return loginUseCase;
  }

  @Provides
  PasswordUseCase providePasswordUseCase(PasswordUseCaseImpl passwordUseCase) {
    return passwordUseCase;
  }

  @Provides
  SmsUseCase provideSmsUseCase(SmsUseCaseImpl smsUseCase) {
    return smsUseCase;
  }

  @Provides
  HeatMapUseCase provideHeatMapUseCase(HeatMapUseCaseImpl heatMapUseCase) {
    return heatMapUseCase;
  }

  @Provides
  OnlineUseCase provideOnlineUseCase(OnlineUseCaseImpl onlineUseCase) {
    return onlineUseCase;
  }

  /* Обмен данными между юзкейсами */

  @Provides
  @Named("loginSharer")
  DataSharer<String> provideLoginSharer(@NonNull LoginSharer loginSharer) {
    return loginSharer;
  }
}
