package com.fasten.executor_driver.di;

import android.arch.lifecycle.ViewModelProvider;
import com.fasten.executor_driver.presentation.ViewModelFactory;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModelImpl;
import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.map.MapViewModel;
import com.fasten.executor_driver.presentation.map.MapViewModelImpl;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl;
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
  SmsButtonViewModel provideSmsButtonViewModel(SmsButtonViewModelImpl buttonViewModel) {
    return buttonViewModel;
  }

  @Provides
  MapViewModel provideMapViewModel(MapViewModelImpl mapViewModel) {
    return mapViewModel;
  }

  @Provides
  OnlineButtonViewModel provideOnlineButtonViewModel(
      OnlineButtonViewModelImpl onlineButtonViewModel) {
    return onlineButtonViewModel;
  }

  @Provides
  ChooseVehicleViewModel provideChooseVehicleViewModel(
      ChooseVehicleViewModelImpl chooseVehicleViewModel) {
    return chooseVehicleViewModel;
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

  @Provides
  @Named("map")
  ViewModelProvider.Factory provideMapViewModelFactory(
      ViewModelFactory<MapViewModel> factory) {
    return factory;
  }

  @Provides
  @Named("goOnline")
  ViewModelProvider.Factory provideOnlineButtonViewModelFactory(
      ViewModelFactory<OnlineButtonViewModel> factory) {
    return factory;
  }

  @Provides
  @Named("chooseVehicle")
  ViewModelProvider.Factory provideChooseVehicleViewModelFactory(
      ViewModelFactory<ChooseVehicleViewModel> factory) {
    return factory;
  }
}
