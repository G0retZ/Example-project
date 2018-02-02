package com.fasten.executor_driver.di;

import android.arch.lifecycle.ViewModelProvider;
import com.fasten.executor_driver.presentation.ViewModelFactory;
import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module
class PresentationModule {

  @Provides
  CodeViewModel provideCodeViewModel(CodeViewModelImpl codeViewModel) {
    return codeViewModel;
  }

  @Provides
  PhoneViewModel providePhoneViewModel(PhoneViewModelImpl codeViewModel) {
    return codeViewModel;
  }

  @Provides
  SmsButtonViewModel provideTimeoutButtonViewModel(SmsButtonViewModelImpl buttonViewModel) {
    return buttonViewModel;
  }

  @Provides
  @Named("phone")
  ViewModelProvider.Factory providePhoneViewModelFactory(ViewModelFactory<PhoneViewModel> factory) {
    return factory;
  }

  @Provides
  @Named("code")
  ViewModelProvider.Factory provideCodeViewModelFactory(ViewModelFactory<CodeViewModel> factory) {
    return factory;
  }

  @Provides
  @Named("button")
  ViewModelProvider.Factory provideSmsButtonViewModelFactory(
      ViewModelFactory<SmsButtonViewModel> factory) {
    return factory;
  }
}
