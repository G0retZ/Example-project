package com.fasten.executor_driver.di;

import android.arch.lifecycle.ViewModelProvider;
import com.fasten.executor_driver.presentation.ViewModelFactory;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModel;
import com.fasten.executor_driver.presentation.choosevehicle.ChooseVehicleViewModelImpl;
import com.fasten.executor_driver.presentation.code.CodeViewModel;
import com.fasten.executor_driver.presentation.code.CodeViewModelImpl;
import com.fasten.executor_driver.presentation.codeHeader.CodeHeaderViewModel;
import com.fasten.executor_driver.presentation.codeHeader.CodeHeaderViewModelImpl;
import com.fasten.executor_driver.presentation.map.MapViewModel;
import com.fasten.executor_driver.presentation.map.MapViewModelImpl;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModel;
import com.fasten.executor_driver.presentation.onlinebutton.OnlineButtonViewModelImpl;
import com.fasten.executor_driver.presentation.options.OptionsViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModel;
import com.fasten.executor_driver.presentation.phone.PhoneViewModelImpl;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModel;
import com.fasten.executor_driver.presentation.selectedvehicle.SelectedVehicleViewModelImpl;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModel;
import com.fasten.executor_driver.presentation.smsbutton.SmsButtonViewModelImpl;
import com.fasten.executor_driver.presentation.vehicleoptions.VehicleOptionsViewModelImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

@Module
class PresentationModule {

  @Provides
  PhoneViewModel providePhoneViewModel(PhoneViewModelImpl codeViewModel) {
    return codeViewModel;
  }

  @Provides
  CodeViewModel provideCodeViewModel(CodeViewModelImpl codeViewModel) {
    return codeViewModel;
  }

  @Provides
  CodeHeaderViewModel provideCodeHeaderViewModel(CodeHeaderViewModelImpl codeHeaderViewModel) {
    return codeHeaderViewModel;
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
  OptionsViewModel provideVehicleOptionsViewModel(
      VehicleOptionsViewModelImpl vehicleOptionsViewModel) {
    return vehicleOptionsViewModel;
  }

  @Provides
  SelectedVehicleViewModel provideSelectedVehicleViewModel(
      SelectedVehicleViewModelImpl selectedVehicleViewModel) {
    return selectedVehicleViewModel;
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
  @Named("codeHeader")
  ViewModelProvider.Factory provideCodeHeaderViewModelFactory(
      ViewModelFactory<CodeHeaderViewModel> factory) {
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

  @Provides
  @Named("vehicleOptions")
  ViewModelProvider.Factory provideVehicleOptionsViewModelFactory(
      ViewModelFactory<OptionsViewModel> factory) {
    return factory;
  }

  @Provides
  @Named("selectedVehicle")
  ViewModelProvider.Factory provideSelectedVehicleViewModelFactory(
      ViewModelFactory<SelectedVehicleViewModel> factory) {
    return factory;
  }
}
