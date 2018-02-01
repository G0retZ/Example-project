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

  /* Обмен данными между юзкейсами */

  @Provides
  @Named("loginSharer")
  DataSharer<String> provideLoginSharer(@NonNull LoginSharer loginSharer) {
    return loginSharer;
  }
}
