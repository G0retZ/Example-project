package com.fasten.executor_driver.di;

import com.fasten.executor_driver.entity.CodeExtractor;
import com.fasten.executor_driver.entity.LoginValidator;
import com.fasten.executor_driver.entity.PasswordValidator;
import com.fasten.executor_driver.entity.PhoneNumberValidator;
import com.fasten.executor_driver.entity.SmsCodeExtractor;
import com.fasten.executor_driver.entity.Validator;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module
class EntityModule {

  @Provides
  @Named("loginValidator")
  Validator<String> provideLoginValidator(LoginValidator loginValidator) {
    return loginValidator;
  }

  @Provides
  @Named("passwordValidator")
  Validator<String> providePasswordValidator(PasswordValidator passwordValidator) {
    return passwordValidator;
  }

  @Provides
  @Named("phoneNumberValidator")
  Validator<String> providePhoneNumberValidator(PhoneNumberValidator phoneNumberValidator) {
    return phoneNumberValidator;
  }

  @Provides
  @Named("smsExtractor")
  CodeExtractor provideSmsCodeExtractor(SmsCodeExtractor smsCodeExtractor) {
    return smsCodeExtractor;
  }
}
