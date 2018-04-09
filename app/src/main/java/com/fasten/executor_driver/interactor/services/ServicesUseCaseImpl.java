package com.fasten.executor_driver.interactor.services;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.NoServicesAvailableException;
import com.fasten.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServicesUseCaseImpl implements ServicesUseCase {

  @NonNull
  private final ServicesGateway gateway;

  @Inject
  public ServicesUseCaseImpl(@NonNull ServicesGateway gateway) {
    this.gateway = gateway;
  }

  @NonNull
  @Override
  public Single<List<Service>> loadServices() {
    return gateway.getServices().map(services -> {
      if (services.isEmpty()) {
        throw new NoServicesAvailableException();
      }
      return services;
    });
  }

  @Override
  public Completable setSelectedServices(List<Service> services) {
    List<Service> selectedServices = new ArrayList<>();
    for (Service service : services) {
      if (service.isSelected()) {
        selectedServices.add(service);
      }
    }
    if (selectedServices.isEmpty()) {
      return Completable.error(new NoServicesAvailableException());
    }
    return gateway.sendSelectedServices(selectedServices);
  }
}
