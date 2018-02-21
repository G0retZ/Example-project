package com.fasten.executor_driver.di;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.backend.web.TokenKeeper;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicle;
import com.fasten.executor_driver.backend.web.incoming.ApiVehicleOptionItem;
import com.fasten.executor_driver.entity.Vehicle;
import com.fasten.executor_driver.entity.VehicleOption;
import com.fasten.executor_driver.gateway.ErrorMapper;
import com.fasten.executor_driver.gateway.HeatMapGatewayImpl;
import com.fasten.executor_driver.gateway.Mapper;
import com.fasten.executor_driver.gateway.PasswordGatewayImpl;
import com.fasten.executor_driver.gateway.SmsGatewayImpl;
import com.fasten.executor_driver.gateway.TokenKeeperImpl;
import com.fasten.executor_driver.gateway.VehicleApiMapper;
import com.fasten.executor_driver.gateway.VehicleOptionApiMapper;
import com.fasten.executor_driver.gateway.VehicleOptionsGatewayImpl;
import com.fasten.executor_driver.gateway.VehiclesGatewayImpl;
import com.fasten.executor_driver.interactor.auth.PasswordGateway;
import com.fasten.executor_driver.interactor.auth.SmsGateway;
import com.fasten.executor_driver.interactor.map.HeatMapGateway;
import com.fasten.executor_driver.interactor.vehicle.VehicleOptionsGateway;
import com.fasten.executor_driver.interactor.vehicle.VehiclesGateway;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;

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
  HeatMapGateway provideHeatMapGateway(HeatMapGatewayImpl heatMapGateway) {
    return heatMapGateway;
  }

  @Provides
  VehiclesGateway provideVehicleChoiceGateway(VehiclesGatewayImpl vehicleChoiceGateway) {
    return vehicleChoiceGateway;
  }

  @Provides
  VehicleOptionsGateway provideVehicleOptionsGateway(
      VehicleOptionsGatewayImpl vehicleOptionsGateway) {
    return vehicleOptionsGateway;
  }

  /* Преобразователи */

  @Provides
  @Named("vehicleOptionMapper")
  Mapper<ApiVehicleOptionItem, VehicleOption> provideVehicleOptionApiMapper(
      VehicleOptionApiMapper vehicleOptionApiMapper) {
    return vehicleOptionApiMapper;
  }

  @Provides
  @Named("vehicleMapper")
  Mapper<ApiVehicle, Vehicle> provideVehicleApiMapper(VehicleApiMapper vehicleApiMapper) {
    return vehicleApiMapper;
  }

  @Provides
  @Named("errorMapper")
  Mapper<Throwable, Throwable> provideErrorMapper(ErrorMapper errorMapper) {
    return errorMapper;
  }
}
