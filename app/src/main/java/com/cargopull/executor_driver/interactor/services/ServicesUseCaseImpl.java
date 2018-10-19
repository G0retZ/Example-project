package com.cargopull.executor_driver.interactor.services;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.EmptyListException;
import com.cargopull.executor_driver.entity.Service;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
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
    return gateway.getServices().observeOn(Schedulers.single()).map(services -> {
      if (services.isEmpty()) {
        throw new EmptyListException("Нет доступных услуг.");
      }
      return services;
    });
  }

  @Override
  public Completable setSelectedServices(List<Service> services) {
    return Single.fromCallable(() -> {
      List<Service> selectedServices = new ArrayList<>();
      for (Service service : services) {
        if (service.isSelected()) {
          selectedServices.add(service);
        }
      }
      if (selectedServices.isEmpty()) {
        throw new EmptyListException("Не выбрано услуг для on-line.");
      }
      return selectedServices;
    }).subscribeOn(Schedulers.single())
        .flatMapCompletable(gateway::sendSelectedServices)
        .observeOn(Schedulers.single());
  }
}
