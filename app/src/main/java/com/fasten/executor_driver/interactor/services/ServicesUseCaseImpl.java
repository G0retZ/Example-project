package com.fasten.executor_driver.interactor.services;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;

public class ServicesUseCaseImpl implements ServicesUseCase {

  @NonNull
  private final ServicesGateway gateway;

  @Inject
  ServicesUseCaseImpl(@NonNull ServicesGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Observable<List<Service>> getVehicleOptions() {
    return gateway.getServices().toObservable();
  }

  @Override
  public Completable setSelectedVehicleOptions(List<Service> services) {
    return gateway.sendSelectedServices(services);
  }
}
