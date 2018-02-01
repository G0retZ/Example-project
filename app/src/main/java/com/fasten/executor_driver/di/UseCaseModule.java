package com.fasten.executor_driver.di;

import com.fasten.executor_driver.interactor.auth.LoginUseCase;
import com.fasten.executor_driver.interactor.auth.LoginUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.PasswordUseCase;
import com.fasten.executor_driver.interactor.auth.PasswordUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.PhoneCallUseCase;
import com.fasten.executor_driver.interactor.auth.PhoneCallUseCaseImpl;
import com.fasten.executor_driver.interactor.auth.SmsUseCase;
import com.fasten.executor_driver.interactor.auth.SmsUseCaseImpl;
import dagger.Module;
import dagger.Provides;

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
  PhoneCallUseCase providePhoneCallUseCase(PhoneCallUseCaseImpl phoneCallUseCase) {
    return phoneCallUseCase;
  }

  @Provides
  SmsUseCase provideSmsUseCase(SmsUseCaseImpl smsUseCase) {
    return smsUseCase;
  }
}
