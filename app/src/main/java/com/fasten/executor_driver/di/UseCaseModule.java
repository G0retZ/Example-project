package com.fasten.executor_driver.di;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Vehicle;
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
import com.fasten.executor_driver.interactor.services.ServicesUseCase;
import com.fasten.executor_driver.interactor.services.ServicesUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.LastUsedVehicleSharer;
import com.fasten.executor_driver.interactor.vehicle.SelectedVehicleUseCase;
import com.fasten.executor_driver.interactor.vehicle.SelectedVehicleUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceSharer;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCase;
import com.fasten.executor_driver.interactor.vehicle.VehicleChoiceUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsUseCase;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsUseCaseImpl;
import com.fasten.executor_driver.interactor.vehicle.VehiclesSharer;
import com.fasten.executor_driver.interactor.vehicle.VehiclesUseCase;
import com.fasten.executor_driver.interactor.vehicle.VehiclesUseCaseImpl;
import dagger.Module;
import dagger.Provides;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;

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
  VehiclesUseCase provideOnlineUseCase(VehiclesUseCaseImpl onlineUseCase) {
    return onlineUseCase;
  }

  @Provides
  VehicleChoiceUseCase provideVehicleChoiceUseCase(VehicleChoiceUseCaseImpl vehicleChoiceUseCase) {
    return vehicleChoiceUseCase;
  }

  @Provides
  VehicleOptionsUseCase provideVehicleOptionsUseCase(
      VehicleOptionsUseCaseImpl vehicleOptionsUseCase) {
    return vehicleOptionsUseCase;
  }

  @Provides
  SelectedVehicleUseCase provideSelectedVehicleUseCase(
      SelectedVehicleUseCaseImpl selectedVehicleUseCase) {
    return selectedVehicleUseCase;
  }

  @Provides
  ServicesUseCase provideServicesUseCase(ServicesUseCaseImpl servicesUseCase) {
    return servicesUseCase;
  }

  /* Обмен данными между юзкейсами */

  @Provides
  @Singleton
  @Named("loginSharer")
  DataSharer<String> provideLoginSharer(@NonNull LoginSharer loginSharer) {
    return loginSharer;
  }

  @Provides
  @Singleton
  @Named("vehiclesSharer")
  DataSharer<List<Vehicle>> provideVehiclesSharer(@NonNull VehiclesSharer vehiclesSharer) {
    return vehiclesSharer;
  }

  @Provides
  @Singleton
  @Named("vehicleChoiceSharer")
  DataSharer<Vehicle> provideVehicleChoiceSharer(@NonNull VehicleChoiceSharer vehicleChoiceSharer) {
    return vehicleChoiceSharer;
  }

  @Provides
  @Singleton
  @Named("lastUsedVehicleSharer")
  DataSharer<Vehicle> provideLastUsedVehiclesSharer(
      @NonNull LastUsedVehicleSharer lastUsedVehicleSharer) {
    return lastUsedVehicleSharer;
  }
}
